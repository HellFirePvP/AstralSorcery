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
import hellfirepvp.astralsorcery.common.item.ItemResonator;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DiscoveryAltarRecipeProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 18:10
 */
public class DiscoveryAltarRecipeProvider {

    public static void registerAltarRecipes(Consumer<IFinishedRecipe> registrar) {
        registerRecipes(registrar);
        registerBuildingBlockRecipes(registrar);
    }

    private static void registerRecipes(Consumer<IFinishedRecipe> registrar) {
        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.ALTAR_UPGRADE_ATTUNEMENT)
                .createRecipe(BlocksAS.ALTAR_ATTUNEMENT, AltarType.DISCOVERY)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("PCP")
                        .patternLine("ELE")
                        .patternLine("P P")
                        .key('C', new CrystalIngredient(false, false))
                        .key('L', FluidsAS.LIQUID_STARLIGHT_SOURCE)
                        .key('E', BlocksAS.MARBLE_CHISELED)
                        .key('P', BlocksAS.MARBLE_PILLAR)
                )
                .addOutput(BlocksAS.ALTAR_ATTUNEMENT)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.WAND, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" AE")
                        .patternLine(" MA")
                        .patternLine("M  ")
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('E', Tags.Items.ENDER_PEARLS)
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(ItemsAS.WAND)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.HAND_TELESCOPE, AltarType.DISCOVERY)
                .setStarlightRequirement(0.3F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" SL")
                        .patternLine("SGS")
                        .patternLine("PS ")
                        .key('S', Tags.Items.RODS_WOODEN)
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('P', ItemTags.PLANKS)
                )
                .addOutput(ItemsAS.HAND_TELESCOPE)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.RESONATOR, AltarType.DISCOVERY)
                .setStarlightRequirement(0.15F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" A ")
                        .patternLine("MLM")
                        .patternLine("IGI")
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('M', BlocksAS.MARBLE_RAW)
                        .key('L', FluidsAS.LIQUID_STARLIGHT_SOURCE)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('I', BlocksAS.INFUSED_WOOD)
                )
                .addOutput(ItemResonator.setCurrentUpgradeUnsafe(
                        ItemResonator.setUpgradeUnlocked(new ItemStack(ItemsAS.RESONATOR),
                                ItemResonator.ResonatorUpgrade.STARLIGHT),
                        ItemResonator.ResonatorUpgrade.STARLIGHT))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.TOME, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" P ")
                        .patternLine("ABA")
                        .patternLine(" A ")
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('B', Items.BOOK)
                        .key('P', ItemsAS.PARCHMENT)
                )
                .addOutput(ItemsAS.TOME)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.PARCHMENT, AltarType.DISCOVERY)
                .setStarlightRequirement(0.1F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" P ")
                        .patternLine("PAP")
                        .patternLine(" P ")
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('P', Items.PAPER)
                )
                .addOutput(ItemsAS.PARCHMENT)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.GLASS_LENS, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" P ")
                        .patternLine("PAP")
                        .patternLine(" P ")
                        .key('A', Tags.Items.GLASS_PANES)
                        .key('P', ItemsAS.AQUAMARINE)
                )
                .addOutput(ItemsAS.GLASS_LENS)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.SPECTRAL_RELAY, AltarType.DISCOVERY)
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("NGN")
                        .patternLine("PMP")
                        .key('N', Tags.Items.NUGGETS_GOLD)
                        .key('G', ItemsAS.GLASS_LENS)
                        .key('P', ItemTags.PLANKS)
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(BlocksAS.SPECTRAL_RELAY)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.WELL, AltarType.DISCOVERY)
                .setStarlightRequirement(0.3F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("R R")
                        .patternLine("CZC")
                        .patternLine("ARA")
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('R', BlocksAS.MARBLE_RUNED)
                        .key('C', BlocksAS.MARBLE_CHISELED)
                        .key('Z', new CrystalIngredient(false, false))
                )
                .addOutput(BlocksAS.WELL)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.ILLUMINATOR, AltarType.DISCOVERY)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("RIR")
                        .patternLine("A A")
                        .patternLine("RIR")
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('R', BlocksAS.MARBLE_RUNED)
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                )
                .addOutput(BlocksAS.ILLUMINATOR)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.ILLUMINATION_POWDER, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.6F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" G ")
                        .patternLine("GAG")
                        .patternLine(" G ")
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('G', Tags.Items.DUSTS_GLOWSTONE)
                )
                .addOutput(new ItemStack(ItemsAS.ILLUMINATION_POWDER, 16))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.NOCTURNAL_POWDER, AltarType.DISCOVERY)
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" D ")
                        .patternLine("CPC")
                        .patternLine(" B ")
                        .key('C', ItemTags.COALS)
                        .key('B', Tags.Items.DYES_BLUE)
                        .key('D', Tags.Items.DYES_BLACK)
                        .key('P', ItemsAS.ILLUMINATION_POWDER)
                )
                .addOutput(new ItemStack(ItemsAS.NOCTURNAL_POWDER, 4))
                .build(registrar);


        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_CRYSTAL_MERGE)
                .createRecipe(ItemsAS.CRYSTAL_AXE, AltarType.DISCOVERY)
                .setStarlightRequirement(0.5F)
                .multiplyDuration(1.75F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("CC")
                        .patternLine("CS")
                        .patternLine(" S")
                        .key('S', Tags.Items.RODS_WOODEN)
                        .key('C', new CrystalIngredient(false, false))
                )
                .addOutput(ItemsAS.CRYSTAL_AXE)
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_CRYSTAL_MERGE)
                .createRecipe(ItemsAS.CRYSTAL_PICKAXE, AltarType.DISCOVERY)
                .setStarlightRequirement(0.5F)
                .multiplyDuration(1.75F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("CCC")
                        .patternLine(" S ")
                        .patternLine(" S ")
                        .key('S', Tags.Items.RODS_WOODEN)
                        .key('C', new CrystalIngredient(false, false))
                )
                .addOutput(ItemsAS.CRYSTAL_PICKAXE)
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_CRYSTAL_MERGE)
                .createRecipe(ItemsAS.CRYSTAL_SHOVEL, AltarType.DISCOVERY)
                .setStarlightRequirement(0.5F)
                .multiplyDuration(1.75F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("C")
                        .patternLine("S")
                        .patternLine("S")
                        .key('S', Tags.Items.RODS_WOODEN)
                        .key('C', new CrystalIngredient(false, false))
                )
                .addOutput(ItemsAS.CRYSTAL_SHOVEL)
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_CRYSTAL_MERGE)
                .createRecipe(ItemsAS.CRYSTAL_SWORD, AltarType.DISCOVERY)
                .setStarlightRequirement(0.5F)
                .multiplyDuration(1.75F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("C")
                        .patternLine("C")
                        .patternLine("S")
                        .key('S', Tags.Items.RODS_WOODEN)
                        .key('C', new CrystalIngredient(false, false))
                )
                .addOutput(ItemsAS.CRYSTAL_SWORD)
                .build(registrar);
    }

    private static void registerBuildingBlockRecipes(Consumer<IFinishedRecipe> registrar) {
        /*****************************************************************************
         * Infused wood
         *****************************************************************************/
        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.INFUSED_WOOD_ARCH, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("PP")
                        .key('P', BlocksAS.INFUSED_WOOD_PLANKS)
                )
                .addOutput(new ItemStack(BlocksAS.INFUSED_WOOD_ARCH, 2))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.INFUSED_WOOD_COLUMN, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("P")
                        .patternLine("P")
                        .key('P', BlocksAS.INFUSED_WOOD_PLANKS)
                )
                .addOutput(new ItemStack(BlocksAS.INFUSED_WOOD_COLUMN, 2))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.INFUSED_WOOD_ENGRAVED, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" P ")
                        .patternLine("P P")
                        .patternLine(" P ")
                        .key('P', BlocksAS.INFUSED_WOOD_PLANKS)
                )
                .addOutput(new ItemStack(BlocksAS.INFUSED_WOOD_ENGRAVED, 4))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.INFUSED_WOOD_ENRICHED, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" P ")
                        .patternLine("PAP")
                        .patternLine(" P ")
                        .key('P', BlocksAS.INFUSED_WOOD_PLANKS)
                        .key('A', ItemsAS.AQUAMARINE)
                )
                .addOutput(new ItemStack(BlocksAS.INFUSED_WOOD_ENRICHED, 4))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.INFUSED_WOOD_PLANKS, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("L")
                        .key('L', BlocksAS.INFUSED_WOOD)
                )
                .addOutput(new ItemStack(BlocksAS.INFUSED_WOOD_PLANKS, 4))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.INFUSED_WOOD_SLAB, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("PPP")
                        .key('P', BlocksAS.INFUSED_WOOD_PLANKS)
                )
                .addOutput(new ItemStack(BlocksAS.INFUSED_WOOD_SLAB, 6))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.INFUSED_WOOD_STAIRS, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("P  ")
                        .patternLine("PP ")
                        .patternLine("PPP")
                        .key('P', BlocksAS.INFUSED_WOOD_PLANKS)
                )
                .addOutput(new ItemStack(BlocksAS.INFUSED_WOOD_STAIRS, 8))
                .build(registrar);

        /*****************************************************************************
         * Marble
         *****************************************************************************/
        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.MARBLE_ARCH, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MM")
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.MARBLE_ARCH, 2))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.MARBLE_PILLAR, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("M")
                        .patternLine("M")
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.MARBLE_PILLAR, 2))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.MARBLE_BRICKS, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MM")
                        .patternLine("MM")
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.MARBLE_BRICKS, 4))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.MARBLE_CHISELED, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" M ")
                        .patternLine("M M")
                        .patternLine(" M ")
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.MARBLE_CHISELED, 4))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.MARBLE_ENGRAVED, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" M ")
                        .patternLine("MMM")
                        .patternLine(" M ")
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.MARBLE_ENGRAVED, 5))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.MARBLE_RUNED, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MCM")
                        .key('M', BlocksAS.MARBLE_RAW)
                        .key('C', BlocksAS.MARBLE_CHISELED)
                )
                .addOutput(new ItemStack(BlocksAS.MARBLE_RUNED, 3))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.MARBLE_SLAB, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MMM")
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.MARBLE_SLAB, 6))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.MARBLE_STAIRS, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("M  ")
                        .patternLine("MM ")
                        .patternLine("MMM")
                        .key('M', BlocksAS.MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.MARBLE_STAIRS, 8))
                .build(registrar);

        /*****************************************************************************
         * Sooty Marble
         *****************************************************************************/
        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_RAW, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MMM")
                        .patternLine("MCM")
                        .patternLine("MMM")
                        .key('M', BlocksAS.MARBLE_RAW)
                        .key('C', ItemTags.COALS)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_RAW, 8))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_PILLAR, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("M")
                        .patternLine("M")
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_PILLAR, 2))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_ARCH, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MM")
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_ARCH, 2))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_BRICKS, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MM")
                        .patternLine("MM")
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_BRICKS, 4))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_CHISELED, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" M ")
                        .patternLine("M M")
                        .patternLine(" M ")
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_CHISELED, 4))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_ENGRAVED, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" M ")
                        .patternLine("MMM")
                        .patternLine(" M ")
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_ENGRAVED, 5))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_RUNED, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MCM")
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                        .key('C', BlocksAS.BLACK_MARBLE_CHISELED)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_RUNED, 3))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_SLAB, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("MMM")
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_SLAB, 6))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.BLACK_MARBLE_STAIRS, AltarType.DISCOVERY)
                .setStarlightRequirement(0.2F)
                .multiplyDuration(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("M  ")
                        .patternLine("MM ")
                        .patternLine("MMM")
                        .key('M', BlocksAS.BLACK_MARBLE_RAW)
                )
                .addOutput(new ItemStack(BlocksAS.BLACK_MARBLE_STAIRS, 8))
                .build(registrar);
    }
}
