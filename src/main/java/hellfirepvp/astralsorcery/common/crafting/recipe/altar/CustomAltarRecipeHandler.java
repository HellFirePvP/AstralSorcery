/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin.*;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomAltarRecipeHandler
 * Created by HellFirePvP
 * Date: 27.09.2019 / 20:05
 */
public class CustomAltarRecipeHandler {

    private static Map<ResourceLocation, Function<SimpleAltarRecipe, ? extends SimpleAltarRecipe>> typeConverterMap = new HashMap<>();

    public static void registerConverter(ResourceLocation name, Function<SimpleAltarRecipe, ? extends SimpleAltarRecipe> converter) {
        typeConverterMap.put(name, converter);
    }

    public static SimpleAltarRecipe convert(SimpleAltarRecipe recipe, ResourceLocation alternativeBase) {
        return typeConverterMap.getOrDefault(alternativeBase, Function.identity()).apply(recipe);
    }

    public static void registerDefaultConverters() {
        registerConverter(AstralSorcery.key("attunement_upgrade"), AttunementUpgradeRecipe::convertToThis);
        registerConverter(AstralSorcery.key("constellation_upgrade"), ConstellationUpgradeRecipe::convertToThis);
        registerConverter(AstralSorcery.key("trait_upgrade"), TraitUpgradeRecipe::convertToThis);

        registerConverter(AstralSorcery.key("constellation_base_average"), ConstellationBaseAverageStatsRecipe::convertToThis);
        registerConverter(AstralSorcery.key("constellation_base_merge"), ConstellationBaseMergeStatsRecipe::convertToThis);
        registerConverter(AstralSorcery.key("constellation_base"), ConstellationBaseItemRecipe::convertToThis);
        registerConverter(AstralSorcery.key("constellation_item"), ConstellationItemRecipe::convertToThis);
        registerConverter(AstralSorcery.key("crystal_count"), CrystalCountRecipe::convertToThis);
    }

}
