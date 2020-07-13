/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.altar;

import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.builder.SimpleAltarRecipeBuilder;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.CrystalIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeTypeHandler;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.TagsAS;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialAltarRecipeProvider
 * Created by HellFirePvP
 * Date: 07.04.2020 / 20:52
 */
public class CelestialAltarRecipeProvider {

    public static void registerAltarRecipes(Consumer<IFinishedRecipe> registrar) {
        //TODO tree beacon
        registerRecipes(registrar);
        registerColoredLensRecipes(registrar);
    }

    private static void registerRecipes(Consumer<IFinishedRecipe> registrar) {
        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.ALTAR_UPGRADE_TRAIT)
                .createRecipe(BlocksAS.ALTAR_RADIANCE, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.8F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("RM MR")
                        .patternLine("ARLRA")
                        .patternLine("  C  ")
                        .patternLine("AR RA")
                        .patternLine("RM MR")
                        .key('C', new CrystalIngredient(false, true))
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('A', ItemsAS.RESONATING_GEM)
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                        .key('R', BlocksAS.MARBLE_RUNED)
                )
                .addOutput(BlocksAS.ALTAR_RADIANCE)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.REFRACTION_TABLE, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("D   D")
                        .patternLine("PR RP")
                        .patternLine(" S S ")
                        .patternLine("PBBBP")
                        .patternLine("M   M")
                        .key('S', TagsAS.Items.INGOTS_STARMETAL)
                        .key('R', ItemsAS.RESONATING_GEM)
                        .key('M', BlocksAS.MARBLE_RUNED)
                        .key('B', BlocksAS.BLACK_MARBLE_RUNED)
                        .key('D', Tags.Items.DYES)
                        .key('P', BlocksAS.INFUSED_WOOD_COLUMN)
                )
                .addOutput(BlocksAS.REFRACTION_TABLE)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.INFUSED_GLASS, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.6F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("S   S")
                        .patternLine("S   S")
                        .patternLine(" GLG ")
                        .patternLine("S   S")
                        .patternLine("S   S")
                        .key('L', TagsAS.Items.COLORED_LENS)
                        .key('G', ItemsAS.GLASS_LENS)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                )
                .addOutput(ItemsAS.INFUSED_GLASS)
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_CRYSTAL_AVERAGE)
                .createRecipe(BlocksAS.PRISM, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" A A ")
                        .patternLine("GPSPG")
                        .patternLine(" LCL ")
                        .patternLine("EPSPE")
                        .patternLine("RR RR")
                        .key('C', new CrystalIngredient(false, false))
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('A', ItemsAS.RESONATING_GEM)
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('P', Tags.Items.GLASS_PANES)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('R', BlocksAS.MARBLE_RUNED)
                        .key('E', BlocksAS.INFUSED_WOOD_ENGRAVED)
                )
                .addOutput(BlocksAS.PRISM)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(NameUtil.suffixPath(ItemsAS.ENCHANTMENT_AMULET.getRegistryName(), "_init"), AltarType.CONSTELLATION)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine(" GTG ")
                        .patternLine(" SZS ")
                        .patternLine("S E S")
                        .patternLine(" S S ")
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('T', Tags.Items.STRING)
                        .key('E', Items.ENDER_EYE)
                        .key('Z', ItemsAS.SHIFTING_STAR)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                )
                .addOutput(ItemsAS.ENCHANTMENT_AMULET)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(NameUtil.suffixPath(ItemsAS.ENCHANTMENT_AMULET.getRegistryName(), "_reroll"), AltarType.CONSTELLATION)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("  R  ")
                        .patternLine("  A  ")
                        .patternLine("SSLSS")
                        .patternLine("     ")
                        .key('L', FluidsAS.LIQUID_STARLIGHT_SOURCE)
                        .key('A', ItemsAS.ENCHANTMENT_AMULET)
                        .key('R', ItemsAS.RESONATING_GEM)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                )
                .addOutput(ItemsAS.ENCHANTMENT_AMULET)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.RITUAL_LINK, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" N N ")
                        .patternLine(" NGN ")
                        .patternLine(" LAL ")
                        .patternLine(" PSP ")
                        .patternLine(" P P ")
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('P', Tags.Items.GLASS_PANES)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('N', Tags.Items.NUGGETS_GOLD)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('A', ItemsAS.RESONATING_GEM)
                )
                .addOutput(new ItemStack(BlocksAS.RITUAL_LINK, 2))
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_CRYSTAL_AVERAGE)
                .createRecipe(BlocksAS.ROCK_COLLECTOR_CRYSTAL, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.8F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("SI IS")
                        .patternLine("R   R")
                        .patternLine("  C  ")
                        .patternLine("R   R")
                        .patternLine("SI IS")
                        .key('C', new CrystalIngredient(true, false, false, false))
                        .key('R', ItemsAS.RESONATING_GEM)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                )
                .addOutput(BlocksAS.ROCK_COLLECTOR_CRYSTAL)
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_CRYSTAL_AVERAGE)
                .createRecipe(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.8F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("SI IS")
                        .patternLine("R   R")
                        .patternLine("  C  ")
                        .patternLine("R   R")
                        .patternLine("SI IS")
                        .key('C', new CrystalIngredient(true, true, false, true))
                        .key('R', ItemsAS.RESONATING_GEM)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                )
                .addOutput(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.ILLUMINATION_WAND, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("   I ")
                        .patternLine("  ISI")
                        .patternLine("  RI ")
                        .patternLine(" R   ")
                        .patternLine("R    ")
                        .key('R', BlocksAS.MARBLE_RUNED)
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                        .key('S', ItemsAS.SHIFTING_STAR)
                )
                .addOutput(ItemsAS.ILLUMINATION_WAND)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.INFUSER, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.3F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("PASAP")
                        .patternLine(" GLG ")
                        .patternLine("PRRRP")
                        .patternLine("PG GP")
                        .key('P', BlocksAS.MARBLE_PILLAR)
                        .key('R', BlocksAS.MARBLE_RUNED)
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('L', FluidsAS.LIQUID_STARLIGHT_SOURCE)
                        .key('S', TagsAS.Items.INGOTS_STARMETAL)
                )
                .addOutput(BlocksAS.INFUSER)
                .build(registrar);
    }

    private static void registerColoredLensRecipes(Consumer<IFinishedRecipe> registrar) {
        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.COLORED_LENS_SPECTRAL, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.5F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("S   S")
                        .patternLine("  R  ")
                        .patternLine(" ILI ")
                        .patternLine("  R  ")
                        .patternLine("S   S")
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                        .key('R', ItemsAS.RESONATING_GEM)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                )
                .addOutput(ItemsAS.COLORED_LENS_SPECTRAL)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.COLORED_LENS_FIRE, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.5F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("B   B")
                        .patternLine("B A B")
                        .patternLine("  L  ")
                        .patternLine("B A B")
                        .patternLine("B   B")
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('B', Items.BLAZE_POWDER)
                        .key('A', ItemsAS.AQUAMARINE)
                )
                .addOutput(ItemsAS.COLORED_LENS_FIRE)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.COLORED_LENS_BREAK, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.5F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("A   A")
                        .patternLine("  D  ")
                        .patternLine("  L  ")
                        .patternLine("G P G")
                        .patternLine("     ")
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('P', Items.IRON_PICKAXE)
                        .key('D', Tags.Items.GEMS_DIAMOND)
                )
                .addOutput(ItemsAS.COLORED_LENS_BREAK)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.COLORED_LENS_DAMAGE, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.5F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("I D I")
                        .patternLine(" ILI ")
                        .patternLine(" F F ")
                        .patternLine("A   A")
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('I', Tags.Items.INGOTS_IRON)
                        .key('D', Tags.Items.GEMS_DIAMOND)
                        .key('F', Items.FLINT)
                )
                .addOutput(ItemsAS.COLORED_LENS_DAMAGE)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.COLORED_LENS_GROWTH, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.5F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("AK KA")
                        .patternLine("  A  ")
                        .patternLine(" CLC ")
                        .patternLine("     ")
                        .patternLine("AS SA")
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('C', Tags.Items.CROPS_CARROT)
                        .key('K', Items.SUGAR_CANE)
                        .key('S', Tags.Items.SEEDS)
                )
                .addOutput(ItemsAS.COLORED_LENS_GROWTH)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.COLORED_LENS_PUSH, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.5F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("G   G")
                        .patternLine("AF FA")
                        .patternLine("  L  ")
                        .patternLine("     ")
                        .patternLine("G   G")
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('F', Tags.Items.FEATHERS)
                        .key('G', Tags.Items.DUSTS_GLOWSTONE)
                )
                .addOutput(ItemsAS.COLORED_LENS_PUSH)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.COLORED_LENS_REGENERATION, AltarType.CONSTELLATION)
                .setStarlightRequirement(0.5F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("A   A")
                        .patternLine("  T  ")
                        .patternLine("  L  ")
                        .patternLine("S D S")
                        .patternLine("     ")
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('D', Tags.Items.GEMS_DIAMOND)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('T', Items.GHAST_TEAR)
                )
                .addOutput(ItemsAS.COLORED_LENS_REGENERATION)
                .build(registrar);
    }
}
