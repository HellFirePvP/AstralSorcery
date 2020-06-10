/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.vanilla;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.datagen.data.recipe.builder.ResultCookingRecipeBuilder;
import hellfirepvp.astralsorcery.datagen.data.recipe.builder.SimpleShapedRecipeBuilder;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VanillaTypedRecipeProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 18:09
 */
public class VanillaTypedRecipeProvider {

    public static void registerShapedRecipes(Consumer<IFinishedRecipe> registrar) {
        SimpleShapedRecipeBuilder.shapedRecipe(ItemsAS.TOME)
                .patternLine(" P ")
                .patternLine("ABA")
                .patternLine(" A ")
                .key('A', ItemsAS.AQUAMARINE)
                .key('B', Items.BOOK)
                .key('P', ItemsAS.PARCHMENT)
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(ItemsAS.PARCHMENT, 4)
                .patternLine(" P ")
                .patternLine("PAP")
                .patternLine(" P ")
                .key('A', ItemsAS.AQUAMARINE)
                .key('P', Items.PAPER)
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(ItemsAS.WAND)
                .patternLine(" AE")
                .patternLine(" MA")
                .patternLine("M  ")
                .key('A', ItemsAS.AQUAMARINE)
                .key('M', BlocksAS.MARBLE_RAW)
                .key('E', Tags.Items.ENDER_PEARLS)
                .build(registrar);

        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.MARBLE_ARCH, 2)
                .patternLine("MM")
                .key('M', BlocksAS.MARBLE_RAW)
                .subDirectory("marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.MARBLE_BRICKS, 4)
                .patternLine("MM")
                .patternLine("MM")
                .key('M', BlocksAS.MARBLE_RAW)
                .subDirectory("marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.MARBLE_CHISELED, 4)
                .patternLine(" M ")
                .patternLine("M M")
                .patternLine(" M ")
                .key('M', BlocksAS.MARBLE_RAW)
                .subDirectory("marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.MARBLE_ENGRAVED, 5)
                .patternLine(" M ")
                .patternLine("MMM")
                .patternLine(" M ")
                .key('M', BlocksAS.MARBLE_RAW)
                .subDirectory("marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.MARBLE_PILLAR, 2)
                .patternLine("M")
                .patternLine("M")
                .key('M', BlocksAS.MARBLE_RAW)
                .subDirectory("marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.MARBLE_RUNED, 3)
                .patternLine("MCM")
                .key('M', BlocksAS.MARBLE_RAW)
                .key('C', BlocksAS.MARBLE_CHISELED)
                .subDirectory("marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.MARBLE_SLAB, 6)
                .patternLine("MMM")
                .key('M', BlocksAS.MARBLE_RAW)
                .subDirectory("marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.MARBLE_STAIRS, 8)
                .patternLine("M  ")
                .patternLine("MM ")
                .patternLine("MMM")
                .key('M', BlocksAS.MARBLE_RAW)
                .subDirectory("marble")
                .build(registrar);

        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_RAW, 8)
                .patternLine("MMM")
                .patternLine("MCM")
                .patternLine("MMM")
                .key('M', BlocksAS.MARBLE_RAW)
                .key('C', ItemTags.COALS)
                .subDirectory("black_marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_ARCH, 2)
                .patternLine("MM")
                .key('M', BlocksAS.BLACK_MARBLE_RAW)
                .subDirectory("black_marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_BRICKS, 4)
                .patternLine("MM")
                .patternLine("MM")
                .key('M', BlocksAS.BLACK_MARBLE_RAW)
                .subDirectory("black_marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_CHISELED, 4)
                .patternLine(" M ")
                .patternLine("M M")
                .patternLine(" M ")
                .key('M', BlocksAS.BLACK_MARBLE_RAW)
                .subDirectory("black_marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_ENGRAVED, 5)
                .patternLine(" M ")
                .patternLine("MMM")
                .patternLine(" M ")
                .key('M', BlocksAS.BLACK_MARBLE_RAW)
                .subDirectory("black_marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_PILLAR, 2)
                .patternLine("M")
                .patternLine("M")
                .key('M', BlocksAS.BLACK_MARBLE_RAW)
                .subDirectory("marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_RUNED, 3)
                .patternLine("MCM")
                .key('M', BlocksAS.BLACK_MARBLE_RAW)
                .key('C', BlocksAS.BLACK_MARBLE_CHISELED)
                .subDirectory("black_marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_SLAB, 6)
                .patternLine("MMM")
                .key('M', BlocksAS.BLACK_MARBLE_RAW)
                .subDirectory("black_marble")
                .build(registrar);
        SimpleShapedRecipeBuilder.shapedRecipe(BlocksAS.BLACK_MARBLE_STAIRS, 8)
                .patternLine("M  ")
                .patternLine("MM ")
                .patternLine("MMM")
                .key('M', BlocksAS.BLACK_MARBLE_RAW)
                .subDirectory("black_marble")
                .build(registrar);
    }

    public static void registerCookingRecipes(Consumer<IFinishedRecipe> registrar) {
        ResultCookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(BlocksAS.STARMETAL_ORE), new ItemStack(ItemsAS.STARMETAL_INGOT), 1.8F, 200)
                .build(registrar);
        ResultCookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(BlocksAS.AQUAMARINE_SAND_ORE), new ItemStack(ItemsAS.AQUAMARINE, 4), 1.8F, 200)
                .build(registrar);

        ResultCookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(BlocksAS.STARMETAL_ORE), new ItemStack(ItemsAS.STARMETAL_INGOT), 2.5F, 80)
                .build(registrar);
        ResultCookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(BlocksAS.AQUAMARINE_SAND_ORE), new ItemStack(ItemsAS.AQUAMARINE, 3), 0.2F, 40)
                .build(registrar);
    }

    public static void registerCustomRecipes(Consumer<IFinishedRecipe> registrar) {
        CustomRecipeBuilder.customRecipe(RecipeSerializersAS.CUSTOM_CHANGE_WAND_COLOR_SERIALIZER).build(registrar, AstralSorcery.key("change_wand_color").toString());
    }

}
