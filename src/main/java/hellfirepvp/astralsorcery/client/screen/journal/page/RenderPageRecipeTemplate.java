/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.auxiliary.book.BookLookupInfo;
import hellfirepvp.astralsorcery.common.auxiliary.book.BookLookupRegistry;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarUpgradeRecipe;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.IngredientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.LogicalSide;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageRecipeTemplate
 * Created by HellFirePvP
 * Date: 12.10.2019 / 10:53
 */
public abstract class RenderPageRecipeTemplate implements RenderablePage {

    protected Map<Rectangle, ItemStack> thisFrameInputStacks = new HashMap<>();
    protected Tuple<Rectangle, ItemStack> thisFrameOuputStack = null;
    protected Rectangle thisFrameInfoStar = null;

    protected void clearFrameRectangles() {
        this.thisFrameInputStacks.clear();
        this.thisFrameOuputStack = null;
        this.thisFrameInfoStar = null;
    }

    public void renderRecipeGrid(float offsetX, float offsetY, float zLevel, AbstractRenderableTexture tex) {
        tex.bindTexture();
        RenderingGuiUtils.drawRectDetailed(offsetX + 25, offsetY, zLevel, 129, 202);
    }

    public void renderExpectedIngredientInput(float offsetX, float offsetY, float zLevel, double scale, long tickOffset, Ingredient ingredient) {
        ItemStack expected = IngredientHelper.getRandomMatchingStack(ingredient, ClientScheduler.getClientTick() + tickOffset);
        if (!expected.isEmpty()) {
            TextureHelper.bindBlockAtlas();

            GlStateManager.pushMatrix();
            GlStateManager.translated(offsetX, offsetY, zLevel + 60);
            GlStateManager.scaled(scale, scale, scale);
            RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), expected, 0, 0, null);
            this.thisFrameInputStacks.put(new Rectangle((int) offsetX, (int) offsetY, (int) (16 * scale), (int) (16 * scale)), expected);
            GlStateManager.popMatrix();
        }
    }

    public void renderExpectedRelayInputs(float offsetX, float offsetY, float zLevel, SimpleAltarRecipe altarRecipe) {
        float centerX = offsetX + 80;
        float centerY = offsetY + 128;

        float perc = (ClientScheduler.getClientTick() % 3000) / 3000F;

        List<WrappedIngredient> ingredients = altarRecipe.getTraitInputIngredients();
        int amt = ingredients.size();
        for (int i = 0; i < ingredients.size(); i++) {
            double part = ((double) i) / ((double) amt) * 2.0 * Math.PI; //Shift by half a period
            part = MathHelper.clamp(part, 0, 2.0 * Math.PI);
            part += (2.0 * Math.PI * perc) + Math.PI;
            double xAdd = Math.sin(part) * 75.0;
            double yAdd = Math.cos(part) * 75.0;

            renderExpectedIngredientInput((float) (centerX + xAdd), (float) (centerY + yAdd), zLevel, 1F, i * 20, ingredients.get(i).getIngredient());
        }
    }

    public void renderExpectedItemStackOutput(float offsetX, float offsetY, float zLevel, double scale, ItemStack stack) {
        if (!stack.isEmpty()) {
            TextureHelper.bindBlockAtlas();

            GlStateManager.pushMatrix();
            GlStateManager.translated(offsetX, offsetY, zLevel + 60);
            GlStateManager.scaled(scale, scale, scale);
            RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), stack, 0, 0, null);
            this.thisFrameOuputStack = new Tuple<>(new Rectangle((int) offsetX, (int) offsetY, (int) (16 * scale), (int) (16 * scale)), stack);
            GlStateManager.popMatrix();
        }
    }

    public boolean handleRecipeNameCopyClick(double mouseX, double mouseZ, SimpleAltarRecipe recipe) {
        if (Minecraft.getInstance().gameSettings.showDebugInfo &&
                Screen.hasControlDown() &&
                this.thisFrameOuputStack.getA().contains(mouseX, mouseZ)) {
            String recipeName = recipe.getId().toString();
            Minecraft.getInstance().keyboardListener.setClipboardString(recipeName);
            Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("misc.ctrlcopy.copied", recipeName));
            return true;
        }
        return false;
    }

    public boolean handleBookLookupClick(double mouseX, double mouseZ) {
        for (Rectangle r : thisFrameInputStacks.keySet()) {
            if (r.contains(mouseX, mouseZ)) {
                ItemStack stack = thisFrameInputStacks.get(r);
                BookLookupInfo info = BookLookupRegistry.findPage(Minecraft.getInstance().player, LogicalSide.CLIENT, stack);
                if (info != null && info.canSee(ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT))) {
                    info.openGui();
                    return true;
                }
            }
        }
        if (this.thisFrameOuputStack != null) {
            if (this.thisFrameOuputStack.getA().contains(mouseX, mouseZ)) {
                ItemStack stack = this.thisFrameOuputStack.getB();
                BookLookupInfo info = BookLookupRegistry.findPage(Minecraft.getInstance().player, LogicalSide.CLIENT, stack);
                if (info != null && info.canSee(ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT))) {
                    info.openGui();
                    return true;
                }
            }
        }
        return false;
    }

    public void renderInfoStar(float offsetX, float offsetY, float zLevel, float pTicks) {
        this.thisFrameInfoStar = RenderingDrawUtils.drawInfoStar(offsetX + 140, offsetY + 20, zLevel, 15F, pTicks);
    }

    public void renderRequiredConstellation(float offsetX, float offsetY, float zLevel, SimpleAltarRecipe altarRecipe) {
        if (altarRecipe.getFocusConstellation() != null) {
            IConstellation focus = altarRecipe.getFocusConstellation();
            GlStateManager.disableAlphaTest();
            RenderingConstellationUtils.renderConstellationIntoGUI(new Color(0xEEEEEE), focus,
                    Math.round(offsetX + 30), Math.round(offsetY + 78), zLevel,
                    125, 125, 2F, () -> 0.4F, true, false);
            GlStateManager.enableAlphaTest();
        }
    }

    public void renderInfoStarTooltips(float offsetX, float offsetY, float mouseX, float mouseY, SimpleAltarRecipe altarRecipe) {
        List<ITextComponent> toolTip = new LinkedList<>();
        addInfoTooltip(altarRecipe, toolTip);

        if (!toolTip.isEmpty() && this.thisFrameInfoStar.contains(mouseX, mouseY)) {
            GlStateManager.disableDepthTest();
            RenderingDrawUtils.renderBlueTooltipComponents((int) offsetX, (int) offsetY, toolTip, RenderablePage.getFontRenderer(), false);
            GlStateManager.enableDepthTest();
        }
    }

    public void renderHoverTooltips(float mouseX, float mouseY, ResourceLocation recipeName) {
        List<ITextComponent> toolTip = new LinkedList<>();
        addStackTooltip(mouseX, mouseY, recipeName, toolTip);

        if (!toolTip.isEmpty()) {
            GlStateManager.disableDepthTest();
            RenderingDrawUtils.renderBlueTooltipComponents((int) mouseX, (int) mouseY, toolTip, RenderablePage.getFontRenderer(), true);
            GlStateManager.enableDepthTest();
        }
    }

    protected void addInfoTooltip(SimpleAltarRecipe altarRecipe, List<ITextComponent> toolTip) {
        if (altarRecipe.getStarlightRequirement() > 0) {
            AltarType highestPossible = null;
            ProgressionTier reached = ResearchHelper.getClientProgress().getTierReached();
            for (AltarType type : AltarType.values()) {
                if ((highestPossible == null || !type.isThisLEThan(highestPossible)) &&
                        reached.isThisLaterOrEqual(type.getAssociatedTier().getRequiredProgress())) {
                    highestPossible = type;
                }
            }
            if (highestPossible != null) {
                long indexSel = (ClientScheduler.getClientTick() / 30) % (highestPossible.ordinal() + 1);
                AltarType typeSelected = AltarType.values()[((int) indexSel)];
                ITextComponent itemName = typeSelected.getAltarItemRepresentation().getDisplayName();
                ITextComponent starlightRequired = getStarlightAmountDescription(itemName, altarRecipe.getStarlightRequirement(), typeSelected.getStarlightCapacity());
                ITextComponent starlightRequirementDescription = new TranslationTextComponent("astralsorcery.journal.recipe.starlight.desc");

                toolTip.add(starlightRequirementDescription);
                toolTip.add(starlightRequired);
            }
        }
        if (altarRecipe instanceof AltarUpgradeRecipe) {
            toolTip.add(new TranslationTextComponent("astralsorcery.journal.recipe.upgrade"));
        }
        if (altarRecipe.getFocusConstellation() != null) {
            toolTip.add(new TranslationTextComponent("astralsorcery.journal.recipe.constellation",
                    new TranslationTextComponent(altarRecipe.getFocusConstellation().getUnlocalizedName())));
        }
    }

    protected ITextComponent getStarlightAmountDescription(ITextComponent altarName, float amountRequired, float maxAmount) {
        String base = "astralsorcery.journal.recipe.starlight.";
        float perc = amountRequired / maxAmount;
        if(perc <= 0.1) {
            base += "lowest";
        } else if(perc <= 0.25) {
            base += "low";
        } else if(perc <= 0.5) {
            base += "avg";
        } else if(perc <= 0.75) {
            base += "more";
        } else if(perc <= 0.9) {
            base += "high";
        } else if(perc > 1) {
            base += "toomuch";
        } else {
            base += "highest";
        }
        return new TranslationTextComponent("astralsorcery.journal.recipe.starlight.format",
                altarName,
                new TranslationTextComponent(base));
    }

    protected void addStackTooltip(float mouseX, float mouseY, ResourceLocation recipeName, List<ITextComponent> tooltip) {
        for (Rectangle rect : thisFrameInputStacks.keySet()) {
            if (rect.contains(mouseX, mouseY)) {
                ItemStack stack = thisFrameInputStacks.get(rect);
                try {
                    tooltip.addAll(stack.getTooltip(Minecraft.getInstance().player, Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
                } catch (Exception exc) {
                    tooltip.add(new TranslationTextComponent("misc.tooltipError").setStyle(new Style().setColor(TextFormatting.RED)));
                }
                BookLookupInfo info = BookLookupRegistry.findPage(Minecraft.getInstance().player, LogicalSide.CLIENT, stack);
                if (info != null) {
                    tooltip.add(new StringTextComponent(""));
                    tooltip.add(new TranslationTextComponent("misc.craftInformation").setStyle(new Style().setColor(TextFormatting.GRAY)));
                }
            }
        }
        if (this.thisFrameOuputStack.getA().contains(mouseX, mouseY)) {
            ItemStack stack = this.thisFrameOuputStack.getB();
            try {
                tooltip.addAll(stack.getTooltip(Minecraft.getInstance().player, Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
            } catch (Exception exc) {
                tooltip.add(new TranslationTextComponent("misc.tooltipError").setStyle(new Style().setColor(TextFormatting.RED)));
            }
            BookLookupInfo info = BookLookupRegistry.findPage(Minecraft.getInstance().player, LogicalSide.CLIENT, stack);
            if (info != null) {
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new TranslationTextComponent("misc.craftInformation").setStyle(new Style().setColor(TextFormatting.GRAY)));
            }
            if (Minecraft.getInstance().gameSettings.showDebugInfo) {
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new TranslationTextComponent("misc.recipename", recipeName.toString()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true)));
                tooltip.add(new TranslationTextComponent("misc.ctrlcopy", recipeName.toString()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true)));
            }
        }
    }

}
