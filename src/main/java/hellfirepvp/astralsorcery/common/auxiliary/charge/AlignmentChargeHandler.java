/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.charge;

import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncCharge;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyChargeBalancing;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AlignmentChargeHandler
 * Created by HellFirePvP
 * Date: 08.03.2020 / 15:48
 */
public class AlignmentChargeHandler implements ITickHandler {

    public static final AlignmentChargeHandler INSTANCE = new AlignmentChargeHandler();
    private static final float MAX_CHARGE = 1000F;

    private static final Map<LogicalSide, Map<UUID, Float>> maximumCharge = new HashMap<>();
    private static final Map<LogicalSide, Map<UUID, Float>> currentCharge = new HashMap<>();

    private AlignmentChargeHandler() {}

    public void updateMaximum(PlayerEntity player, LogicalSide side) {
        float cap = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, ResearchHelper.getProgress(player, side), PerkAttributeTypesAS.ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM, MAX_CHARGE);
        cap = AttributeEvent.postProcessModded(player, PerkAttributeTypesAS.ATTR_TYPE_ALIGNMENT_CHARGE_MAXIMUM, cap);

        maximumCharge.computeIfAbsent(side, s -> new HashMap<>()).put(player.getUniqueID(), cap);
    }

    public float getMaximumCharge(PlayerEntity player, LogicalSide side) {
        return maximumCharge.computeIfAbsent(side, s -> new HashMap<>())
                .computeIfAbsent(player.getUniqueID(), uuid -> MAX_CHARGE);
    }

    public float getCurrentCharge(PlayerEntity player, LogicalSide side) {
        if (player.isCreative()) {
            return getMaximumCharge(player, side);
        }
        return currentCharge.computeIfAbsent(side, s -> new HashMap<>())
                .computeIfAbsent(player.getUniqueID(), uuid -> MAX_CHARGE);
    }

    public float getFilledPercentage(PlayerEntity player, LogicalSide side) {
        if (player.isCreative()) {
            return 1F;
        }
        float max = this.getMaximumCharge(player, side);
        float current = this.getCurrentCharge(player, side);
        return MathHelper.clamp(current / max, 0F, 1F);
    }

    public boolean hasCharge(PlayerEntity player, LogicalSide side, float charge) {
        if (player.isCreative()) {
            return true;
        }
        float current = this.getCurrentCharge(player, side);
        return current >= charge;
    }

    public boolean drainCharge(PlayerEntity player, LogicalSide side, float charge, boolean simulate) {
        if (player.isCreative()) {
            return true;
        }
        if (!this.hasCharge(player, side, charge)) {
            return false;
        }
        float current = this.getCurrentCharge(player, side);
        float result = current - charge;
        if (result < 0) {
            return false;
        }
        if (!simulate) {
            currentCharge.computeIfAbsent(side, s -> new HashMap<>())
                    .put(player.getUniqueID(), Math.min(result, this.getMaximumCharge(player, side)));
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void receiveCharge(PktSyncCharge pkt, PlayerEntity player) {
        maximumCharge.computeIfAbsent(LogicalSide.CLIENT, s -> new HashMap<>()).put(player.getUniqueID(), pkt.getMaxCharge());
        currentCharge.computeIfAbsent(LogicalSide.CLIENT, s -> new HashMap<>()).put(player.getUniqueID(), pkt.getCharge());
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        PlayerEntity player = (PlayerEntity) context[0];
        LogicalSide side = (LogicalSide) context[1];

        float charge = this.getCurrentCharge(player, side);
        float max = this.getMaximumCharge(player, side);
        if (charge >= max) {
            return;
        }
        PlayerProgress progress = ResearchHelper.getProgress(player, side);

        float regenPerTick = max / (10F * 20F);

        boolean underground = player.getEntityWorld().getHeight(Heightmap.Type.WORLD_SURFACE, player.getPosition()).getY() > player.getPosition().getY() + 1;

        float dayMultiplier = underground ? 0.85F : 0.3F + 0.7F * DayTimeHelper.getCurrentDaytimeDistribution(player.getEntityWorld());
        float caveMultiplier = underground ? 0.25F : 1F;
        if (progress.hasPerkEffect(p -> p instanceof KeyChargeBalancing)) {
            dayMultiplier = 0.6F + dayMultiplier * 0.4F;
            caveMultiplier = 0.6F + caveMultiplier * 0.4F;
        }

        regenPerTick *= dayMultiplier;
        regenPerTick *= caveMultiplier;

        regenPerTick = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, progress, PerkAttributeTypesAS.ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION, regenPerTick);
        regenPerTick = AttributeEvent.postProcessModded(player, PerkAttributeTypesAS.ATTR_TYPE_ALIGNMENT_CHARGE_REGENERATION, regenPerTick);

        charge += regenPerTick;
        currentCharge.computeIfAbsent(side, s -> new HashMap<>()).put(player.getUniqueID(), Math.min(charge, max));

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncCharge(player));
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Alignment Charge Handler";
    }
}
