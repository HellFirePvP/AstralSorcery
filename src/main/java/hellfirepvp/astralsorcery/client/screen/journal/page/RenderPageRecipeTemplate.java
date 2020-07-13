/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.auxiliary.book.BookLookupInfo;
import hellfirepvp.astralsorcery.common.auxiliary.book.BookLookupRegistry;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarUpgradeRecipe;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.IngredientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageRecipeTemplate
 * Created by HellFirePvP
 * Date: 12.10.2019 / 10:53
 */
public abstract class RenderPageRecipeTemplate extends RenderablePage {

    protected Map<Rectangle, Tuple<ItemStack, Ingredient>> thisFrameInputStacks = new HashMap<>();
    protected Tuple<Rectangle, ItemStack> thisFrameOuputStack = null;
    protected Rectangle thisFrameInfoStar = null;

    protected RenderPageRecipeTemplate(@Nullable ResearchNode node, int nodePage) {
        super(node, nodePage);
    }

    protected void clearFrameRectangles() {
        this.thisFrameInputStacks.clear();
        this.thisFrameOuputStack = null;
        this.thisFrameInfoStar = null;
    }

    public void renderRecipeGrid(float offsetX, float offsetY, float zLevel, AbstractRenderableTexture tex) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        tex.bindTexture();
        RenderingGuiUtils.drawRect(offsetX + 25, offsetY, zLevel, 129, 202);
        RenderSystem.disableBlend();
    }

    public void renderExpectedIngredientInput(float offsetX, float offsetY, float zLevel, double scale, long tickOffset, Ingredient ingredient) {
        ItemStack expected = IngredientHelper.getRandomMatchingStack(ingredient, ClientScheduler.getClientTick() + tickOffset);
        if (!expected.isEmpty()) {
            BlockAtlasTexture.getInstance().bindTexture();

            this.renderItemStack(offsetX, offsetY, zLevel, scale, expected);
            this.thisFrameInputStacks.put(new Rectangle((int) offsetX, (int) offsetY, (int) (16 * scale), (int) (16 * scale)), new Tuple<>(expected, ingredient));
        }
    }

    public void renderExpectedIngredientInput(float offsetX, float offsetY, float zLevel, double scale, long tickOffset, List<ItemStack> displayOptions) {
        int mod = (int) (((ClientScheduler.getClientTick() + tickOffset) / 20L) % displayOptions.size());
        ItemStack expected = displayOptions.get(MathHelper.clamp(mod, 0, displayOptions.size() - 1));
        if (!expected.isEmpty()) {
            BlockAtlasTexture.getInstance().bindTexture();

            this.renderItemStack(offsetX, offsetY, zLevel, scale, expected);
            this.thisFrameInputStacks.put(new Rectangle((int) offsetX, (int) offsetY, (int) (16 * scale), (int) (16 * scale)), new Tuple<>(expected, null));
        }
    }

    public void renderExpectedRelayInputs(float offsetX, float offsetY, float zLevel, SimpleAltarRecipe altarRecipe) {
        float centerX = offsetX + 80;
        float centerY = offsetY + 128;

        float perc = (ClientScheduler.getClientTick() % 3000) / 3000F;

        List<WrappedIngredient> ingredients = altarRecipe.getRelayInputs();
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
            BlockAtlasTexture.getInstance().bindTexture();

            this.renderItemStack(offsetX, offsetY, zLevel, scale, stack);
            this.thisFrameOuputStack = new Tuple<>(new Rectangle((int) offsetX, (int) offsetY, (int) (16 * scale), (int) (16 * scale)), stack);
        }
    }

    protected void renderItemStack(float offsetX, float offsetY, float zLevel, double scale, ItemStack stack) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(offsetX, offsetY, zLevel + 60);
        RenderSystem.scaled(scale, scale, scale);
        RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), stack, 0, 0, null);
        RenderSystem.popMatrix();
    }

    public boolean handleRecipeNameCopyClick(double mouseX, double mouseZ, SimpleAltarRecipe recipe) {
        if (Minecraft.getInstance().gameSettings.showDebugInfo &&
                Screen.hasControlDown() &&
                this.thisFrameOuputStack.getA().contains(mouseX, mouseZ)) {
            String recipeName = recipe.getId().toString();
            Minecraft.getInstance().keyboardListener.setClipboardString(recipeName);
            Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("astralsorcery.misc.ctrlcopy.copied", recipeName));
            return true;
        }
        return false;
    }

    public boolean handleBookLookupClick(double mouseX, double mouseZ) {
        for (Rectangle r : thisFrameInputStacks.keySet()) {
            if (r.contains(mouseX, mouseZ)) {
                ItemStack stack = thisFrameInputStacks.get(r).getA();
                BookLookupInfo info = BookLookupRegistry.findPage(Minecraft.getInstance().player, LogicalSide.CLIENT, stack);
                if (info != null &&
                        info.canSee(ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT)) &&
                        !info.getResearchNode().equals(this.getResearchNode())) {
                    info.openGui();
                    return true;
                }
            }
        }
        if (this.thisFrameOuputStack != null) {
            if (this.thisFrameOuputStack.getA().contains(mouseX, mouseZ)) {
                ItemStack stack = this.thisFrameOuputStack.getB();
                BookLookupInfo info = BookLookupRegistry.findPage(Minecraft.getInstance().player, LogicalSide.CLIENT, stack);
                if (info != null &&
                        info.canSee(ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT)) &&
                        !info.getResearchNode().equals(this.getResearchNode())) {
                    info.openGui();
                    return true;
                }
            }
        }
        return false;
    }

    public void renderInfoStar(float offsetX, float offsetY, float zLevel, float pTicks) {
        MatrixStack renderStack = new MatrixStack();
        renderStack.translate(offsetX + 140, offsetY + 20, zLevel);
        this.thisFrameInfoStar = RenderingDrawUtils.drawInfoStar(renderStack, IDrawRenderTypeBuffer.defaultBuffer(), 15F, pTicks);
        this.thisFrameInfoStar.translate((int) (offsetX + 140), (int) (offsetY + 20));
    }

    public void renderRequiredConstellation(float offsetX, float offsetY, float zLevel, @Nullable IConstellation constellation) {
        if (constellation != null) {
            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            RenderingConstellationUtils.renderConstellationIntoGUI(new Color(0xEEEEEE), constellation,
                    Math.round(offsetX + 30), Math.round(offsetY + 78), zLevel,
                    125, 125, 2F, () -> 0.4F, true, false);
            RenderSystem.disableBlend();
        }
    }

    public void renderInfoStarTooltips(float offsetX, float offsetY, float zLevel, float mouseX, float mouseY, Consumer<List<ITextComponent>> tooltipProvider) {
        if (this.thisFrameInfoStar == null) {
            return;
        }

        List<ITextComponent> toolTip = new LinkedList<>();
        tooltipProvider.accept(toolTip);
        if (!toolTip.isEmpty() && this.thisFrameInfoStar.contains(mouseX, mouseY)) {
            zLevel += 200;
            RenderingDrawUtils.renderBlueTooltipComponents(offsetX, offsetY, zLevel, toolTip, RenderablePage.getFontRenderer(), false);
            zLevel -= 200;
        }
    }

    public void renderHoverTooltips(float mouseX, float mouseY, float zLevel, ResourceLocation recipeName) {
        List<ITextComponent> toolTip = new LinkedList<>();
        addStackTooltip(mouseX, mouseY, recipeName, toolTip);

        if (!toolTip.isEmpty()) {
            zLevel += 800;
            RenderingDrawUtils.renderBlueTooltipComponents(mouseX, mouseY, zLevel, toolTip, RenderablePage.getFontRenderer(), true);
            zLevel -= 800;
        }
    }

    protected void addAltarRecipeTooltip(SimpleAltarRecipe altarRecipe, List<ITextComponent> toolTip) {
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
                ITextComponent starlightRequired = getAltarStarlightAmountDescription(itemName, altarRecipe.getStarlightRequirement(), typeSelected.getStarlightCapacity());
                ITextComponent starlightRequirementDescription = new TranslationTextComponent("astralsorcery.journal.recipe.altar.starlight.desc");

                toolTip.add(starlightRequirementDescription);
                toolTip.add(starlightRequired);
            }
        }
        if (altarRecipe instanceof AltarUpgradeRecipe) {
            toolTip.add(new TranslationTextComponent("astralsorcery.journal.recipe.altar.upgrade"));
        }
    }

    protected void addConstellationInfoTooltip(@Nullable IConstellation cst, List<ITextComponent> toolTip) {
        if (cst != null) {
            toolTip.add(new TranslationTextComponent("astralsorcery.journal.recipe.constellation", cst.getConstellationName()));
        }
    }

    protected ITextComponent getAltarStarlightAmountDescription(ITextComponent altarName, float amountRequired, float maxAmount) {
        String base = "astralsorcery.journal.recipe.altar.starlight.";
        float perc = amountRequired / maxAmount;
        if (perc <= 0.1) {
            base += "lowest";
        } else if (perc <= 0.25) {
            base += "low";
        } else if (perc <= 0.5) {
            base += "avg";
        } else if (perc <= 0.75) {
            base += "more";
        } else if (perc <= 0.9) {
            base += "high";
        } else if (perc > 1) {
            base += "toomuch";
        } else {
            base += "highest";
        }
        return new TranslationTextComponent("astralsorcery.journal.recipe.altar.starlight.format",
                altarName,
                new TranslationTextComponent(base));
    }

    protected ITextComponent getInfuserChanceDescription(float chance) {
        String base = "astralsorcery.journal.recipe.infusion.chance.";
        if (chance <= 0.3) {
            base += "low";
        } else if (chance <= 0.7) {
            base += "average";
        } else if (chance < 1) {
            base += "high";
        } else {
            base += "always";
        }
        return new TranslationTextComponent(base);
    }

    protected void addStackTooltip(float mouseX, float mouseY, ResourceLocation recipeName, List<ITextComponent> tooltip) {
        for (Rectangle rect : thisFrameInputStacks.keySet()) {
            if (rect.contains(mouseX, mouseY)) {
                Tuple<ItemStack, Ingredient> inputInfo = thisFrameInputStacks.get(rect);
                addInputInformation(inputInfo.getA(), inputInfo.getB(), tooltip);
                return;
            }
        }
        if (this.thisFrameOuputStack.getA().contains(mouseX, mouseY)) {
            ItemStack stack = this.thisFrameOuputStack.getB();
            addInputInformation(stack, null, tooltip);

            if (Minecraft.getInstance().gameSettings.showDebugInfo) {
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new TranslationTextComponent("astralsorcery.misc.recipename", recipeName.toString()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true)));
                tooltip.add(new TranslationTextComponent("astralsorcery.misc.ctrlcopy", recipeName.toString()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE).setItalic(true)));
            }
        }
    }

    protected void addInputInformation(ItemStack stack, @Nullable Ingredient stackIngredient, List<ITextComponent> tooltip) {
        try {
            tooltip.addAll(stack.getTooltip(Minecraft.getInstance().player, Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL));
        } catch (Exception exc) {
            tooltip.add(new TranslationTextComponent("astralsorcery.misc.tooltipError").setStyle(new Style().setColor(TextFormatting.RED)));
        }
        BookLookupInfo info = BookLookupRegistry.findPage(Minecraft.getInstance().player, LogicalSide.CLIENT, stack);
        if (info != null &&
                info.canSee(ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT)) &&
                !info.getResearchNode().equals(this.getResearchNode())) {
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new TranslationTextComponent("astralsorcery.misc.craftInformation").setStyle(new Style().setColor(TextFormatting.GRAY)));
        }
        if (stackIngredient != null && Minecraft.getInstance().gameSettings.advancedItemTooltips) {
            Tag<Item> itemTag = IngredientHelper.guessTag(stackIngredient);
            if (itemTag != null) {
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new TranslationTextComponent("astralsorcery.misc.input.tag", itemTag.getId().toString()).setStyle(new Style().setColor(TextFormatting.GRAY)));
            }
            if (stackIngredient instanceof FluidIngredient) {
                List<FluidStack> fluids = ((FluidIngredient) stackIngredient).getFluids();

                if (!fluids.isEmpty()) {
                    ITextComponent cmp = null;
                    for (FluidStack f : fluids) {
                        if (cmp == null) {
                            cmp = f.getFluid().getAttributes().getDisplayName(f);
                        } else {
                            cmp = new TranslationTextComponent("astralsorcery.misc.input.fluid.chain", cmp, f.getFluid().getAttributes().getDisplayName(f)).setStyle(new Style().setColor(TextFormatting.GRAY));
                        }
                    }
                    tooltip.add(new StringTextComponent(""));
                    tooltip.add(new TranslationTextComponent("astralsorcery.misc.input.fluid", cmp).setStyle(new Style().setColor(TextFormatting.GRAY)));
                }
            }
        }
    }
}
