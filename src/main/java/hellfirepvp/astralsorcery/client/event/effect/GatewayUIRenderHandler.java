/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.EnumSet;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GatewayUIRenderHandler
 * Created by HellFirePvP
 * Date: 12.09.2020 / 09:46
 */
public class GatewayUIRenderHandler implements ITickHandler {

    private static final GatewayUIRenderHandler INSTANCE = new GatewayUIRenderHandler();

    private GatewayUI currentUI = null;

    private GatewayUIRenderHandler() {}

    public static GatewayUIRenderHandler getInstance() {
        return INSTANCE;
    }

    public GatewayUI getOrCreateUI(IWorld world, BlockPos pos, Vector3 renderPos) {
        if (currentUI == null ||
                !currentUI.getDimType().equals(world.getDimension().getType().getRegistryName()) ||
                !currentUI.getPos().equals(pos)) {
            currentUI = GatewayUI.create(world, pos, renderPos, 5.5D);
        }
        currentUI.refreshView();
        return currentUI;
    }

    public GatewayUI getCurrentUI() {
        return currentUI;
    }

    private boolean validate() {
        if (this.currentUI == null) {
            return true;
        }
        World world = Minecraft.getInstance().world;
        TileCelestialGateway gateway;
        if (world == null ||
                this.currentUI.getVisibleTicks() <= 0 ||
                !this.currentUI.getDimType().equals(world.getDimension().getType().getRegistryName()) ||
                (gateway = MiscUtils.getTileAt(world, this.currentUI.getPos(), TileCelestialGateway.class, true)) == null ||
                !gateway.doesSeeSky() ||
                !gateway.hasMultiblock()) {
            this.currentUI = null;
        }
        return this.currentUI == null;
    }

    void render(RenderWorldLastEvent event) {
        if (this.validate()) {
            return;
        }
        float pTicks = event.getPartialTicks();
        MatrixStack renderStack = event.getMatrixStack();
        Vector3 renderOffset = this.currentUI.getRenderCenter();

        PlayerEntity player = Minecraft.getInstance().player;
        double dst = new Vector3(renderOffset).distance(Vector3.atEntityCorner(player).addY(1.5));
        if(dst > 3) return;
        float alpha = MathHelper.clamp(1F - ((float) (dst / 2D)), 0F, 1F);
        Color c = new Color(0xF0BD00);
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

        long seed = 0xA781B4F01C771923L;
        seed |= ((long) this.currentUI.getPos().getX()) << 48;
        seed |= ((long) this.currentUI.getPos().getY()) << 24;
        seed |= ((long) this.currentUI.getPos().getZ());
        Random rand = new Random(seed);

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);

        TexturesAS.TEX_STAR_1.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            for (int i = 0; i < 300; i++) {
                Vector3 dir = Vector3.random(rand).normalize().multiply(this.currentUI.getSphereRadius() * 0.9);
                float a = RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), pTicks, rand.nextInt(7) + 6);
                a *= alpha;
                RenderingDrawUtils.renderFacingFullQuadVB(buf, renderStack,
                        renderOffset.getX() + dir.getX(),
                        renderOffset.getY() + dir.getY(),
                        renderOffset.getZ() + dir.getZ(),
                        0.07F, 0, 255, 255, 255, (int) (a * 255F));
            }
            for (GatewayUI.GatewayEntry entry : this.currentUI.getGatewayEntries()) {
                int r = red;
                int g = green;
                int b = blue;
                DyeColor nodeColor = entry.getNode().getColor();
                if (nodeColor != null) {
                    Color ovr = ColorUtils.flareColorFromDye(nodeColor);
                    r = ovr.getRed();
                    g = ovr.getGreen();
                    b = ovr.getBlue();
                }
                float a = RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), pTicks, rand.nextInt(7) + 6);
                a = 0.4F + (0.6F * a);
                a *= alpha;
                RenderingDrawUtils.renderFacingFullQuadVB(buf, renderStack,
                        renderOffset.getX() + entry.getRelativePos().getX(),
                        renderOffset.getY() + entry.getRelativePos().getY(),
                        renderOffset.getZ() + entry.getRelativePos().getZ(),
                        0.16F, 0, r, g, b, (int) (a * 255F));
            }
        });

        RenderSystem.depthMask(true);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        GatewayUI.GatewayEntry entry = findMatchingEntry(MathHelper.wrapDegrees(player.rotationYaw), MathHelper.wrapDegrees(player.rotationPitch));
        if (entry != null) {
            ITextComponent display = entry.getNode().getDisplayName();
            if (display != null && !display.getFormattedText().isEmpty()) {
                String text = display.getFormattedText();
                Vector3 at = entry.getRelativePos().clone().add(renderOffset);
                at.subtract(RenderingVectorUtils.getStandardTranslationRemovalVector(pTicks));
                FontRenderer fr = Minecraft.getInstance().fontRenderer;

                Color color = c;
                DyeColor nodeColor = entry.getNode().getColor();
                if (nodeColor != null) {
                    color = ColorUtils.flareColorFromDye(nodeColor);
                }

                //MatrixStack st = new MatrixStack();

                //st.push();
                //st.translate(at.getX(), at.getY() + 2, at.getZ());
                //st.scale(0.15F, 0.15F, 0.15F);
                //float iYaw = RenderingVectorUtils.interpolate(MathHelper.wrapDegrees(player.prevRotationYaw), MathHelper.wrapDegrees(player.rotationYaw), pTicks);
                //st.rotate(Vector3f.YP.rotationDegrees(-iYaw + 180F));

                //Matrix4f matr = renderStack.getLast().getMatrix();
                //int length = fr.getStringWidth(text);
                //IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                //fr.renderString(text, -(length / 2F), 0, color.getRGB(), false, matr, buffers, true, 0, LightmapUtil.getPackedFullbrightCoords());
                //buffers.finish();

                //st.pop();

                /*
                                GlStateManager.pushMatrix();
                FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
                Vector3 pos = entry.relativePos.clone().add(origin);
                RenderingUtils.removeStandartTranslationFromTESRMatrix(pticks);
                GlStateManager.translate(pos.getX(), pos.getY() + 0.25, pos.getZ());
                GlStateManager.scale(0.015, -0.015, 0.015);
                GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
                float plRot = RenderingUtils.interpolateRotation(MathHelper.wrapDegrees(player.prevRotationYaw), MathHelper.wrapDegrees(player.rotationYaw), pticks);
                GlStateManager.rotate(-plRot + 180, 0, 1, 0);
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();
                int length = fr.getStringWidth(display);
                fr.drawString(display, -(length / 2), 0, 0x88F0BD00);

                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.popMatrix();
                 */
            }
        }
    }

    @Nullable
    public GatewayUI.GatewayEntry findMatchingEntry(float yaw, float pitch) {
        float matchAccurancy = 4;
        for (GatewayUI.GatewayEntry entry : this.currentUI.getGatewayEntries()) {
            if(Math.abs(entry.getPitch() - pitch) < matchAccurancy &&
                    (Math.abs(entry.getYaw() - yaw) <= matchAccurancy || Math.abs(entry.getYaw() - yaw - 360F) <= matchAccurancy)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if (this.currentUI != null) {
            this.currentUI.decrementVisibleTicks();
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "GatewayUI Render Handler";
    }
}
