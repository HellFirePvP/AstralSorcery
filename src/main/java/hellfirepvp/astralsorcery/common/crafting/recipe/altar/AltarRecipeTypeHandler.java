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
 * Class: AltarRecipeTypeHandler
 * Created by HellFirePvP
 * Date: 27.09.2019 / 20:05
 */
public class AltarRecipeTypeHandler {

    public static final Type<AttunementUpgradeRecipe> ALTAR_UPGRADE_ATTUNEMENT = new Type<>(AstralSorcery.key("attunement_upgrade"));
    public static final Type<ConstellationUpgradeRecipe> ALTAR_UPGRADE_CONSTELLATION = new Type<>(AstralSorcery.key("constellation_upgrade"));
    public static final Type<TraitUpgradeRecipe> ALTAR_UPGRADE_TRAIT = new Type<>(AstralSorcery.key("trait_upgrade"));

    public static final Type<ConstellationBaseAverageStatsRecipe> CONSTELLATION_CRYSTAL_AVERAGE = new Type<>(AstralSorcery.key("constellation_base_average"));
    public static final Type<ConstellationBaseMergeStatsRecipe> CONSTELLATION_CRYSTAL_MERGE = new Type<>(AstralSorcery.key("constellation_base_merge"));
    public static final Type<ConstellationBaseItemRecipe> CONSTELLATION_ITEM_BASE = new Type<>(AstralSorcery.key("constellation_base"));
    public static final Type<ConstellationItemRecipe> CONSTELLATION_ITEM = new Type<>(AstralSorcery.key("constellation_item"));
    public static final Type<CrystalCountRecipe> CRYSTAL_SET_COUNT = new Type<>(AstralSorcery.key("crystal_count"));
    public static final Type<ConstellationBaseNBTCopyRecipe> CONSTELLATION_BASE_NBT_COPY = new Type<>(AstralSorcery.key("constellation_base_nbt_copy"));
    public static final Type<NBTCopyRecipe> NBT_COPY = new Type<>(AstralSorcery.key("nbt_copy"));
    public static final Type<SimpleAltarRecipe> DEFAULT = new Type<>(AstralSorcery.key("default"));

    private static Map<ResourceLocation, Type<?>> typeConverterMap = new HashMap<>();

    public static <T extends SimpleAltarRecipe> Type<T> registerConverter(ResourceLocation name, Function<SimpleAltarRecipe, T> converter) {
        Type<T> type = new Type<>(name, converter);
        typeConverterMap.put(name, type);
        return type;
    }

    private static <T extends SimpleAltarRecipe> void registerInternal(Type<T> type, Function<SimpleAltarRecipe, T> converter) {
        type.converter = converter;
        typeConverterMap.put(type.key, type);
    }

    public static <T extends SimpleAltarRecipe> T convert(SimpleAltarRecipe recipe, ResourceLocation alternativeBase) {
        return (T) typeConverterMap.getOrDefault(alternativeBase, DEFAULT).convert(recipe);
    }

    public static void registerDefaultConverters() {
        registerInternal(ALTAR_UPGRADE_ATTUNEMENT, AttunementUpgradeRecipe::convertToThis);
        registerInternal(ALTAR_UPGRADE_CONSTELLATION, ConstellationUpgradeRecipe::convertToThis);
        registerInternal(ALTAR_UPGRADE_TRAIT, TraitUpgradeRecipe::convertToThis);

        registerInternal(CONSTELLATION_CRYSTAL_AVERAGE, ConstellationBaseAverageStatsRecipe::convertToThis);
        registerInternal(CONSTELLATION_CRYSTAL_MERGE, ConstellationBaseMergeStatsRecipe::convertToThis);
        registerInternal(CONSTELLATION_ITEM_BASE, ConstellationBaseItemRecipe::convertToThis);
        registerInternal(CONSTELLATION_ITEM, ConstellationItemRecipe::convertToThis);
        registerInternal(CRYSTAL_SET_COUNT, CrystalCountRecipe::convertToThis);
        registerInternal(CONSTELLATION_BASE_NBT_COPY, ConstellationBaseNBTCopyRecipe::convertToThis);
        registerInternal(NBT_COPY, NBTCopyRecipe::convertToThis);
        registerInternal(DEFAULT, Function.identity());
    }

    public static class Type<T extends SimpleAltarRecipe> {

        private final ResourceLocation key;
        private Function<SimpleAltarRecipe, T> converter;

        private Type(ResourceLocation key) {
            this.key = key;
        }

        private Type(ResourceLocation key, Function<SimpleAltarRecipe, T> converter) {
            this.key = key;
            this.converter = converter;
        }

        public T convert(SimpleAltarRecipe recipe) {
            return this.converter.apply(recipe);
        }

        public final ResourceLocation getKey() {
            return key;
        }
    }
}
