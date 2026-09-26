/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei.category;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.config.RenderingConfig;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.integration.jei.base.ASRecipeCategory;
import hellfirepvp.astralsorcery.common.integration.jei.ingredient.LumenIngredientRenderer;
import hellfirepvp.astralsorcery.common.integration.jei.ingredient.LumenIngredientType;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.IngredientUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.CountIngredient;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarRecipeCategory
 * Created by HellFirePvP
 * Date: 26.09.2026 / 19:45
 */
public class AltarRecipeCategory extends ASRecipeCategory<AltarRecipe> {

    public static final RecipeType<AltarRecipe> RECIPE_TYPE = makeType("altar_crafting", AltarRecipe.class);

    private static final ResourceLocation ALTAR_T1         = AstralSorcery.key("textures/screen/jei/altar_grid_1.png");
    private static final ResourceLocation ALTAR_T2         = AstralSorcery.key("textures/screen/jei/altar_grid_2.png");
    private static final ResourceLocation ALTAR_T3         = AstralSorcery.key("textures/screen/jei/altar_grid_3.png");
    private static final ResourceLocation ALTAR_ADDITIONAL = AstralSorcery.key("textures/screen/jei/altar_grid_additional.png");
    private static final IntRectangle INFO_ICON = new IntRectangle(86, 3, 12, 12);

    private final IGuiHelper helper;
    private final IDrawable infoIcon, additionalBox;

    public AltarRecipeCategory(IGuiHelper helper) {
        super(148, 220, helper, ItemsAS.BLOCK_ALTAR_ILLUMINATION);
        this.helper = helper;
        this.infoIcon = createInfoIcon(helper);
        this.additionalBox = helper.drawableBuilder(ALTAR_ADDITIONAL, 0, 0, 130, 34)
                .setTextureSize(130, 34)
                .build();
    }

