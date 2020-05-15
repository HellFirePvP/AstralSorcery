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
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttunementAltarRecipeProvider
 * Created by HellFirePvP
 * Date: 07.04.2020 / 19:54
 */
public class AttunementAltarRecipeProvider {

    public static void registerAltarRecipes(Consumer<IFinishedRecipe> registrar) {
        //TODO gateway
        registerRecipes(registrar);
    }

    private static void registerRecipes(Consumer<IFinishedRecipe> registrar) {
        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.ALTAR_UPGRADE_CONSTELLATION)
                .createRecipe(BlocksAS.ALTAR_CONSTELLATION, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("S   S")
                        .patternLine(" A A ")
                        .patternLine(" MCM ")
                        .patternLine(" PIP ")
                        .patternLine("M   M")
                        .key('C', new CrystalIngredient(false, false))
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('I', TagsAS.Items.INGOTS_STARMETAL)
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('M', BlocksAS.MARBLE_CHISELED)
                        .key('P', BlocksAS.MARBLE_PILLAR)
                )
                .addOutput(BlocksAS.ALTAR_CONSTELLATION)
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.NBT_COPY)
                .createRecipe(NameUtil.suffixPath(ItemsAS.RESONATOR.getRegistryName(), "_upgrade_domic"), AltarType.ATTUNEMENT)
                .modify(recipe -> recipe.addNBTCopyMatchIngredient(ItemsAS.RESONATOR))
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("I   I")
                        .patternLine(" I I ")
                        .patternLine(" GRG ")
                        .patternLine(" S S ")
                        .patternLine("S   S")
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                        .key('G', ItemsAS.GLASS_LENS)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('R', ItemsAS.RESONATOR)
                )
                .addOutput(ItemResonator.setCurrentUpgradeUnsafe(
                        ItemResonator.setUpgradeUnlocked(new ItemStack(ItemsAS.RESONATOR),
                                ItemResonator.ResonatorUpgrade.STARLIGHT,
                                ItemResonator.ResonatorUpgrade.AREA_SIZE),
                        ItemResonator.ResonatorUpgrade.AREA_SIZE))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.TELESCOPE, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine(" T ")
                        .patternLine("GPG")
                        .patternLine("SSS")
                        .key('T', ItemsAS.HAND_TELESCOPE)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('P', ItemTags.PLANKS)
                        .key('S', Tags.Items.RODS_WOODEN)
                )
                .addOutput(BlocksAS.TELESCOPE)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.RITUAL_PEDESTAL, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("G   G")
                        .patternLine(" MCM ")
                        .patternLine(" PLP ")
                        .patternLine(" RRR ")
                        .patternLine("P   P")
                        .key('C', new CrystalIngredient(false, false))
                        .key('L', FluidsAS.LIQUID_STARLIGHT_SOURCE)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('M', BlocksAS.MARBLE_CHISELED)
                        .key('P', BlocksAS.MARBLE_PILLAR)
                        .key('R', BlocksAS.MARBLE_RUNED)
                )
                .addOutput(BlocksAS.RITUAL_PEDESTAL)
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CRYSTAL_SET_COUNT)
                .createRecipe(BlocksAS.LENS, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.3F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine(" ALA ")
                        .patternLine(" LCL ")
                        .patternLine(" WGW ")
                        .patternLine("R   R")
                        .key('C', new CrystalIngredient(false, false))
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('L', ItemsAS.GLASS_LENS)
                        .key('W', BlocksAS.INFUSED_WOOD_ENGRAVED)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('R', BlocksAS.MARBLE_RUNED)
                )
                .addOutput(BlocksAS.LENS)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.ATTUNEMENT_ALTAR, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.8F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("A   A")
                        .patternLine("  C  ")
                        .patternLine(" M M ")
                        .patternLine(" RSR ")
                        .patternLine("R   R")
                        .key('C', new CrystalIngredient(false, false))
                        .key('A', ItemsAS.AQUAMARINE)
                        .key('M', TagsAS.Items.INGOTS_STARMETAL)
                        .key('S', BlocksAS.SPECTRAL_RELAY)
                        .key('R', BlocksAS.MARBLE_RUNED)
                )
                .addOutput(BlocksAS.ATTUNEMENT_ALTAR)
                .addAltarEffect(AltarRecipeEffectsAS.PILLAR_LIGHTBEAMS)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.PERK_SEAL, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.1F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("N   N")
                        .patternLine("     ")
                        .patternLine(" SLS ")
                        .patternLine("     ")
                        .patternLine("N   N")
                        .key('N', ItemsAS.NOCTURNAL_POWDER)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('L', ItemsAS.GLASS_LENS)
                )
                .addOutput(new ItemStack(ItemsAS.PERK_SEAL, 4))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.KNOWLEDGE_SHARE, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.7F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("I   I")
                        .patternLine("  F  ")
                        .patternLine(" SPS ")
                        .patternLine("  B  ")
                        .patternLine("I   I")
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                        .key('F', Tags.Items.FEATHERS)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('P', ItemsAS.PARCHMENT)
                        .key('B', Tags.Items.DYES_BLACK)
                )
                .addOutput(ItemsAS.KNOWLEDGE_SHARE)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.GRAPPLE_WAND, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.6F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("  SB ")
                        .patternLine(" BR  ")
                        .patternLine(" R   ")
                        .patternLine("R    ")
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('B', Tags.Items.DYES_BLUE)
                        .key('R', BlocksAS.MARBLE_RUNED)
                )
                .addOutput(ItemsAS.GRAPPLE_WAND)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.ARCHITECT_WAND, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.6F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("  SP ")
                        .patternLine(" PRS ")
                        .patternLine(" R   ")
                        .patternLine("R    ")
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('P', Tags.Items.DYES_PURPLE)
                        .key('R', BlocksAS.MARBLE_RUNED)
                )
                .addOutput(ItemsAS.ARCHITECT_WAND)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.EXCHANGE_WAND, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.6F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("  SD ")
                        .patternLine(" DR  ")
                        .patternLine(" R   ")
                        .patternLine("R    ")
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('D', Tags.Items.GEMS_DIAMOND)
                        .key('R', BlocksAS.MARBLE_RUNED)
                )
                .addOutput(ItemsAS.EXCHANGE_WAND)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.BLINK_WAND, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.6F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("  E  ")
                        .patternLine(" ERS ")
                        .patternLine(" RD  ")
                        .patternLine("R    ")
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('D', Tags.Items.GEMS_DIAMOND)
                        .key('E', Tags.Items.ENDER_PEARLS)
                        .key('R', BlocksAS.MARBLE_RUNED)
                )
                .addOutput(ItemsAS.BLINK_WAND)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.LINKING_TOOL, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.3F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine(" SA  ")
                        .patternLine(" SCA ")
                        .patternLine(" LSS ")
                        .patternLine("L    ")
                        .key('C', new CrystalIngredient(false, false))
                        .key('S', Tags.Items.RODS_WOODEN)
                        .key('L', ItemTags.LOGS)
                        .key('A', ItemsAS.AQUAMARINE)
                )
                .addOutput(ItemsAS.LINKING_TOOL)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.SHIFTING_STAR, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.8F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("A   A")
                        .patternLine(" S S ")
                        .patternLine(" ILI ")
                        .patternLine(" S S ")
                        .patternLine("A   A")
                        .key('L', FluidsAS.LIQUID_STARLIGHT_SOURCE)
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('A', ItemsAS.AQUAMARINE)
                )
                .addOutput(ItemsAS.SHIFTING_STAR)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.CHISEL, AltarType.ATTUNEMENT)
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("  GS ")
                        .patternLine("  PG ")
                        .patternLine(" P   ")
                        .patternLine("G    ")
                        .key('S', TagsAS.Items.INGOTS_STARMETAL)
                        .key('G', Tags.Items.NUGGETS_GOLD)
                        .key('P', BlocksAS.INFUSED_WOOD_PLANKS)
                )
                .addOutput(ItemsAS.CHISEL)
                .build(registrar);
    }
}
