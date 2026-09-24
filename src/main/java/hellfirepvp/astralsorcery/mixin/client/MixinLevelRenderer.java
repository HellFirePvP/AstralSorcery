/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.helper.ClientLinkHelper;
import hellfirepvp.astralsorcery.client.helper.FocalPointEffectHelper;
import hellfirepvp.astralsorcery.client.helper.GatewayInterfaceRenderHelper;
import hellfirepvp.astralsorcery.client.sky.AstralSkyRenderer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ClientHooks;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinLevelRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    @Shadow @Nullable private ClientLevel level;
    @Shadow @Final private RenderBuffers renderBuffers;
    @Shadow private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {}

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void specialSkyRenderer(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        AstralSkyRenderer renderer = AstralSkyRenderer.getInstance();
        if (this.level != null && renderer.shouldRenderSpecialSky(this.level)) {
            renderer.renderSky(this.level, frustumMatrix, projectionMatrix, partialTick, camera, isFoggy, skyFogSetup);
            ci.cancel();
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V"))
    public void renderStellarFilamentCustomHitRender(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (!renderBlockOutline) return;
        HitResult result = Minecraft.getInstance().hitResult;
        if (result instanceof BlockHitResult bhr && bhr.getType() == HitResult.Type.MISS && this.level != null) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            if (player.getMainHandItem().is(ItemsAS.BLOCK_STELLAR_FILAMENT) || player.getOffhandItem().is(ItemsAS.BLOCK_STELLAR_FILAMENT)) {
                if (FocalPointEffectHelper.isInFocalPointArea(bhr.getBlockPos())) {
                    BlockHitResult blockHit = new BlockHitResult(bhr.getLocation(), bhr.getDirection(), bhr.getBlockPos(), bhr.isInside());
                    MultiBufferSource.BufferSource buffers = this.renderBuffers.bufferSource();
                    PoseStack pose = new PoseStack(); //During actual rendering, this is also just identity.
                    if (!ClientHooks.onDrawHighlight(MiscUtil.cast(this), camera, blockHit, deltaTracker, pose, buffers)) {
                        if (this.level.getWorldBorder().isWithinBounds(bhr.getBlockPos())) {
                            BlockState placeable = BlocksAS.STELLAR_FILAMENT.get().defaultBlockState();
                            VertexConsumer buf = buffers.getBuffer(RenderType.lines());
                            Vec3 cameraPos = camera.getPosition();
                            ColorWrapper color = ColorWrapper.ofHSB((ClientProxy.getClientTick() % 100) / 100F, 1F, 1F).copyWithAlpha(0x66);
                            renderShape(pose,
                                    buf,
                                    placeable.getShape(this.level, bhr.getBlockPos(), CollisionContext.of(camera.getEntity())),
                                    (double) bhr.getBlockPos().getX() - cameraPos.x(),
                                    (double) bhr.getBlockPos().getY() - cameraPos.y(),
                                    (double) bhr.getBlockPos().getZ() - cameraPos.z(),
                                    color.getRed() / 255F,
                                    color.getGreen() / 255F,
                                    color.getBlue() / 255F,
                                    color.getAlpha() / 255F);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V"))
    public void renderLinkSessionSourceOutline(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (!renderBlockOutline || this.level == null) return;
        ClientLinkHelper.getActiveSession().ifPresent(session -> {
            PoseStack pose = new PoseStack();
            MultiBufferSource.BufferSource buffers = this.renderBuffers.bufferSource();
            VertexConsumer buf = buffers.getBuffer(RenderType.lines());
            Vec3 cameraPos = camera.getPosition();
            ColorWrapper color = ColorWrapper.ofHSB((ClientProxy.getClientTick() % 100) / 100F, 1F, 1F).copyWithAlpha(0x66);
            session.selected().ifPos(blockPos -> {
                BlockState linkable = this.level.getBlockState(blockPos);
                if (!linkable.isAir()) {
                    renderShape(pose,
                            buf,
                            linkable.getShape(this.level, blockPos, CollisionContext.of(camera.getEntity())),
                            (double) blockPos.getX() - cameraPos.x(),
                            (double) blockPos.getY() - cameraPos.y(),
                            (double) blockPos.getZ() - cameraPos.z(),
                            color.getRed() / 255F,
                            color.getGreen() / 255F,
                            color.getBlue() / 255F,
                            color.getAlpha() / 255F);
                }
            });
            session.selected().ifEntity(entityId -> {
                Entity entity = this.level.getEntity(entityId);
                if (entity == null) return;
                VoxelShape shape = Shapes.create(entity.getBoundingBox());
                renderShape(pose, buf, shape,
                        entity.getX() - cameraPos.x(),
                        entity.getY() - cameraPos.y(),
                        entity.getZ() - cameraPos.z(),
                        color.getRed() / 255F,
                        color.getGreen() / 255F,
                        color.getBlue() / 255F,
                        color.getAlpha() / 255F);
            });
        });
    }
}
