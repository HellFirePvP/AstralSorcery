/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.base.ASaltarT2;
import hellfirepvp.astralsorcery.client.models.base.ASaltarT3;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collection;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:21
 */
public class TESRAltar extends TileEntitySpecialRenderer<TileAltar> {

    private static final Random rand = new Random();

    private static final ASaltarT2 modelAltar2 = new ASaltarT2();
    private static final BindableResource texAltar2 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "base/altart2");

    private static final ASaltarT3 modelAltar3 = new ASaltarT3();
    private static final BindableResource texAltar3 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "base/altart3");

    @Override
    public void render(TileAltar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        long sBase = 7553015156732193565L;
        sBase ^= (long) te.getPos().getX();
        sBase ^= (long) te.getPos().getY();
        sBase ^= (long) te.getPos().getZ();
        double jBase = ClientScheduler.getClientTick() + partialTicks;
        jBase /= 20D;

        switch (te.getAltarLevel()) {
            case ATTUNEMENT:
                renderT2Additions(te, x, y, z, jBase);
                break;
            case CONSTELLATION_CRAFT:
                renderT3Additions(te, x, y, z, jBase);
                if(te.getMultiblockState()) {
                    GL11.glPushMatrix();
                    renderCrystalEffects(te, x, y, z, partialTicks, sBase);
                    renderFocusLens(te, x, y, z, partialTicks);
                    //renderConstellation(te, x, y, z, partialTicks);
                    GL11.glPopMatrix();
                }
                break;
            case TRAIT_CRAFT:
                if(te.getMultiblockState()) {
                    IConstellation c = te.getFocusedConstellation();
                    if(c != null) {
                        GL11.glPushMatrix();
                        float alphaDaytime = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(te.getWorld());
                        alphaDaytime *= 0.8F;

                        int max = 5000;
                        int t = (int) (ClientScheduler.getClientTick() % max);
                        float halfAge = max / 2F;
                        float tr = 1F - (Math.abs(halfAge - t) / halfAge);
                        tr *= 2;

                        RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);

                        float br = 0.9F * alphaDaytime;

                        RenderConstellation.renderConstellationIntoWorldFlat(c, c.getConstellationColor(), new Vector3(te).add(0.5, 0.03, 0.5), 5 + tr, 2, 0.1F + br);
                        GL11.glPopMatrix();
                    }
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glPushMatrix();
                    GL11.glTranslated(x + 0.5, y + 4, z + 0.5);
                    ActiveCraftingTask act = te.getActiveCraftingTask();
                    if(act != null && act.getRecipeToCraft() instanceof TraitRecipe) {
                        Collection<ItemHandle> requiredHandles = ((TraitRecipe) act.getRecipeToCraft()).getTraitItemHandles();
                        int amt = 60 / requiredHandles.size();
                        for (ItemHandle outer : requiredHandles) {
                            NonNullList<ItemStack> stacksApplicable = outer.getApplicableItemsForRender();
                            int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                            ItemStack element = stacksApplicable.get(MathHelper.floor(
                                    MathHelper.clamp(stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)), 0, stacksApplicable.size() - 1)));
                            Color col = ItemColorizationHelper.getDominantColorFromItemStack(element);
                            if(col == null) {
                                col = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                            }
                            RenderingUtils.renderLightRayEffects(0, 0.5, 0, col, 0x12315L | outer.hashCode(), ClientScheduler.getClientTick(), 20, 2F, amt, amt / 2);
                        }
                        RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.WHITE, 0, ClientScheduler.getClientTick(), 15, 2F, 40, 25);
                    } else {
                        RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.WHITE, 0x12315661L, ClientScheduler.getClientTick(), 20, 2F, 50, 25);
                        RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.BLUE, 0, ClientScheduler.getClientTick(), 10, 1F, 40, 25);
                    }
                    TESRCollectorCrystal.renderCrystal(false, true);
                    GL11.glPopMatrix();
                    TextureHelper.refreshTextureBindState();
                    GL11.glPopAttrib();
                }
                break;
        }

        ActiveCraftingTask task = te.getActiveCraftingTask();
        if(task != null) {
            task.getRecipeToCraft().onCraftTESRRender(te, x, y, z, partialTicks);
        }
    }

    private void renderT3Additions(TileAltar te, double x, double y, double z, double jump) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glScaled(0.0625, 0.0625, 0.0625);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.pushMatrix();
        //GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        texAltar3.bind();
        modelAltar3.render(null, (float) jump, 0, 0, 0, 0, 1F);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderT2Additions(TileAltar te, double x, double y, double z, double jump) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glScaled(0.0625, 0.0625, 0.0625);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.pushMatrix();
        //GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        texAltar2.bind();
        modelAltar2.render(null, (float) jump, 0, 0, 0, 0, 1F);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    /*private void renderConstellation(TileAltar te, double x, double y, double z, float partialTicks) {
        IConstellation c = te.getFocusedConstellation();
        if(c == null) return;

        float alphaDaytime = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(te.getWorld());

        int max = 5000;
        int t = (int) (ClientScheduler.getClientTick() % max);
        float halfAge = max / 2F;
        float tr = 1F - (Math.abs(halfAge - t) / halfAge);
        tr *= 2;

        RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        RenderConstellation.renderConstellationIntoWorldFlat(c, c.getTierRenderColor(), new Vector3(te).add(0.5, 0.03, 0.5), 4 + tr, 2, 0.1F + 0.8F * alphaDaytime);
    }*/

    private void renderFocusLens(TileAltar te, double x, double y, double z, float partialTicks) {

    }

    private void renderCrystalEffects(TileAltar te, double x, double y, double z, float partialTicks, long sBase) {
        /*GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        RenderingUtils.renderLightRayEffects(x + 0.5, y + 3.5, z + 0.5, BlockCollectorCrystal.CollectorCrystalType.ROCK_CRYSTAL.displayColor, sBase, ClientScheduler.getClientTick(), 20, 50, 25);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 3, z + 0.5);
        TESRCollectorCrystal.renderCrystal(false, true);
        GL11.glPopMatrix();
        GL11.glPopAttrib();*/
    }

    /*private void doAltarTileTransforms(TileAltar.AltarLevel level) {
        switch (level) {
            case DISCOVERY:
                GL11.glTranslated(0.5, 1.44, 0.5);
                GL11.glScaled(0.06, 0.06, 0.06);
                GL11.glRotated(180, 1, 0, 0);
                break;
            case ATTENUATION:
                break;
            case CONSTELLATION_CRAFT:
                break;
            case TRAIT_CRAFT:
                break;
            case ENDGAME:
                break;
        }
    }
    private void doAltarItemTransforms(TileAltar.AltarLevel level) {
        switch (level) {
            case DISCOVERY:
                GL11.glTranslated(0.5, 0.9, 0.5);
                GL11.glScalef(0.035F, 0.035F, 0.035F);
                GL11.glRotated(45, 1, 0, 0);
                GL11.glRotated(45, 0, 1, 0);
                GL11.glRotated(180, 0, 0, 1);
                break;
            case ATTENUATION:
                break;
            case CONSTELLATION_CRAFT:
                break;
            case TRAIT_CRAFT:
                break;
            case ENDGAME:
                break;
        }
    }

    private void doAltarRender(TileAltar.AltarLevel level) {
        switch (level) {
            case DISCOVERY:
                break;
            case ATTENUATION:
                break;
            case CONSTELLATION_CRAFT:
                break;
            case TRAIT_CRAFT:
                break;
            case ENDGAME:
                break;
        }
    }*/

}
