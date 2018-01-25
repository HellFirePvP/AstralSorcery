/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.IAltarUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryBookLookups;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageDiscoveryRecipe
 * Created by HellFirePvP
 * Date: 06.10.2016 / 11:35
 */
public class JournalPageDiscoveryRecipe implements IJournalPage {

    public DiscoveryRecipe recipeToRender;

    public JournalPageDiscoveryRecipe(DiscoveryRecipe recipeToRender) {
        this.recipeToRender = recipeToRender;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(recipeToRender, TileAltar.AltarLevel.DISCOVERY);
    }

    public static class Render implements IGuiRenderablePage {

        private static final BindableResource texGrid = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "griddisc");

        private final DiscoveryRecipe recipe;
        private final TileAltar.AltarLevel altarLevel;
        protected BindableResource gridTexture;

        private Map<Rectangle, ItemStack> thisFrameStackFrames = new HashMap<>();

        public Render(DiscoveryRecipe recipe, TileAltar.AltarLevel correspondingAltarLevel) {
            this.recipe = recipe;
            this.gridTexture = texGrid;
            this.altarLevel = correspondingAltarLevel;
        }

        @Override
        public boolean propagateMouseClick(int mouseX, int mouseZ) {
            for (Rectangle r : thisFrameStackFrames.keySet()) {
                if(r.contains(mouseX, mouseZ)) {
                    ItemStack stack = thisFrameStackFrames.get(r);
                    RegistryBookLookups.LookupInfo lookup = RegistryBookLookups.tryGetPage(Minecraft.getMinecraft().player, Side.CLIENT, stack);
                    if(lookup != null) {
                        RegistryBookLookups.openLookupJournalPage(lookup);
                    }
                }
            }
            return false;
        }

        protected void renderStandartRecipeGrid(float offsetX, float offsetY, float zLevel, BindableResource grid) {
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            grid.bind();
            drawRect(offsetX + 25, offsetY, 129, 202, zLevel);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            TextureHelper.refreshTextureBindState();
        }

