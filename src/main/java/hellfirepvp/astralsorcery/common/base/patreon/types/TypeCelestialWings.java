/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.render.ObjModelRender;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeCelestialWings
 * Created by HellFirePvP
 * Date: 04.04.2020 / 20:56
 */
public class TypeCelestialWings extends PatreonEffect implements ITickHandler {

    private final UUID playerUUID;

    public TypeCelestialWings(UUID effectUUID, @Nullable FlareColor flareColor, UUID playerUUID) {
        super(effectUUID, flareColor);
        this.playerUUID = playerUUID;
    }

    @Override
    public void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.register(this);
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
                (Minecraft.getInstance().player != null &&
                        Minecraft.getInstance().player.getUniqueID().equals(playerUUID) &&
                        Minecraft.getInstance().gameSettings.thirdPersonView != 0)) {
            playEffects(player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playEffects(PlayerEntity player) {
        float rot = RenderingVectorUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, 0);
        float yOffset = 1.3F;
        if (player.isSneaking()) {
            yOffset = 1F;
        }
        float f = Math.abs((ClientScheduler.getSystemClientTick() % 240) - 120F) / 120F;
        double offset = Math.cos(f * 2 * Math.PI) * 0.03;

        Vector3 look = new Vector3(1, 0, 0).rotate(Math.toRadians(360F - rot), Vector3.RotAxis.Y_AXIS).normalize();
        Vector3 pos = Vector3.atEntityCorner(player);
        pos.setY(player.getPosY() + yOffset + offset);

        for (int i = 0; i < 4; i++) {
            double height = -0.1 + Math.min(rand.nextFloat() * 1.3, rand.nextFloat() * 1.3);
            double distance = 1.2F - (rand.nextFloat() * 0.6) * (1 - Math.max(0, height));

            Vector3 dir = look.clone().rotate(Math.toRadians(180 * (rand.nextBoolean() ? 1 : 0)), Vector3.RotAxis.Y_AXIS)
                    .normalize()
                    .multiply(distance);

            Vector3 at = pos.clone().addY(height).add(dir);

            Color col = Color.getHSBColor(0.68F, 1, 0.6F - rand.nextFloat() * 0.5F);

            FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(VFXColorFunction.constant(col))
                    .setScaleMultiplier(0.27F + rand.nextFloat() * 0.1F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setMaxAge(25 + rand.nextInt(20));

            if (rand.nextInt(4) == 0) {
                p.setScaleMultiplier(0.09F + rand.nextFloat() * 0.02F)
                        .color(VFXColorFunction.WHITE)
                        .setMaxAge(10 + rand.nextInt(8));
            } else {
                p.setGravityStrength(0.0003F);
            }
        }
    }

    private boolean shouldDoEffect(PlayerEntity player) {
        return player.getUniqueID().equals(playerUUID) &&
                !player.isPassenger() &&
                !player.isElytraFlying() &&
                !player.isPotionActive(Effects.INVISIBILITY);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    void onRender(RenderPlayerEvent.Post event) {
        PlayerEntity player = event.getPlayer();
        if (!shouldDoEffect(player)) {
            return;
        }
        this.renderWings(player, event.getMatrixStack(), event.getPartialRenderTick());
    }

    private void renderWings(PlayerEntity player, MatrixStack renderStack, float pTicks) {
        float rot = RenderingVectorUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, pTicks);
        float yOffset = 1.3F;
        if (player.isSneaking() && !player.abilities.isFlying) {
            yOffset = 1F;
        }
        float f = Math.abs((ClientScheduler.getSystemClientTick() % 240) - 120F) / 120F;
        double offset = Math.cos(f * 2 * Math.PI) * 0.03;

        renderStack.push();
        renderStack.translate(0, yOffset + offset, 0);
        renderStack.rotate(Vector3f.YP.rotationDegrees(180F - rot));
        renderStack.scale(0.02F, 0.02F, 0.02F);

        RenderTypesAS.MODEL_CELESTIAL_WINGS.setupRenderState();

        renderStack.translate(-25, 0, 0);
        ObjModelRender.renderCelestialWings(renderStack);
        renderStack.rotate(Vector3f.YP.rotationDegrees(180F));
        renderStack.translate(-50, 0, 0);
        ObjModelRender.renderCelestialWings(renderStack);
        renderStack.pop();

        RenderTypesAS.MODEL_CELESTIAL_WINGS.clearRenderState();
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
        return "PatreonEffect - Celestial Wings " + this.playerUUID.toString();
    }
}
