/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeNebulaCloud
 * Created by HellFirePvP
 * Date: 12.12.2020 / 16:27
 */
public class TypeNebulaCloud extends PatreonEffect implements ITickHandler {

    private final UUID playerUUID;

    public TypeNebulaCloud(UUID effectUUID, @Nullable FlareColor flareColor, UUID playerUUID) {
        super(effectUUID, flareColor);
        this.playerUUID = playerUUID;
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

        if (side.isClient() && shouldDoEffect(player)) {
            spawnCloudParticles(player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnCloudParticles(PlayerEntity player) {
        Vector3 playerPos = Vector3.atEntityCorner(player).addY(0.1F);

        for (int i = 0; i < 3; i++) {
            float oX = (rand.nextFloat() - rand.nextFloat()) * 2F;
            float oZ = (rand.nextFloat() - rand.nextFloat()) * 2F;
            Vector3 offset = new Vector3(oX, rand.nextFloat() * 0.1F, oZ);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(playerPos.clone().add(offset))
                    .setAlphaMultiplier(0.8F)
                    .alpha(((VFXAlphaFunction<EntityVisualFX>) (fx, alphaIn, pTicks) -> {
                        if (shouldDoEffect(player) && Minecraft.getInstance().gameSettings.getPointOfView().func_243192_a()) {
                            if (player.rotationPitch > 40) {
                                return MathHelper.clamp(1F - (player.rotationPitch - 40F) / 20F, 0, 1F) * alphaIn;
                            }
                        }
                        return alphaIn;
                    }).andThen(VFXAlphaFunction.FADE_OUT))
                    .color(VFXColorFunction.WHITE)
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.3F)
                    .setMaxAge(40 + rand.nextInt(20));
        }

        if (rand.nextInt(16) == 0) {
            Vector3 from = Vector3.random().setY(0).normalize().multiply(rand.nextFloat() * 2F).addY(rand.nextFloat() * 0.1F);
            Vector3 to   = Vector3.random().setY(0).normalize().multiply(rand.nextFloat() * 2F).addY(rand.nextFloat() * 0.1F);

            EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                    .spawn(playerPos.clone().add(from))
                    .makeDefault(playerPos.clone().add(to))
                    .color(VFXColorFunction.WHITE)
                    .alpha((fx, alphaIn, pTicks) -> {
                        if (shouldDoEffect(player) && Minecraft.getInstance().gameSettings.getPointOfView().func_243192_a()) {
                            if (player.rotationPitch > 40) {
                                return MathHelper.clamp(1F - (Math.abs(player.rotationPitch) - 40F) / 20F, 0, 1F) * alphaIn;
                            }
                        }
                        return alphaIn;
                    });
        }
    }

    private boolean shouldDoEffect(PlayerEntity player) {
        return player.getUniqueID().equals(playerUUID) &&
                (player.getPose() == Pose.STANDING || player.getPose() == Pose.CROUCHING) &&
                !player.isPotionActive(Effects.INVISIBILITY);
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
        return "PatreonEffect - Nebula Cloud " + this.playerUUID.toString();
    }
}
