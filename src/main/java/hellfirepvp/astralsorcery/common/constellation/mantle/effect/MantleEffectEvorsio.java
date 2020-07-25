/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectEvorsio
 * Created by HellFirePvP
 * Date: 31.03.2020 / 17:31
 */
public class MantleEffectEvorsio extends MantleEffect {

    public static EvorsioConfig CONFIG = new EvorsioConfig();

    public MantleEffectEvorsio() {
        super(ConstellationsAS.evorsio);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(EventPriority.LOWEST, this::onBreak);
    }

    private void onBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if (ItemMantle.getEffect(player, ConstellationsAS.evorsio) != null) {
            LogicalSide side = player.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER;
            if (side.isServer()) {
                float charge = Math.min(AlignmentChargeHandler.INSTANCE.getCurrentCharge(player, side), CONFIG.chargeCostPerBreak.get());
                AlignmentChargeHandler.INSTANCE.drainCharge(player, side, charge, false);
            }
        }
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        this.playCapeSparkles(player, 0.1F);
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static class EvorsioConfig extends Config {

        private final int defaultChargeCostPerBreak = 2;

        public ForgeConfigSpec.IntValue chargeCostPerBreak;

        public EvorsioConfig() {
            super("evorsio");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.chargeCostPerBreak = cfgBuilder
                    .comment("Set the amount alignment charge consumed per block break enhanced by the mantle effect")
                    .translation(translationKey("chargeCostPerBreak"))
                    .defineInRange("chargeCostPerBreak", this.defaultChargeCostPerBreak, 0, 1000);
        }
    }
}
