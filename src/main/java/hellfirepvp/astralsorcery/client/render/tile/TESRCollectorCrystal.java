/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.obj.OBJModelLibrary;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:42
 */
public class TESRCollectorCrystal extends TileEntitySpecialRenderer<TileCollectorCrystal> implements IItemRenderer {

    private static final BindableResource texWhite = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_white");
    private static final BindableResource texBlue = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_blue");

    private static int dlCrystal = -1;

    @Override
    public void render(TileCollectorCrystal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        BlockCollectorCrystalBase.CollectorCrystalType type = te.getType();
        if(te.doesSeeSky()) {
            long sBase = 1553015L;
            sBase ^= (long) te.getPos().getX();
            sBase ^= (long) te.getPos().getY();
            sBase ^= (long) te.getPos().getZ();
            Color c = type == null ? BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor : type.displayColor;
            GlStateManager.enableBlend();
            Blending.DEFAULT.applyStateManager();
            Blending.DEFAULT.apply();
            if(te.isEnhanced()) {
                c = te.getConstellation() != null ? te.getConstellation().getConstellationColor() : c;
                RenderingUtils.renderLightRayEffects(x + 0.5, y + 0.5, z + 0.5, c, sBase, ClientScheduler.getClientTick(), 20, 1.4F, 50, 25);
                RenderingUtils.renderLightRayEffects(x + 0.5, y + 0.5, z + 0.5, Color.WHITE, sBase, ClientScheduler.getClientTick(), 40, 2, 15, 15);
            } else {
                RenderingUtils.renderLightRayEffects(x + 0.5, y + 0.5, z + 0.5, c, sBase, ClientScheduler.getClientTick(), 20, 50, 25);
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        renderCrystal(type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL, true);
        GlStateManager.popMatrix();
    }

    public static void renderCrystal(boolean isCelestial, boolean bounce) {
        GlStateManager.pushMatrix();
        if(bounce) {
            int t = (int) (Minecraft.getMinecraft().world.getTotalWorldTime() & 255);
            float perc = (256 - t) / 256F;
            perc = MathHelper.cos((float) (perc * 2 * Math.PI));
            GlStateManager.translate(0, 0.03 * perc, 0);
        }
        TextureHelper.refreshTextureBindState();
        RenderHelper.disableStandardItemLighting();
        if(isCelestial) {
            renderTile(texBlue);
        } else {
            renderTile(texWhite);
        }
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private static void renderTile(BindableResource tex) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.13F, 0.13F, 0.13F);
        tex.bind();
        if(dlCrystal == -1) {
            dlCrystal = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(dlCrystal, GL11.GL_COMPILE);
            OBJModelLibrary.bigCrystal.renderAll(true);
            GlStateManager.glEndList();
        }
        GlStateManager.callList(dlCrystal);

        GlStateManager.popMatrix();
    }

    @Override
    public void render(ItemStack stack) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0, 0.5);
        RenderHelper.disableStandardItemLighting();
        BlockCollectorCrystalBase.CollectorCrystalType type = ItemCollectorCrystal.getType(stack);
        switch (type) {
            case ROCK_CRYSTAL:
                renderTile(texWhite);
                break;
            case CELESTIAL_CRYSTAL:
                renderTile(texBlue);
                break;
        }
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

}
