/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageTraitRecipe
 * Created by HellFirePvP
 * Date: 24.07.2017 / 21:25
 */
public class JournalPageTraitRecipe implements IJournalPage {

    public TraitRecipe recipe;

    public JournalPageTraitRecipe(TraitRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(recipe, TileAltar.AltarLevel.TRAIT_CRAFT);
    }

    public static class Render extends JournalPageConstellationRecipe.Render {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridtr");

        private final TraitRecipe recipe;

        public Render(TraitRecipe recipe, TileAltar.AltarLevel altarLevel) {
            super(recipe, altarLevel);
            this.recipe = recipe;
            this.gridTexture = texGrid;
        }

        private void renderTraitInnerSlots(float offsetX, float offsetY, float zLevel, TraitRecipe recipe) {
            RenderHelper.enableGUIStandardItemLighting();
            renderAltarSlot(offsetX +  80, offsetY +  78, zLevel, recipe.getInnerTraitItems(TraitRecipe.TraitRecipeSlot.UPPER_CENTER));
            renderAltarSlot(offsetX +  30, offsetY + 128, zLevel, recipe.getInnerTraitItems(TraitRecipe.TraitRecipeSlot.LEFT_CENTER));
            renderAltarSlot(offsetX + 131, offsetY + 128, zLevel, recipe.getInnerTraitItems(TraitRecipe.TraitRecipeSlot.RIGHT_CENTER));
            renderAltarSlot(offsetX +  80, offsetY + 178, zLevel, recipe.getInnerTraitItems(TraitRecipe.TraitRecipeSlot.LOWER_CENTER));
            RenderHelper.disableStandardItemLighting();
            TextureHelper.refreshTextureBindState();
        }

        private void renderTraitOuterSlots(float offsetX, float offsetY, float zLevel, TraitRecipe recipe) {
            RenderHelper.enableGUIStandardItemLighting();

            float centerX = offsetX + 80;
            float centerY = offsetY + 128;

            float perc = (ClientScheduler.getClientTick() % 3000) / 3000F;

            zLevel += 100;

            List<ItemHandle> traitItemHandles = recipe.getTraitItemHandles();
            int amt = traitItemHandles.size();
            for (int i = 0; i < amt; i++) {
                ItemHandle handle = traitItemHandles.get(i);
                double part = ((double) i) / ((double) amt) * 2.0 * Math.PI; //Shift by half a period
                part = MathHelper.clamp(part, 0, 2.0 * Math.PI);
                part += (2.0 * Math.PI * perc) + Math.PI;
                double xAdd = Math.sin(part) * 75.0;
                double yAdd = Math.cos(part) * 75.0;
                renderRotatingSlot((float) (centerX + xAdd), (float) (centerY + yAdd), zLevel, handle.getApplicableItems());
            }

            RenderHelper.disableStandardItemLighting();
            TextureHelper.refreshTextureBindState();
        }

        private void renderRotatingSlot(float offsetX, float offsetY, float zLevel, List<ItemStack> stacks) {
            if(stacks == null || stacks.isEmpty()) return;

            long select = (ClientScheduler.getClientTick() / 20);
            select %= stacks.size();
            ItemStack draw = stacks.get((int) select);

            TextureHelper.refreshTextureBindState();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX, offsetY, zLevel + 60);
            GL11.glScaled(1.1, 1.1, 1.1);
            Rectangle r = drawItemStack(draw, 0, 0, 0);
            r = new Rectangle((int) offsetX, (int) offsetY, (int) (r.getWidth() * 1.1), (int) (r.getHeight() * 1.1));
            addRenderedStackRectangle(r, draw);
            GL11.glPopMatrix();
        }

        @Override
        public void addTooltip(List<String> out) {
            super.addTooltip(out);

            if(recipe.getRequiredConstellation() != null) {
                out.add(I18n.format("astralsorcery.journal.recipe.constellation",
                        I18n.format(recipe.getRequiredConstellation().getUnlocalizedName())));
            }
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            super.render(offsetX, offsetY, pTicks, zLevel, mouseX, mouseY);

            GlStateManager.color(1F, 1F, 1F, 1F);
            renderTraitInnerSlots(offsetX, offsetY, zLevel, recipe);
            renderTraitOuterSlots(offsetX, offsetY, zLevel, recipe);
            if(recipe.getRequiredConstellation() != null) {
                IConstellation focus = recipe.getRequiredConstellation();
                GlStateManager.disableAlpha();
                RenderConstellation.renderConstellationIntoGUI(new Color(0xEEEEEE), focus,
                        Math.round(offsetX + 30), Math.round(offsetY + 78), zLevel,
                        125, 125, 2F, new RenderConstellation.BrightnessFunction() {
                            @Override
                            public float getBrightness() {
                                return 0.3F;
                            }
                        }, true, false);
                GlStateManager.enableAlpha();
            }
            GlStateManager.color(1F, 1F, 1F, 1F);
        }

    }

}
