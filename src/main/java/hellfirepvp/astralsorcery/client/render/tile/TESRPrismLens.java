/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.base.ASprism_color;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;


/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRPrismLens
 * Created by HellFirePvP
 * Date: 20.09.2016 / 13:08
 */
public class TESRPrismLens extends TileEntitySpecialRenderer<TileCrystalPrismLens> {

    private static List<TileCrystalPrismLens> coloredPositions = new LinkedList<>();

    private static final ASprism_color modelPrismColoredFrame = new ASprism_color();
    private static final BindableResource texPrismColorFrame = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "prism/prism_color");

    public static void renderColoredPrismsLast() {
        GlStateManager.pushMatrix();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(Minecraft.getMinecraft().getRenderPartialTicks());

        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        for (TileCrystalPrismLens prism : coloredPositions) {
            if(prism.getLensColor() == null) continue;
            EnumFacing against = prism.getPlacedAgainst();

            Color c = prism.getLensColor().wrappedColor;
            GlStateManager.pushMatrix();
            BlockPos pos = prism.getPos();

            switch (against) {
                case DOWN:
                    GlStateManager.translate(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                    GlStateManager.rotate(180, 1, 0, 0);
                    break;
                case UP:
                    GlStateManager.translate(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5);
                    break;
                case NORTH:
                    GlStateManager.translate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 1.5);
                    GlStateManager.rotate(270, 1, 0, 0);
                    break;
                case SOUTH:
                    GlStateManager.translate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() - 0.5);
                    GlStateManager.rotate(90, 1, 0, 0);
                    break;
                case WEST:
                    GlStateManager.translate(pos.getX() + 1.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    GlStateManager.rotate(90, 0, 0, 1);
                    break;
                case EAST:
                    GlStateManager.translate(pos.getX() - 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    GlStateManager.rotate(270, 0, 0, 1);
                    break;
                default:
                    break;
            }

            GlStateManager.scale(0.0625, 0.0625, 0.0625);

            GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
            renderColoredPrism();
            GlStateManager.color(1F, 1F, 1F, 1F);

            GlStateManager.popMatrix();
        }

        coloredPositions.clear();

        GlStateManager.popMatrix();
    }

    @Override
    public void render(TileCrystalPrismLens te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();

        if(te.getLinkedPositions().size() > 0) {
            long sBase = 0x5911539513145924L;
            sBase ^= (long) te.getPos().getX();
            sBase ^= (long) te.getPos().getY();
            sBase ^= (long) te.getPos().getZ();
            RenderingUtils.renderLightRayEffects(x + 0.5, y + 0.6, z + 0.5, BlockCollectorCrystal.CollectorCrystalType.ROCK_CRYSTAL.displayColor, sBase, ClientScheduler.getClientTick(), 9, 50, 25);
        }

        GlStateManager.translate(x + 0.5, y + 0.20, z + 0.5);

        GlStateManager.scale(0.6, 0.6, 0.6);
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        TESRCollectorCrystal.renderCrystal(false, true);
        RenderHelper.disableStandardItemLighting();
        if(te.getLensColor() != null) {
            coloredPositions.add(te);
            /*GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
            GL11.glScaled(0.0625, 0.0625, 0.0625);
            GL11.glRotated(180, 1, 0, 0);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.applyServer();
            Color c = te.getLensColor().wrappedColor;
            GL11.glColor4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
            renderColoredPrism();
            GL11.glColor4f(1F, 1F, 1F, 1F);*/
        }
        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();
    }

    private static void renderColoredPrism() {
        texPrismColorFrame.bind();
        modelPrismColoredFrame.render(null, 0, 0, 0, 0, 0, 1);
    }

}
