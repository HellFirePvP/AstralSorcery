/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.liquid;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.recipe.builder.LiquidInteractionRecipeBuilder;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResultDropItem;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionRecipeProvider
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class LiquidInteractionRecipeProvider {

    private static final int DEFAULT_AMOUNT = 6;
    private static final int OBSIDIAN_AMOUNT = 100;

    private LiquidInteractionRecipeProvider() {}

    public static void registerRecipes(RecipeOutput recipeOutput) {
        SizedFluidIngredient water = SizedFluidIngredient.of(Fluids.WATER, DEFAULT_AMOUNT);
        SizedFluidIngredient lava = SizedFluidIngredient.of(Fluids.LAVA, DEFAULT_AMOUNT);
        SizedFluidIngredient waterBucket = SizedFluidIngredient.of(Fluids.WATER, OBSIDIAN_AMOUNT);
        SizedFluidIngredient lavaBucket = SizedFluidIngredient.of(Fluids.LAVA, OBSIDIAN_AMOUNT);
        SizedFluidIngredient liquidStarlight = SizedFluidIngredient.of(FluidsAS.LIQUID_STARLIGHT.getSource().get(), DEFAULT_AMOUNT);

        LiquidInteractionRecipeBuilder.builder("water_lava_cobblestone", water, lava,
                        new LiquidInteractionResultDropItem(new ItemStack(Items.COBBLESTONE)))
                .chanceConsumeA(0F)
                .chanceConsumeB(0F)
                .weight(6)
                .save(recipeOutput);

        LiquidInteractionRecipeBuilder.builder("water_lava_stone", water, lava,
                        new LiquidInteractionResultDropItem(new ItemStack(Items.STONE)))
                .chanceConsumeA(0.1F)
                .chanceConsumeB(0.1F)
                .weight(3)
                .save(recipeOutput);

        LiquidInteractionRecipeBuilder.builder("water_lava_obsidian", waterBucket, lavaBucket,
                        new LiquidInteractionResultDropItem(new ItemStack(Items.OBSIDIAN)))
                .chanceConsumeA(0.5F)
                .chanceConsumeB(0.5F)
                .weight(1)
                .save(recipeOutput);

        LiquidInteractionRecipeBuilder.builder("liquidstarlight_water_ice", liquidStarlight, water,
                        new LiquidInteractionResultDropItem(new ItemStack(Items.ICE)))
                .chanceConsumeA(1F)
                .chanceConsumeB(1F)
                .weight(1)
                .save(recipeOutput);

        LiquidInteractionRecipeBuilder.builder("liquidstarlight_lava_sand", liquidStarlight, lava,
                        new LiquidInteractionResultDropItem(new ItemStack(Items.SAND)))
                .chanceConsumeA(0.15F)
                .chanceConsumeB(0.15F)
                .weight(49)
                .save(recipeOutput);

        LiquidInteractionRecipeBuilder.builder("liquidstarlight_lava_aquamarine", liquidStarlight, lava,
                        new LiquidInteractionResultDropItem(new ItemStack(BlocksAS.AQUAMARINE_SHALE.get().asItem())))
                .chanceConsumeA(0.5F)
                .chanceConsumeB(0.5F)
                .weight(1)
                .save(recipeOutput);
    }
}
