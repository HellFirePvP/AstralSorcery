/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Quaternionf;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GatewayInterfaceRenderHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GatewayInterfaceRenderHelper {

    private static final GatewayInterfaceRenderHelper INSTANCE = new GatewayInterfaceRenderHelper();
    private GatewayUserInterface currentUI = null;

    private GatewayInterfaceRenderHelper() {}

    public static GatewayInterfaceRenderHelper getInstance() {
        return INSTANCE;
    }

    public void tryCreateUI(Level level, BlockPos pos, Vector3 renderPos, float sphereRadius) {
        if (this.currentUI == null ||
                !this.currentUI.getLevelKey().equals(level.dimension()) ||
                !this.currentUI.getPos().equals(pos) ||
                !this.currentUI.getRenderPos().equals(renderPos)) {
            this.currentUI = GatewayUserInterface.create(level, pos, renderPos, sphereRadius);
        }

        if (this.currentUI != null) {
            this.currentUI.refreshView();
        }
    }

    public Optional<GatewayUserInterface> getCurrentUI() {
        return Optional.ofNullable(this.currentUI);
    }

    private void validateCurrentUI() {
        if (this.currentUI == null) return;

        Level level = Minecraft.getInstance().level;
        if (level == null) {
            this.currentUI = null;
            return;
        }
        if (!this.currentUI.isViewVisible() || !this.currentUI.getLevelKey().equals(level.dimension())) {
            this.currentUI = null;
            return;
        }
        TileCelestialGateway gateway = MiscUtil.getTileAt(level, this.currentUI.getPos(), TileCelestialGateway.class, true).orElse(null);
        if (gateway == null || !gateway.doesSeeSky() || !gateway.hasStructure()) {
            this.currentUI = null;
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        GatewayInterfaceRenderHelper.getInstance().getCurrentUI().ifPresent(GatewayUserInterface::tick);
    }

    public void renderCurrentUI(ClientLevel level, PoseStack poseStack, Camera camera, MultiBufferSource.BufferSource buffers, float pTicks) {
        this.validateCurrentUI();
        if (this.currentUI == null) return;

        Vector3 uiPos = this.currentUI.getRenderPos();
        Vector3 cameraPos = new Vector3(camera.getPosition());
        float cameraDistance = (float) uiPos.distance(cameraPos);
        if (cameraDistance > 2.5F) return;
        float alpha = 1F - ((cameraDistance - 0.5F) / 2F); //shorter cutoff, to have more full-opacity on the block itself
        alpha = Mth.clamp(alpha, 0, 1F);

        this.renderInterfaceStars(poseStack, camera, buffers, alpha, pTicks);
        this.renderFocusedEntryDetails(poseStack, camera, buffers, alpha, pTicks);
    }

    private void renderInterfaceStars(PoseStack stack, Camera camera, MultiBufferSource.BufferSource buffers, float distanceAlpha, float pTicks) {
        long seed = MiscUtil.getBlockPosSeed(this.currentUI.getPos());
        RandomSource rand = RandomSource.create(seed);
        Vector3 renderOffset = this.currentUI.getRenderPos();
        Quaternionf facing = new Quaternionf();
        facing.set(camera.rotation());

        VertexConsumer vb = buffers.getBuffer(RenderTypesAS.GATEWAY_UI_STAR);
        for (int i = 0; i < 300; i++) {
            Vector3 at = Vector3.random(rand).normalize()
                    .multiply(this.currentUI.getSphereRadius() * 0.98F)
                    .add(renderOffset);
            if (at.getY() >= this.currentUI.getPos().getY() - 1) {
                float flickerSpeed = 0.05F + rand.nextFloat() * 0.05F;
                float alpha = EffectUtil.flicker(flickerSpeed, pTicks);
                alpha *= distanceAlpha;
                int alphaI = Mth.clamp(Mth.floor(alpha * 255F), 0, 255);

                RenderingDrawUtil.renderFacingQuad(vb, facing, at.subtract(camera.getPosition()), ColorWrapper.WHITE.copyWithAlpha(alphaI),
                        0.05F + rand.nextFloat() * 0.04F, LightmapUtil.getPackedFullbrightCoords(), UVFrame.FULL);
            }
        }

        for (GatewayUserInterface.GatewayTarget target : this.currentUI.getGatewayTargets()) {
            DyeColor color = target.getEntry().getColor();
            if (color == DyeColor.BLACK) color = DyeColor.GRAY;
            ColorWrapper starColor = ColorsAS.DYE_COLORS[color.getId()];

            Vector3 at = target.getRelativePos().copy().multiply(0.95F)
                    .add(renderOffset)
                    .subtract(camera.getPosition());

            float flickerSpeed = 0.03F + rand.nextFloat() * 0.03F;
            float alpha = EffectUtil.flicker(flickerSpeed, pTicks);
            alpha = 0.5F + (alpha * 0.5F);
            alpha *= distanceAlpha;
            int alphaI = Mth.clamp(Mth.floor(alpha * 255F), 0, 255);

            RenderingDrawUtil.renderFacingQuad(vb, facing, at, starColor.copyWithAlpha(alphaI),
                    0.2F, LightmapUtil.getPackedFullbrightCoords(), UVFrame.FULL);
        }
        buffers.endBatch(RenderTypesAS.GATEWAY_UI_STAR);
    }

    private void renderFocusedEntryDetails(PoseStack poseStack, Camera camera, MultiBufferSource.BufferSource buffers, float alpha, float pTicks) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!MiscUtil.getTileExists(Minecraft.getInstance().level, player.blockPosition(), TileCelestialGateway.class, true)) return;

        this.findMatching(camera.getYRot(), camera.getXRot()).ifPresent(target -> {
            Optional.ofNullable(target.getEntry().getCustomName()).ifPresent(displayName -> {
                Vector3 at = target.getRelativePos().copy().multiply(0.95F)
                        .add(this.currentUI.getRenderPos())
                        .addY(0.4F)
                        .subtract(camera.getPosition());

                DyeColor color = target.getEntry().getColor();
                if (color == DyeColor.BLACK) color = DyeColor.GRAY;
                ColorWrapper starColor = ColorsAS.DYE_COLORS[color.getId()];
                int alphaI = Mth.clamp(Mth.floor(alpha * 0xCC), 0, 255);

                poseStack.pushPose();
                poseStack.translate(at.getX(), at.getY(), at.getZ());
                RenderingDrawUtil.drawFacingTextInWorld(displayName, poseStack, buffers, camera, starColor.copyWithAlpha(alphaI));
                poseStack.popPose();
            });
        });
    }

    public Optional<GatewayUserInterface.GatewayTarget> findMatching(float yaw, float pitch) {
        yaw = Mth.wrapDegrees(yaw);
        pitch = Mth.wrapDegrees(pitch);

        float matchAccuracy = 4;
        for (GatewayUserInterface.GatewayTarget target : this.currentUI.getGatewayTargets()) {
            if(Math.abs(target.getPitch() - pitch) < matchAccuracy &&
                    (Math.abs(target.getYaw() - yaw) <= matchAccuracy || Math.abs(target.getYaw() - yaw - 360F) <= matchAccuracy)) {
                return Optional.of(target);
            }
        }
        return Optional.empty();
    }
}
