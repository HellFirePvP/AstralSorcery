/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.liquid;

import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.ingredient.HasStoredLumenIngredient;
import hellfirepvp.astralsorcery.common.ingredient.IsLumenBindableIngredient;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.recipe.builder.LiquidStarlightRecipeBuilder;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightRecipeProvider extends RecipeProvider {

    private LiquidStarlightRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void registerRecipes(RecipeOutput recipeOutput, HolderLookup.Provider provider) {
        LiquidStarlightRecipeBuilder.builder("infused_wood", new SizedIngredient(Ingredient.of(ItemTags.LOGS), 1))
                .addOutputModifier(LiquidStarlightOutputDropItem.create(ItemsAS.BLOCK_INFUSED_WOOD_RAW.toStack()))
                .duration(6)
                .randomAdditionalDuration(10)
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("merge_crystals",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1)))
                .addOutputModifier(LiquidStarlightOutputMergeCrystal.getInstance())
                .duration(60)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("form_crystal_cluster",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.DUSTS_STARMETAL), 1),
                        List.of(new SizedIngredient(Ingredient.of(TagsAS.Items.DUSTS_STARMETAL), 1)))
                .addOutputModifier(LiquidStarlightOutputFormCrystalCluster.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("form_gem_crystal_cluster",
                        new SizedIngredient(Ingredient.of(ItemsAS.ILLUMINATION_POWDER), 1),
                        List.of(new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1)))
                .addOutputModifier(LiquidStarlightOutputFormGemCrystalCluster.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("grow_crystal_size",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1))
                .addOutputModifier(LiquidStarlightOutputGrowSize.getInstance())
                .duration(60)
                .randomAdditionalDuration(30)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);

        LiquidStarlightRecipeBuilder.builder("bind_lumen",
                        new SizedIngredient(Ingredient.of(ItemsAS.LUMEN_CRYSTAL), 1),
                        List.of(
                                SizedIngredient.of(TagsAS.Items.DUSTS_STARMETAL, 1),
                                new SizedIngredient(IsLumenBindableIngredient.INSTANCE.toVanilla(), 1)
                        ))
                .addOutputModifier(LiquidStarlightOutputBindLumen.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("fill_lumen",
                        new SizedIngredient(Ingredient.of(ItemsAS.LUMEN_CRYSTAL), 1),
                        List.of(new SizedIngredient(HasStoredLumenIngredient.INSTANCE.toVanilla(), 1)))
                .addOutputModifier(LiquidStarlightOutputFillLumen.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);

        LiquidStarlightRecipeBuilder.builder("engrave_lumen_virel",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(
                                new SizedIngredient(DataComponentIngredient.of(
                                        false,
                                        DataComponentPredicate.builder()
                                                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(LumenAS.VIREL))
                                                .build(),
                                        ItemsAS.LUMEN_CRYSTAL.asItem()
                                ), 1),
                                new SizedIngredient(Ingredient.of(ItemsAS.RESONATING_GEM), 1)
                        ))
                .addOutputModifier(LiquidStarlightOutputEngraveLumen.builder()
                        .add(CrystalPropertiesAS.TOOL_EFFICIENCY, 7)
                        .add(CrystalPropertiesAS.TOOL_DURABILITY, 1)
                        .add(CrystalPropertiesAS.STARLIGHT_FOCUS, 1)
                        .add(CrystalPropertiesAS.LUMEN_CHANNELING, 1)
                        .build())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("engrave_lumen_solyn",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(
                                new SizedIngredient(DataComponentIngredient.of(
                                        false,
                                        DataComponentPredicate.builder()
                                                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(LumenAS.SOLYN))
                                                .build(),
                                        ItemsAS.LUMEN_CRYSTAL.asItem()
                                ), 1),
                                new SizedIngredient(Ingredient.of(ItemsAS.RESONATING_GEM), 1)
                        ))
                .addOutputModifier(LiquidStarlightOutputEngraveLumen.builder()
                        .add(CrystalPropertiesAS.TOOL_EFFICIENCY, 1)
                        .add(CrystalPropertiesAS.TOOL_DURABILITY, 1)
                        .add(CrystalPropertiesAS.STARLIGHT_FOCUS, 7)
                        .add(CrystalPropertiesAS.LUMEN_CHANNELING, 1)
                        .build())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("engrave_lumen_nullae",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(
                                new SizedIngredient(DataComponentIngredient.of(
                                        false,
                                        DataComponentPredicate.builder()
                                                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(LumenAS.NULLAE))
                                                .build(),
                                        ItemsAS.LUMEN_CRYSTAL.asItem()
                                ), 1),
                                new SizedIngredient(Ingredient.of(ItemsAS.RESONATING_GEM), 1)
                        ))
                .addOutputModifier(LiquidStarlightOutputEngraveLumen.builder()
                        .add(CrystalPropertiesAS.TOOL_EFFICIENCY, 1)
                        .add(CrystalPropertiesAS.TOOL_DURABILITY, 1)
                        .add(CrystalPropertiesAS.STARLIGHT_FOCUS, 1)
                        .add(CrystalPropertiesAS.LUMEN_CHANNELING, 7)
                        .build())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("engrave_lumen_caldor",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(
                                new SizedIngredient(DataComponentIngredient.of(
                                        false,
                                        DataComponentPredicate.builder()
                                                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(LumenAS.CALDOR))
                                                .build(),
                                        ItemsAS.LUMEN_CRYSTAL.asItem()
                                ), 1),
                                new SizedIngredient(Ingredient.of(ItemsAS.RESONATING_GEM), 1)
                        ))
                .addOutputModifier(LiquidStarlightOutputEngraveLumen.builder()
                        .add(CrystalPropertiesAS.TOOL_EFFICIENCY, 1)
                        .add(CrystalPropertiesAS.TOOL_DURABILITY, 7)
                        .add(CrystalPropertiesAS.STARLIGHT_FOCUS, 1)
                        .add(CrystalPropertiesAS.LUMEN_CHANNELING, 1)
                        .build())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);

        LiquidStarlightOutputEngraveLumen.Builder majorBuilder = LiquidStarlightOutputEngraveLumen.builder();
        LiquidStarlightOutputEngraveLumen.Builder minorBuilder = LiquidStarlightOutputEngraveLumen.builder();
        provider.lookupOrThrow(RegistriesAS.KEY_CONSTELLATIONS).listElements().forEach(ref -> {
            CrystalPropertiesAS.getProperty(ref.value()).ifPresent(prop -> {
                if (ref.value().getTier() == BaseConstellation.Tier.MAJOR) {
                    majorBuilder.add(prop, 5);
                    minorBuilder.add(prop, 1);
                } else {
                    majorBuilder.add(prop, 1);
                    minorBuilder.add(prop, 4);
                }
            });
        });
        LiquidStarlightRecipeBuilder.builder("engrave_lumen_dynamis",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(
                                new SizedIngredient(DataComponentIngredient.of(
                                        false,
                                        DataComponentPredicate.builder()
                                                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(LumenAS.DYNAMIS))
                                                .build(),
                                        ItemsAS.LUMEN_CRYSTAL.asItem()
                                ), 1),
                                new SizedIngredient(Ingredient.of(ItemsAS.RESONATING_GEM), 1)
                        ))
                .addOutputModifier(minorBuilder.build())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("engrave_lumen_aion",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(
                                new SizedIngredient(DataComponentIngredient.of(
                                        false,
                                        DataComponentPredicate.builder()
                                                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(LumenAS.AION))
                                                .build(),
                                        ItemsAS.LUMEN_CRYSTAL.asItem()
                                ), 1),
                                new SizedIngredient(Ingredient.of(ItemsAS.RESONATING_GEM), 1)
                        ))
                .addOutputModifier(majorBuilder.build())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);

        LiquidStarlightOutputEngraveLumen.Builder elementaryBuilder = LiquidStarlightOutputEngraveLumen.builder();
        LiquidStarlightOutputEngraveLumen.Builder complexBuilder = LiquidStarlightOutputEngraveLumen.builder();
        provider.lookupOrThrow(RegistriesAS.KEY_LUMEN).listElements().forEach(ref -> {
            CrystalPropertiesAS.getProperty(ref.value()).ifPresent(prop -> {
                if (ref.value().isElementary()) {
                    elementaryBuilder.add(prop, 3);
                    complexBuilder.add(prop, 1);
                } else {
                    elementaryBuilder.add(prop, 1);
                    complexBuilder.add(prop, 3);
                }
            });
        });
        LiquidStarlightRecipeBuilder.builder("engrave_lumen_hyle",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(
                                new SizedIngredient(DataComponentIngredient.of(
                                        false,
                                        DataComponentPredicate.builder()
                                                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(LumenAS.HYLE))
                                                .build(),
                                        ItemsAS.LUMEN_CRYSTAL.asItem()
                                ), 1),
                                new SizedIngredient(Ingredient.of(ItemsAS.RESONATING_GEM), 1)
                        ))
                .addOutputModifier(elementaryBuilder.build())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("engrave_lumen_akasha",
                        new SizedIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(
                                new SizedIngredient(DataComponentIngredient.of(
                                        false,
                                        DataComponentPredicate.builder()
                                                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(LumenAS.AKASHA))
                                                .build(),
                                        ItemsAS.LUMEN_CRYSTAL.asItem()
                                ), 1),
                                new SizedIngredient(Ingredient.of(ItemsAS.RESONATING_GEM), 1)
                        ))
                .addOutputModifier(complexBuilder.build())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
    }
}
