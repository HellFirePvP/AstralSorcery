/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:21
 */
public class TESRAltar extends TileEntitySpecialRenderer<TileAltar> {

    @Override
    public void render(TileAltar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        switch (te.getAltarLevel()) {
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
                        if(!requiredHandles.isEmpty()) {
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
                        }
                        RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.WHITE, 0, ClientScheduler.getClientTick(), 15, 2F, 40, 25);
                    } else {
                        RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.WHITE, 0x12315661L, ClientScheduler.getClientTick(), 20, 2F, 50, 25);
                        RenderingUtils.renderLightRayEffects(0, 0.5, 0, Color.BLUE, 0, ClientScheduler.getClientTick(), 10, 1F, 40, 25);
                    }
                    GL11.glTranslated(0, 0.15, 0);
                    GL11.glScaled(0.7, 0.7, 0.7);
                    TESRCollectorCrystal.renderCrystal(true, true);
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

}