        protected void renderOutputOnGrid(float offsetX, float offsetY, float zLevel) {
            RenderHelper.enableGUIStandardItemLighting();
            ItemStack out = recipe.getOutputForRender();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX + 78, offsetY + 25, zLevel + 60);
            GL11.glScaled(1.4, 1.4, 1.4);
            Rectangle r = drawItemStack(out, 0, 0, 0);
            r = new Rectangle((int) offsetX + 78, (int) offsetY + 25, (int) (r.getWidth() * 1.4), (int) (r.getHeight() * 1.4));
            addRenderedStackRectangle(r, out);
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
            RenderHelper.disableStandardItemLighting();
        }

        protected void renderDefaultExpectedItems(float offsetX, float offsetY, float zLevel, AccessibleRecipe recipe) {
            RenderHelper.enableGUIStandardItemLighting();
            double offX = offsetX + 55;
            double offY = offsetY + 103;
            for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {

                NonNullList<ItemStack> expected = recipe.getExpectedStackForRender(srs);
                if(expected == null || expected.isEmpty()) expected = recipe.getExpectedStackForRender(srs.rowMultipler, srs.columnMultiplier);
                if(expected == null || expected.isEmpty()) continue;
                long select = ((ClientScheduler.getClientTick() + srs.rowMultipler * 40 + srs.columnMultiplier * 40) / 20);
                select %= expected.size();
                ItemStack draw = expected.get((int) select);

                TextureHelper.refreshTextureBindState();
                GL11.glPushMatrix();
                GL11.glTranslated(offX + (srs.columnMultiplier * 25), offY + (srs.rowMultipler * 25), zLevel + 60);
                GL11.glScaled(1.1, 1.1, 1.1);
                Rectangle r = drawItemStack(draw, 0, 0, 0);
                r = new Rectangle((int) (offX + (srs.columnMultiplier * 25)), (int) (offY + (srs.rowMultipler * 25)),
                        (int) (r.getWidth() * 1.1), (int) (r.getHeight() * 1.1));
                addRenderedStackRectangle(r, draw);
                GL11.glPopMatrix();
            }
            RenderHelper.disableStandardItemLighting();
        }

        public void addTooltip(List<String> out) {
            if(recipe.getPassiveStarlightRequired() > 0) {
                TileAltar.AltarLevel highestPossible = altarLevel;
                ProgressionTier reached = ResearchManager.clientProgress.getTierReached();
                if (reached.isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
                    highestPossible = TileAltar.AltarLevel.TRAIT_CRAFT;
                } else if (reached.isThisLaterOrEqual(ProgressionTier.CONSTELLATION_CRAFT)) {
                    highestPossible = TileAltar.AltarLevel.CONSTELLATION_CRAFT;
                } else if(reached.isThisLaterOrEqual(ProgressionTier.ATTUNEMENT)) {
                    highestPossible = TileAltar.AltarLevel.ATTUNEMENT;
                }
                long indexSel = (ClientScheduler.getClientTick() / 30) % (highestPossible.ordinal() + 1);
                TileAltar.AltarLevel levelSelected = TileAltar.AltarLevel.values()[((int) indexSel)];
                int max = levelSelected.getStarlightMaxStorage();
                Item i = Item.getItemFromBlock(BlocksAS.blockAltar);
                String locTier = i.getUnlocalizedName(new ItemStack(i, 1, levelSelected.ordinal()));
                locTier = I18n.format(locTier + ".name");
                String displReq = "  " + getDescriptionFromStarlightAmount(locTier, recipe.getPassiveStarlightRequired(), max);
                String dsc = I18n.format("astralsorcery.journal.recipe.amt.desc");
                out.add(dsc);
                out.add(displReq);
            }
            if(recipe instanceof INighttimeRecipe) {
                out.add(I18n.format("astralsorcery.journal.recipe.nighttime"));
            }
            if(recipe instanceof IAltarUpgradeRecipe) {
                out.add(I18n.format("astralsorcery.journal.recipe.upgrade"));
            }
        }

        public void addStackTooltip(float mouseX, float mouseY, List<String> tooltip) {
            for (Rectangle rect : thisFrameStackFrames.keySet()) {
                if(rect.contains(mouseX, mouseY)) {
                    ItemStack stack = thisFrameStackFrames.get(rect);
                    try {
                        tooltip.addAll(stack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
                    } catch (Throwable tr) {
                        tooltip.add(TextFormatting.RED + "<Error upon trying to get this item's tooltip>");
                    }
                    RegistryBookLookups.LookupInfo lookup = RegistryBookLookups.tryGetPage(Minecraft.getMinecraft().player, Side.CLIENT, stack);
                    if(lookup != null) {
                        tooltip.add("");
                        tooltip.add(I18n.format("misc.craftInformation"));
                    }
                }
            }
        }

        protected void addRenderedStackRectangle(Rectangle r, ItemStack rendered) {
            this.thisFrameStackFrames.put(r, rendered);
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            thisFrameStackFrames.clear();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);

            renderStandartRecipeGrid(offsetX, offsetY, zLevel, gridTexture);

            renderOutputOnGrid(offsetX, offsetY, zLevel);

            renderDefaultExpectedItems(offsetX, offsetY, zLevel, recipe.getNativeRecipe());

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }

        @Override
        public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            List<String> out = Lists.newLinkedList();
            addTooltip(out);
            if(!out.isEmpty()) {
                float widthHeightStar = 15F;
                Rectangle r = drawInfoStar(offsetX + 140, offsetY + 20, zLevel, widthHeightStar, pTicks);
                if(r.contains(mouseX, mouseY)) {
                    RenderingUtils.renderBlueTooltip((int) (offsetX), (int) (offsetY),
                            out, getStandardFontRenderer());
                }
            }

            out = Lists.newLinkedList();
            addStackTooltip(mouseX, mouseY, out);
            if(!out.isEmpty()) {
                RenderingUtils.renderBlueTooltip((int) (mouseX), (int) (mouseY),
                        out, getStandardFontRenderer());
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }
    }

}
