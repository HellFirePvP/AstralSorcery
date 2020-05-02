/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.altar;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.builder.SimpleAltarRecipeBuilder;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.CrystalIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeTypeHandler;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin.ConstellationBaseItemRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin.ConstellationBaseNBTCopyRecipe;
import hellfirepvp.astralsorcery.common.item.ItemResonator;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RadianceAltarRecipeProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 20:57
 */
public class RadianceAltarRecipeProvider {

    public static void registerAltarRecipes(Consumer<IFinishedRecipe> registrar) {
        //TODO fountain + primes
        registerRecipes(registrar);
        registerConstellationRecipes(registrar);
    }

    private static void registerRecipes(Consumer<IFinishedRecipe> registrar) {
        SimpleAltarRecipeBuilder.builder()
                .createRecipe(ItemsAS.MANTLE, AltarType.RADIANCE)
                .setStarlightRequirement(0.6F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("R C R")
                        .patternLine("RIAIR")
                        .patternLine("SI IS")
                        .patternLine("S   S")
                        .key('C', new CrystalIngredient(false, false))
                        .key('A', Items.LEATHER_CHESTPLATE)
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                        .key('R', ItemsAS.RESONATING_GEM)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                )
                .addRelayInput(TagsAS.Items.INGOTS_STARMETAL)
                .addRelayInput(TagsAS.Items.DUSTS_STARDUST)
                .addRelayInput(Tags.Items.FEATHERS)
                .addRelayInput(Tags.Items.ENDER_PEARLS)
                .addOutput(ItemsAS.MANTLE)
                .build(registrar);

        SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.NBT_COPY)
                .createRecipe(NameUtil.suffixPath(ItemsAS.RESONATOR.getRegistryName(), "_upgrade_ichosic"), AltarType.RADIANCE)
                .modify(recipe -> recipe.addNBTCopyMatchIngredient(ItemsAS.RESONATOR))
                .setFocusConstellation(ConstellationsAS.octans)
                .setStarlightRequirement(0.8F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("  I  ")
                        .patternLine("S R S")
                        .patternLine(" SLS ")
                        .patternLine("  S  ")
                        .patternLine(" GGG ")
                        .key('R', ItemsAS.RESONATOR)
                        .key('L', FluidsAS.LIQUID_STARLIGHT_SOURCE)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('I', ItemsAS.ILLUMINATION_POWDER)
                        .key('G', ItemsAS.RESONATING_GEM)
                )
                .addOutput(ItemResonator.setCurrentUpgradeUnsafe(
                        ItemResonator.setUpgradeUnlocked(new ItemStack(ItemsAS.RESONATOR),
                                ItemResonator.ResonatorUpgrade.STARLIGHT,
                                ItemResonator.ResonatorUpgrade.FLUID_FIELDS),
                        ItemResonator.ResonatorUpgrade.FLUID_FIELDS))
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.CHALICE, AltarType.RADIANCE)
                .setStarlightRequirement(0.5F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("     ")
                        .patternLine("R   R")
                        .patternLine("RGSGR")
                        .patternLine(" MSM ")
                        .patternLine(" GSG ")
                        .key('R', ItemsAS.RESONATING_GEM)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('S', BlocksAS.BLACK_MARBLE_RAW)
                        .key('M', TagsAS.Items.INGOTS_STARMETAL)
                )
                .addOutput(BlocksAS.CHALICE)
                .addRelayInput(ItemsAS.AQUAMARINE)
                .addRelayInput(ItemsAS.AQUAMARINE)
                .addRelayInput(ItemsAS.AQUAMARINE)
                .addRelayInput(ItemsAS.AQUAMARINE)
                .addRelayInput(ItemsAS.AQUAMARINE)
                .build(registrar);

        SimpleAltarRecipeBuilder.builder()
                .createRecipe(BlocksAS.OBSERVATORY, AltarType.RADIANCE)
                .setFocusConstellation(ConstellationsAS.lucerna)
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("  NRL")
                        .patternLine(" NRIR")
                        .patternLine("NRLRN")
                        .patternLine("G RN ")
                        .patternLine("GGG  ")
                        .key('N', Tags.Items.NUGGETS_GOLD)
                        .key('I', ItemsAS.INFUSED_GLASS)
                        .key('G', Tags.Items.INGOTS_GOLD)
                        .key('R', BlocksAS.MARBLE_RUNED)
                        .key('L', ItemsAS.GLASS_LENS)
                )
                .addOutput(BlocksAS.OBSERVATORY)
                .addRelayInput(ItemsAS.ILLUMINATION_POWDER)
                .addRelayInput(ItemsAS.NOCTURNAL_POWDER)
                .addRelayInput(ItemsAS.ILLUMINATION_POWDER)
                .addRelayInput(TagsAS.Items.DUSTS_STARDUST)
                .addRelayInput(ItemsAS.ILLUMINATION_POWDER)
                .addRelayInput(ItemsAS.NOCTURNAL_POWDER)
                .addRelayInput(ItemsAS.ILLUMINATION_POWDER)
                .addRelayInput(TagsAS.Items.DUSTS_STARDUST)
                .build(registrar);

        registerShiftingStarRecipe(registrar, ConstellationsAS.aevitas, ItemsAS.SHIFTING_STAR_AEVITAS);
        registerShiftingStarRecipe(registrar, ConstellationsAS.armara, ItemsAS.SHIFTING_STAR_ARMARA);
        registerShiftingStarRecipe(registrar, ConstellationsAS.discidia, ItemsAS.SHIFTING_STAR_DISCIDIA);
        registerShiftingStarRecipe(registrar, ConstellationsAS.evorsio, ItemsAS.SHIFTING_STAR_EVORSIO);
        registerShiftingStarRecipe(registrar, ConstellationsAS.vicio, ItemsAS.SHIFTING_STAR_VICIO);
    }

    private static void registerShiftingStarRecipe(Consumer<IFinishedRecipe> registrar, IMajorConstellation constellation, Item shiftingStarItem) {
        Ingredient signature = constellation.getConstellationSignatureItems().get(0);
        SimpleAltarRecipeBuilder.builder()
                .createRecipe(shiftingStarItem, AltarType.RADIANCE)
                .setFocusConstellation(constellation)
                .setStarlightRequirement(0.6F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("  S  ")
                        .patternLine("  C  ")
                        .patternLine("SIBIS")
                        .patternLine("  C  ")
                        .patternLine("  S  ")
                        .key('B', ItemsAS.SHIFTING_STAR)
                        .key('I', TagsAS.Items.INGOTS_STARMETAL)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                        .key('C', signature)
                )
                .addOutput(shiftingStarItem)
                .addRelayInput(signature)
                .addRelayInput(ItemsAS.ILLUMINATION_POWDER)
                .addRelayInput(TagsAS.Items.DUSTS_STARDUST)
                .addRelayInput(signature)
                .addRelayInput(ItemsAS.ILLUMINATION_POWDER)
                .addRelayInput(TagsAS.Items.DUSTS_STARDUST)
                .build(registrar);
    }

    private static void registerConstellationRecipes(Consumer<IFinishedRecipe> registrar) {
        RegistriesAS.REGISTRY_CONSTELLATIONS.forEach(cst -> {
            if (Mods.ASTRAL_SORCERY.owns(cst)) {
                registerConstellationPaperRecipe(registrar, cst);
                if (cst instanceof IWeakConstellation) {
                    registerMantleRecipe(registrar, (IWeakConstellation) cst);
                }
            }
        });
    }

    private static void registerMantleRecipe(Consumer<IFinishedRecipe> registrar, IWeakConstellation constellation) {
        List<Ingredient> signature = constellation.getConstellationSignatureItems();
        if (signature.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a mantle recipe for constellation without signature items: " + constellation.getRegistryName());
        }
        Ingredient center = signature.get(0);


        SimpleAltarRecipeBuilder<ConstellationBaseNBTCopyRecipe> builder = SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_BASE_NBT_COPY)
                .createRecipe(AstralSorcery.key("mantle_" + constellation.getSimpleName()), AltarType.RADIANCE)
                .modify(recipe ->
                        recipe.setConstellation(constellation)
                                .addNBTCopyMatchIngredient(ItemsAS.MANTLE))
                .setFocusConstellation(constellation)
                .multiplyDuration(1.2F)
                .setStarlightRequirement(0.8F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("  S  ")
                        .patternLine("  L  ")
                        .patternLine("SLMLS")
                        .patternLine("  L  ")
                        .patternLine("  S  ")
                        .key('L', center)
                        .key('M', ItemsAS.MANTLE)
                        .key('S', TagsAS.Items.DUSTS_STARDUST))
                .addOutput(ItemsAS.MANTLE);
        signature.forEach(builder::addRelayInput);
        builder.build(registrar);
    }

    private static void registerConstellationPaperRecipe(Consumer<IFinishedRecipe> registrar, IConstellation constellation) {
        List<Ingredient> signature = constellation.getConstellationSignatureItems();
        if (signature.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a constellation paper recipe for constellation without signature items: " + constellation.getRegistryName());
        }
        Ingredient center = signature.get(0);

        SimpleAltarRecipeBuilder<ConstellationBaseItemRecipe> builder = SimpleAltarRecipeBuilder.ofType(AltarRecipeTypeHandler.CONSTELLATION_ITEM_BASE)
                .createRecipe(AstralSorcery.key("constellation_paper_" + constellation.getSimpleName()), AltarType.RADIANCE)
                .modify(recipe -> recipe.setConstellation(constellation))
                .multiplyDuration(0.7F)
                .setStarlightRequirement(0.4F)
                .setInputs(AltarRecipeGrid.builder()
                        .patternLine("  L  ")
                        .patternLine("  F  ")
                        .patternLine("LSPSL")
                        .patternLine("  B  ")
                        .patternLine("  L  ")
                        .key('L', center)
                        .key('B', Tags.Items.DYES_BLACK)
                        .key('P', ItemsAS.PARCHMENT)
                        .key('F', Tags.Items.FEATHERS)
                        .key('S', TagsAS.Items.DUSTS_STARDUST)
                )
                .addOutput(ItemsAS.CONSTELLATION_PAPER);
        signature.forEach(builder::addRelayInput);
        builder.build(registrar);
    }

}