    @Override
    public RecipeType<AltarRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AltarRecipe recipe, IFocusGroup focuses) {
        this.setGrid(builder, recipe);
        this.setInnerGrid(builder, recipe);
        this.setOuterGrid(builder, recipe);
        this.setAdditionalInputs(builder, recipe);
        this.setLumenInputs(builder, recipe);

        this.setOutputs(builder, recipe);
    }

    private void setGrid(IRecipeLayoutBuilder builder, AltarRecipe recipe) {
        AltarRecipeGrid grid = recipe.getGrid();
        for (int index = 0; index < 9; index++) {
            int offsetX = (index % 3) * 18;
            int offsetY = (index / 3) * 18;
            IngredientBridge ingredient = grid.getInputs().get(index);
            this.addInputBridge(builder, ingredient, 48 + offsetX, 94 + offsetY);
        }
    }

    private void setInnerGrid(IRecipeLayoutBuilder builder, AltarRecipe recipe) {
        AltarRecipeGrid grid = recipe.getGrid();
        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                int realSlot = (xx + 1) + (yy + 1) * 5;

                int offsetX = 29 + xx * 37;
                int offsetY = 75 + yy * 37;

                IngredientBridge relayInput = grid.getRelayInputs().get(realSlot);
                this.addInputBridge(builder, relayInput, offsetX, offsetY);
            }
        }
    }

    private void setOuterGrid(IRecipeLayoutBuilder builder, AltarRecipe recipe) {
        TileAltar.getOuterRelaySlots().forEach(slot -> {
            IngredientBridge relayInput = recipe.getGrid().getRelayInputs().get(slot);
            if (relayInput.isEmpty()) return;

            int slotX = slot % 5;
            int slotY = slot / 5;
            int offsetX = 10 + switch (slotX) {
                case 1 -> 19;
                case 2 -> 56;
                case 3 -> 93;
                case 4 -> 112;
                default -> 0;
            };
            int offsetY = 56 + switch (slotY) {
                case 1 -> 19;
                case 2 -> 56;
                case 3 -> 93;
                case 4 -> 112;
                default -> 0;
            };

            this.addInputBridge(builder, relayInput, offsetX, offsetY);
        });
    }

    private void setAdditionalInputs(IRecipeLayoutBuilder builder, AltarRecipe recipe) {
        List<CountIngredient> otherIngredients = recipe.getRequiredAdditionalInputs().stream()
                .filter(ingredient -> !ingredient.isEmpty())
                .toList();
        List<FluidStack> otherFluidIngredients = recipe.getRequiredFluid().stream()
                .filter(ingredient -> !ingredient.isEmpty())
                .toList();
        float count = otherIngredients.size() + otherFluidIngredients.size();
        if (count <= 0) return;
        List<Object> merged = new ArrayList<>(otherIngredients);
        merged.addAll(otherFluidIngredients);

        for (int i = 0; i < merged.size(); i++) {
            Object ingredient = merged.get(i);

            int offsetX = 10 + (i % 8) * 16;
            int offsetY = 188 + (i / 8) * 16;

            if (ingredient instanceof CountIngredient countIngredient) {
                builder.addSlot(RecipeIngredientRole.INPUT, offsetX, offsetY)
                        .addIngredients(VanillaTypes.ITEM_STACK, countIngredient.getItems());
            } else if (ingredient instanceof FluidStack fluidStack) {
                builder.addSlot(RecipeIngredientRole.INPUT, offsetX, offsetY)
                        .setFluidRenderer(1000, false, 16, 16)
                        .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(fluidStack));
            }
        }
    }

    private void setLumenInputs(IRecipeLayoutBuilder builder, AltarRecipe recipe) {
        List<LumenStack> lumen = recipe.getRequiredLumen();
        if (lumen.isEmpty()) return;

        int x = 3;
        int y = 2;
        for (int i = 0; i < lumen.size(); i++) {
            int offsetY = y + i * 9;

            LumenStack stack = lumen.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, x, offsetY)
                    .setCustomRenderer(LumenIngredientType.INSTANCE,
                            new LumenIngredientRenderer(LumenIngredientRenderer.Display.SHORT_BAR, LumenIngredientRenderer.TooltipMode.SHOW_AMOUNT, stack.getAmount()))
                    .addIngredient(LumenIngredientType.INSTANCE, stack);
        }
    }

    private void setOutputs(IRecipeLayoutBuilder builder, AltarRecipe recipe) {
        AltarCraftingInput displayInput = recipe.createInputForDisplay(ClientProxy.getClientTick());
        List<ItemStack> outputs = recipe.getOutputsForDisplay(displayInput, RegistryUtil.getRegistryAccess());
        if (outputs.isEmpty()) return;

        builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 18)
                .addItemStack(outputs.getFirst());

        outputs = outputs.subList(1, outputs.size());
        for (int i = 0; i < outputs.size(); i++) {
            int offsetX = 103;
            int offsetY = 3 + (i / 3) * 18;
            builder.addSlot(RecipeIngredientRole.OUTPUT, offsetX, offsetY)
                    .setStandardSlotBackground()
                    .addItemStack(outputs.get(i));
        }
    }

    private void addInputBridge(IRecipeLayoutBuilder builder, IngredientBridge ingredient, int x, int y) {
        if (ingredient.isEmpty()) return;

        if (ingredient.getIngredientType() == IngredientBridge.Type.ITEM) {
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addIngredients(ingredient.getIngredient());
        } else {
            ItemStack contained = IngredientUtil.getRandomDisplayStack(ingredient.getFluidIngredient(), 0);
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addItemStack(contained);
        }
    }

    @Override
    public void draw(AltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.getBackground(recipe).draw(guiGraphics);

        if (!recipe.getRequiredAdditionalInputs().isEmpty() || !recipe.getRequiredFluid().isEmpty()) {
            this.additionalBox.draw(guiGraphics, 9, 187);
        }

        if (recipe.isOnlyNight() || recipe.getFocusConstellation().isPresent() || !recipe.getRequiredStarlight().isEmpty()) {
            this.infoIcon.draw(guiGraphics, INFO_ICON.x(), INFO_ICON.y());
        }
    }

    private IDrawable getBackground(AltarRecipe recipe) {
        ResourceLocation texture = switch (recipe.getRequiredType()) {
            case ILLUMINATION -> ALTAR_T1;
            case RESONANCE -> {
                List<IngredientBridge> relayInputs = recipe.getGrid().getRelayInputs();
                for (int slot : TileAltar.getOuterRelaySlots()) {
                    if (!relayInputs.get(slot).isEmpty()) {
                        yield ALTAR_T3;
                    }
                }
                yield ALTAR_T2;
            }
            case LUMINANCE, RADIANCE -> ALTAR_T3;
        };
        return this.helper.drawableBuilder(texture, 0, 0, this.getWidth(), this.getHeight())
                .setTextureSize(this.getWidth(), this.getHeight())
                .build();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, AltarRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (INFO_ICON.contains(mouseX, mouseY)) {
            PlayerProgress progress = ResearchManager.getClientProgress();

            List<Component> tTip = new ArrayList<>();
            if (recipe.isOnlyNight()) {
                tTip.add(Component.translatable("jei.astralsorcery.info.altar.recipe_night"));
            }
            recipe.getFocusConstellation().ifPresent(cst -> {
                if (!progress.hasDiscoveredConstellation(cst)) {
                    tTip.add(Component.translatable("jei.astralsorcery.info.altar.recipe_constellation.unknown"));
                } else {
                    tTip.add(Component.translatable("jei.astralsorcery.info.altar.recipe_constellation", cst.getColoredName()));
                }

                if (recipe.getBaseFocusShatterChance() > 0) {
                    float displayChance = recipe.getBaseFocusShatterChance() * 100F;
                    int estChance = Math.round(displayChance);

                    if (displayChance < 1F) {
                        tTip.add(Component.translatable("jei.astralsorcery.info.altar.focus_shatter.small"));
                    } else if (displayChance >= 100F) {
                        tTip.add(Component.translatable("jei.astralsorcery.info.altar.focus_shatter.guaranteed"));
                    } else {
                        // In case it rounds up from 99.5
                        if (estChance >= 100) estChance = 99;
                        tTip.add(Component.translatable("jei.astralsorcery.info.altar.focus_shatter", estChance));
                    }
                }
            });
            Set<BaseConstellation> requiredStarlight = recipe.getRequiredStarlight();
            if (!requiredStarlight.isEmpty()) {
                tTip.add(Component.translatable("jei.astralsorcery.info.altar.focused_starlight"));
                requiredStarlight.forEach(cst -> {
                    tTip.add(Component.literal("- ").append(cst.getColoredName()));
                });
            }

            tooltip.addAll(tTip);
        }
    }

    @Override
    public List<ItemStack> provideCatalyst() {
        return List.of(
                ItemsAS.BLOCK_ALTAR_ILLUMINATION.toStack(),
                ItemsAS.BLOCK_ALTAR_RESONANCE.toStack(),
                ItemsAS.BLOCK_ALTAR_LUMINANCE.toStack(),
                ItemsAS.BLOCK_ALTAR_RADIANCE.toStack()
        );
    }

    @Override
    public List<AltarRecipe> provideRecipes(RecipeManager recipeManager, IRecipeRegistration register) {
        return provideRawRecipes(recipeManager, RecipeTypesAS.ALTAR_CRAFTING_TYPE);
    }
}
