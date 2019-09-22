/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.model.builtin.ModelLens;
import hellfirepvp.astralsorcery.client.model.builtin.ModelLensColored;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderTileLens
 * Created by HellFirePvP
 * Date: 21.09.2019 / 15:01
 */
public class RenderTileLens extends CustomTileEntityRenderer<TileLens> {

    private static final ModelLens MODEL_LENS = new ModelLens();
    private static final ModelLensColored MODEL_LENS_COLORED = new ModelLensColored();

    @Override
    public void render(TileLens lens, double x, double y, double z, float pTicks, int destroyStage) {
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.15F);
        GlStateManager.pushMatrix();

        List<BlockPos> linked = lens.getLinkedPositions();
        float degYaw = 0;
        float degPitch = 0;

        switch (lens.getPlacedAgainst()) {
            case DOWN:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = lens.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));

                    degYaw = (float) Math.atan2(dir.getX(), dir.getZ());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }

                GlStateManager.translated(x + 0.5, y + 1.335, z + 0.5);
                GlStateManager.scaled(0.055, 0.055, 0.055);

                GlStateManager.rotated(180, 1, 0, 0);
                GlStateManager.rotated(degYaw % 360, 0, 1, 0);

                renderLens(degPitch, destroyStage);
                if (lens.getColorType() != null) {
                    GlStateManager.rotated(180, 0, 1, 0);
                    Color c = lens.getColorType().getColor();
                    GlStateManager.color4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
                    renderLensColored(-degPitch, destroyStage);
                    GlStateManager.color4f(1F, 1F, 1F, 1F);
                }
                break;
            case UP:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = lens.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));

                    degYaw = (float) Math.atan2(dir.getX(), dir.getZ());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }

                GlStateManager.translated(x + 0.5, y - 0.335, z + 0.5);
                GlStateManager.scaled(0.055, 0.055, 0.055);

                GlStateManager.rotated((-degYaw + 180) % 360, 0, 1, 0);

                renderLens(-degPitch, destroyStage);
                if (lens.getColorType() != null) {
                    GlStateManager.rotated(180, 0, 1, 0);
                    Color c = lens.getColorType().getColor();
                    GlStateManager.color4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
                    renderLensColored(degPitch, destroyStage);
                    GlStateManager.color4f(1F, 1F, 1F, 1F);
                }
                break;
            case NORTH:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = lens.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getZ(), Math.sqrt(dir.getX() * dir.getX() + dir.getY() * dir.getY()));

                    degYaw = (float) Math.atan2(dir.getX(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }
                GlStateManager.translated(x + 0.5, y + 0.5, z + 1.335);
                GlStateManager.scaled(0.055, 0.055, 0.055);

                GlStateManager.rotated(270, 1, 0, 0);
                GlStateManager.rotated((-degYaw + 180) % 360, 0, 1, 0);

                renderLens(degPitch, destroyStage);
                if (lens.getColorType() != null) {
                    GlStateManager.rotated(180, 0, 1, 0);
                    Color c = lens.getColorType().getColor();
                    GlStateManager.color4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
                    renderLensColored(-degPitch, destroyStage);
                    GlStateManager.color4f(1F, 1F, 1F, 1F);
                }
                break;
            case SOUTH:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = lens.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getZ(), Math.sqrt(dir.getX() * dir.getX() + dir.getY() * dir.getY()));

                    degYaw = (float) Math.atan2(dir.getX(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }
                GlStateManager.translated(x + 0.5, y + 0.5, z - 0.335);
                GlStateManager.scaled(0.055, 0.055, 0.055);

                GlStateManager.rotated(90, 1, 0, 0);
                GlStateManager.rotated(degYaw % 360, 0, 1, 0);

                renderLens(-degPitch, destroyStage);
                if (lens.getColorType() != null) {
                    GlStateManager.rotated(180, 0, 1, 0);
                    Color c = lens.getColorType().getColor();
                    GlStateManager.color4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
                    renderLensColored(degPitch, destroyStage);
                    GlStateManager.color4f(1F, 1F, 1F, 1F);
                }
                break;
            case WEST:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = lens.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getX(), Math.sqrt(dir.getZ() * dir.getZ() + dir.getY() * dir.getY()));

                    degYaw = (float) Math.atan2(dir.getZ(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }
                GlStateManager.translated(x + 1.335, y + 0.5, z + 0.5);
                GlStateManager.scaled(0.055, 0.055, 0.055);

                GlStateManager.rotated(90, 0, 0, 1);
                GlStateManager.rotated((degYaw + 270 % 360), 0, 1, 0);

                renderLens(degPitch, destroyStage);
                if (lens.getColorType() != null) {
                    GlStateManager.rotated(180, 0, 1, 0);
                    Color c = lens.getColorType().getColor();
                    GlStateManager.color4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
                    renderLensColored(-degPitch, destroyStage);
                    GlStateManager.color4f(1F, 1F, 1F, 1F);
                }
                break;
            case EAST:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = lens.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getX(), Math.sqrt(dir.getZ() * dir.getZ() + dir.getY() * dir.getY()));

                    degYaw = (float) Math.atan2(dir.getZ(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }
                GlStateManager.translated(x - 0.335, y + 0.5, z + 0.5);
                GlStateManager.scaled(0.055, 0.055, 0.055);

                GlStateManager.rotated(270, 0, 0, 1);
                GlStateManager.rotated((-degYaw + 90 % 360), 0, 1, 0);

                renderLens(-degPitch, destroyStage);
                if (lens.getColorType() != null) {
                    GlStateManager.rotated(180, 0, 1, 0);
                    Color c = lens.getColorType().getColor();
                    GlStateManager.color4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
                    renderLensColored(degPitch, destroyStage);
                    GlStateManager.color4f(1F, 1F, 1F, 1F);
                }
                break;
            default:
                break;
        }

        TextureHelper.refreshTextureBind();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
    }

    private void renderLensColored(float pitch, int destroyStage) {
        MODEL_LENS_COLORED.glass.    rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS_COLORED.fitting1. rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS_COLORED.fitting2. rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS_COLORED.detail1_1.rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS_COLORED.detail1.  rotateAngleX = pitch * 0.017453292F;

        MODEL_LENS_COLORED.renderGlass(1, destroyStage);
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        MODEL_LENS_COLORED.renderFrame(1, destroyStage);
    }

    private void renderLens(float pitch, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.rotated(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotated(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        MODEL_LENS.lens.rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS.render(1, destroyStage);
        RenderHelper.disableStandardItemLighting();
    }

}
