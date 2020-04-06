/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeCrystalFootprints
 * Created by HellFirePvP
 * Date: 05.04.2020 / 10:26
 */
public class TypeCrystalFootprints extends PatreonEffect implements ITickHandler {

    private final UUID playerUUID;
    private final Color color;

    public TypeCrystalFootprints(UUID effectUUID, @Nullable FlareColor flareColor, UUID playerUUID, Color color) {
        super(effectUUID, flareColor);
        this.playerUUID = playerUUID;
        this.color = color;
    }

    @Override
    public void attachTickListeners(Consumer<ITickHandler> registrar) {
        super.attachTickListeners(registrar);

        registrar.accept(this);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        PlayerEntity player = (PlayerEntity) context[0];
        LogicalSide side = (LogicalSide) context[1];

        if (side.isClient() &&
                shouldDoEffect(player) &&
                rand.nextInt(3) == 0) {

            spawnFootprint(player);
        }
    }

    private boolean shouldDoEffect(PlayerEntity player) {
        return player.getUniqueID().equals(playerUUID) &&
                !player.isPotionActive(Effects.INVISIBILITY) &&
                player.onGround;
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnFootprint(PlayerEntity player) {
        Vector3 pos = Vector3.atEntityCorner(player)
                .subtract(player.getWidth() / 2, 0.1, player.getWidth() / 2)
                .add(player.getWidth() * rand.nextFloat(), 0, player.getWidth() * rand.nextFloat());

        if (player.getEntityWorld().isAirBlock(pos.toBlockPos())) {
            return;
        }

        EffectHelper.of(EffectTemplatesAS.CRYSTAL)
                .spawn(pos)
                .rotation(rand.nextFloat() * 35F * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 35F * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 35F * (rand.nextBoolean() ? 1 : -1))
                .color(VFXColorFunction.constant(this.color))
                .alpha(VFXAlphaFunction.FADE_OUT)
                .setScaleMultiplier(0.025F + rand.nextFloat() * 0.03F)
                .setMaxAge(60 + rand.nextInt(30));
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
        return "PatreonEffect - Crystal Footprints " + this.playerUUID.toString();
    }
}
