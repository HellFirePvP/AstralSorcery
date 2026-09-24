/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderingDrawUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderingDrawUtil {

    private static final PoseStack IDENTITY_POSE = new PoseStack();

    public static void renderFloatingItem(ItemRenderer renderer, Level level, long seed, float scale, ItemStack stack, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        long tick = ClientProxy.getClientTick() + seed;
        poseStack.translate(0, Mth.sin(tick / 30F) * 0.06F, 0);
        poseStack.scale(scale, scale, scale);

        float spin = ((float) tick + partialTick) / 30.0F;
        poseStack.mulPose(Axis.YP.rotation(spin));

        renderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                level,
                (int) seed);
        poseStack.popPose();

        RenderUtil.finishDrawing(bufferSource);
    }

    public static void drawFacingTextInWorld(Component text, PoseStack pose, MultiBufferSource buffers, Camera camera, ColorWrapper color) {
        Font font = Minecraft.getInstance().font;
        float textOffset = -font.width(text) / 2F;

        pose.pushPose();
        pose.mulPose(camera.rotation());
        pose.scale(0.025F, -0.025F, 0.025F);
        Matrix4f matr = pose.last().pose();

        font.drawInBatch(text, textOffset, 0, color.getColor(), false,
                matr, buffers, Font.DisplayMode.NORMAL,
                0, LightmapUtil.getPackedFullbrightCoords());

        pose.popPose();
    }

    public static void drawTexturedRectColor(PoseStack poseStack, AbstractRenderTexture texture, ColorWrapper color, ScreenRectangle rectangle) {
        drawTexturedRectColor(poseStack, texture, color, rectangle.position().x(), rectangle.position().y(), rectangle.width(), rectangle.height());
    }

    public static void drawTexturedRectColor(PoseStack poseStack, AbstractRenderTexture texture, ColorWrapper color, float x, float y, float width, float height) {
        drawTexturedRectColor(poseStack, texture, color, x, y, width, height, UVFrame.FULL);
    }

    public static void drawTexturedRectColor(PoseStack poseStack, AbstractRenderTexture texture, ColorWrapper color, float x, float y, float width, float height, UVFrame uv) {
        PoseStack.Pose pose = poseStack.last();
        texture.bindTexture();
        float u0 = uv.u(), v0 = uv.v();
        float u1 = uv.u() + uv.uWidth(), v1 = uv.v() + uv.vHeight();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            buf.addVertex(pose, x,         y, 0)         .setUv(u0, v0).setColor(color.getColor());
            buf.addVertex(pose, x,         y + height, 0).setUv(u0, v1).setColor(color.getColor());
            buf.addVertex(pose, x + width, y + height, 0).setUv(u1, v1).setColor(color.getColor());
            buf.addVertex(pose, x + width, y, 0)         .setUv(u1, v0).setColor(color.getColor());
        });
    }

    public static void drawTexturedRect(PoseStack poseStack, AbstractRenderTexture texture, ScreenRectangle rectangle) {
        drawTexturedRect(poseStack, texture, rectangle.position().x(), rectangle.position().y(), rectangle.width(), rectangle.height());
    }

    public static void drawTexturedRect(PoseStack poseStack, AbstractRenderTexture texture, float x, float y, float width, float height) {
        drawTexturedRect(poseStack, texture, x, y, width, height, UVFrame.FULL);
    }

    public static void drawTexturedRect(PoseStack poseStack, AbstractRenderTexture texture, float x, float y, float width, float height, UVFrame uv) {
        PoseStack.Pose pose = poseStack.last();
        texture.bindTexture();
        float u0 = uv.u(), v0 = uv.v();
        float u1 = uv.u() + uv.uWidth(), v1 = uv.v() + uv.vHeight();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, GameRenderer::getPositionTexShader, buf -> {
            buf.addVertex(pose, x,         y, 0)         .setUv(u0, v0);
            buf.addVertex(pose, x,         y + height, 0).setUv(u0, v1);
            buf.addVertex(pose, x + width, y + height, 0).setUv(u1, v1);
            buf.addVertex(pose, x + width, y, 0)         .setUv(u1, v0);
        });
    }

    public static void renderNormalQuad(VertexConsumer vb, PoseStack pose, Vector3 from, Vector3 vecU, Vector3 vecV, ColorWrapper color, int packedLight, int packedOverlay, UVFrame uv) {
        Matrix4f matr = pose.last().pose();

        from
                .drawPos(matr, vb)
                .setUv(uv.u(), uv.v())
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setLight(packedLight)
                .setOverlay(packedOverlay)
                .setNormal(0, 1, 0);

        from.copy().add(vecV)
                .drawPos(matr, vb)
                .setUv(uv.u(), uv.v() + uv.vHeight())
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setLight(packedLight)
                .setOverlay(packedOverlay)
                .setNormal(0, 1, 0);

        from.copy().add(vecU).add(vecV)
                .drawPos(matr, vb)
                .setUv(uv.u() + uv.uWidth(), uv.v() + uv.vHeight())
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setLight(packedLight)
                .setOverlay(packedOverlay)
                .setNormal(0, 1, 0);

        from.copy().add(vecU)
                .drawPos(matr, vb)
                .setUv(uv.u() + uv.uWidth(), uv.v())
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setLight(packedLight)
                .setOverlay(packedOverlay)
                .setNormal(0, 1, 0);
    }

    public static void renderFacingQuad(VertexConsumer vb, Quaternionf quat, Vector3 pos, ColorWrapper color, float scale, int packedLight, UVFrame uv) {
        renderFacingQuadPosed(vb, IDENTITY_POSE, quat, pos, color, scale, packedLight, uv.u(), uv.v(), uv.uWidth(), uv.vHeight());
    }

    public static void renderFacingQuad(VertexConsumer vb, Quaternionf quat, Vector3 pos, ColorWrapper color, float scale, int packedLight, float u, float v, float uLength, float vLength) {
        renderFacingQuadPosed(vb, IDENTITY_POSE, quat, pos, color, scale, packedLight, u, v, uLength, vLength);
    }

    public static void renderFacingQuadPosed(VertexConsumer vb, PoseStack poseStack, Quaternionf quat, Vector3 pos, ColorWrapper color, float scale, int packedLight, UVFrame uv) {
        renderFacingQuadPosed(vb, poseStack, quat, pos, color, scale, packedLight, uv.u(), uv.v(), uv.uWidth(), uv.vHeight());
    }

    public static void renderFacingQuadPosed(VertexConsumer vb, PoseStack poseStack, Quaternionf quat, Vector3 pos, ColorWrapper color, float scale, int packedLight, float u, float v, float uLength, float vLength) {
        Matrix4f pose = poseStack.last().pose();
        new Vector3(1, -1, 0).rotate(quat).multiply(scale).add(pos)
                .drawPos(pose, vb)
                .setUv(u + uLength, v + vLength)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setLight(packedLight);
        new Vector3(1, 1, 0).rotate(quat).multiply(scale).add(pos.getX(), pos.getY(), pos.getZ())
                .drawPos(pose, vb)
                .setUv(u + uLength, v)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setLight(packedLight);
        new Vector3(-1, 1, 0).rotate(quat).multiply(scale).add(pos.getX(), pos.getY(), pos.getZ())
                .drawPos(pose, vb)
                .setUv(u, v)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setLight(packedLight);
        new Vector3(-1, -1, 0).rotate(quat).multiply(scale).add(pos.getX(), pos.getY(), pos.getZ())
                .drawPos(pose, vb)
                .setUv(u, v + vLength)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setLight(packedLight);
    }

    public static void renderAngledTexturedQuad(VertexConsumer vb, PoseStack pose, Vector3 pos, Vector3 axis, float angle, float scale, UVFrame uv, ColorWrapper color, int packedLight) {
        Vector3 renderOffset = axis.copy().perpendicular().rotate(angle, axis).normalize();
        Matrix4f matr = pose.last().pose();

        Vector3 vec = renderOffset.copy().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(pos);
        vec.drawPos(matr, vb).setUv(uv.u(), uv.v() + uv.vHeight()).setColor(color.getColor()).setLight(packedLight);

        vec = renderOffset.copy().multiply(-1F).normalize().multiply(scale).add(pos);
        vec.drawPos(matr, vb).setUv(uv.u() + uv.uWidth(), uv.v() + uv.vHeight()).setColor(color.getColor()).setLight(packedLight);

        vec = renderOffset.copy().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(pos);
        vec.drawPos(matr, vb).setUv(uv.u() + uv.uWidth(), uv.v()).setColor(color.getColor()).setLight(packedLight);

        vec = renderOffset.copy().normalize().multiply(scale).add(pos);
        vec.drawPos(matr, vb).setUv(uv.u(), uv.v()).setColor(color.getColor()).setLight(packedLight);
    }
}
