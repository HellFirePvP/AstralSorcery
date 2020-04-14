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
import hellfirepvp.astralsorcery.common.crafting.builder.SimpleAltarRecipeBuilder;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeTypeHandler;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin.ConstellationBaseItemRecipe;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.TagsAS;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RadianceAltarRecipeProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 20:57
 */
public class RadianceAltarRecipeProvider {

    public static void registerAltarRecipes(Consumer<IFinishedRecipe> registrar) {
        //TODO ichosic, fountain + primes
        //TODO now: base + attuned mantles, chalice, shifting stars, observatory
        registerConstellationPaperRecipes(registrar);
    }

    private static void registerConstellationPaperRecipes(Consumer<IFinishedRecipe> registrar) {
        RegistriesAS.REGISTRY_CONSTELLATIONS.forEach(cst -> {
            if (Mods.ASTRAL_SORCERY.owns(cst)) {
                registerConstellationPaperRecipe(registrar, cst);
            }
        });
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
