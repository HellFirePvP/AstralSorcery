/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.render.ObjModelRender;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeWraithWings
 * Created by HellFirePvP
 * Date: 04.03.2020 / 23:06
 */
public class TypeWraithWings extends PatreonEffect {

    private final UUID playerUUID;

    public TypeWraithWings(UUID effectUUID, @Nullable FlareColor flareColor, UUID playerUUID) {
        super(effectUUID, flareColor);
        this.playerUUID = playerUUID;
    }

    @Override
    public void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.register(this);
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
        MatrixStack renderStack = event.getMatrixStack();

        float rot = RenderingVectorUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, event.getPartialRenderTick());

        float yOffset = 1.2F;
        if (player.isSneaking() && !player.abilities.isFlying) {
            yOffset = 1F;
        }

        renderStack.push();

        float swimAngle = player.getSwimAnimation(event.getPartialRenderTick());
        if (swimAngle > 0) {
            float waterPitch = player.isInWater() ? -90.0F - player.rotationPitch : -90.0F;
            float bodySwimAngle = MathHelper.lerp(swimAngle, 0.0F, waterPitch);
            renderStack.rotate(Vector3f.YP.rotationDegrees(180 - rot));
            renderStack.rotate(Vector3f.XP.rotationDegrees(bodySwimAngle));
            if (player.isActualySwimming()) {
                renderStack.translate(0, -1, 0.3);
            }
        } else {
            renderStack.rotate(Vector3f.YP.rotationDegrees(180 - rot));
        }

        renderStack.translate(0, yOffset, 0);
        renderStack.scale(0.32F, 0.32F, 0.32F);

        RenderTypesAS.MODEL_WRAITH_WINGS.setupRenderState();

        renderStack.push();
        renderStack.translate(-2.3, 0, 0.8);
        renderStack.rotate(Vector3f.YP.rotationDegrees(10));
        ObjModelRender.renderWraithWings(renderStack);
        renderStack.pop();

        renderStack.push();
        renderStack.rotate(Vector3f.YP.rotationDegrees(180));
        renderStack.translate(-2.3, 0, -0.8);
        renderStack.rotate(Vector3f.YN.rotationDegrees(10));
        ObjModelRender.renderWraithWings(renderStack);
        renderStack.pop();

        RenderTypesAS.MODEL_WRAITH_WINGS.clearRenderState();

        renderStack.pop();
    }
}
