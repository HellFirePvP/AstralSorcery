/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.loot.*;
import hellfirepvp.astralsorcery.common.loot.condition.LockableTileEntityCondition;
import hellfirepvp.astralsorcery.common.loot.condition.PlayerNearbyCondition;
import hellfirepvp.astralsorcery.common.loot.global.SmeltLootFunction;
import hellfirepvp.astralsorcery.common.loot.global.TeleportDropsFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LootAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LootAS {

    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_REGISTER =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, AstralSorcery.MODID);
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_REGISTER =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, AstralSorcery.MODID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_FUNCTION_REGISTER =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AstralSorcery.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<LinearLuckFunction>> LINEAR_LUCK_FUNCTION =
            LOOT_FUNCTION_REGISTER.register("linear_luck", () -> new LootItemFunctionType<>(LinearLuckFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GenerateCrystalPropertiesFunction>> GENERATE_CRYSTAL_PROPERTIES_FUNCTION =
            LOOT_FUNCTION_REGISTER.register("generate_crystal_properties", () -> new LootItemFunctionType<>(GenerateCrystalPropertiesFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyCrystalPropertiesFunction>> COPY_CRYSTAL_PROPERTIES_FUNCTION =
            LOOT_FUNCTION_REGISTER.register("copy_crystal_properties", () -> new LootItemFunctionType<>(CopyCrystalPropertiesFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyConstellationFunction>> COPY_CONSTELLATION_FUNCTION =
            LOOT_FUNCTION_REGISTER.register("copy_constellation", () -> new LootItemFunctionType<>(CopyConstellationFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyLumenFunction>> COPY_LUMEN_FUNCTION =
            LOOT_FUNCTION_REGISTER.register("copy_lumen", () -> new LootItemFunctionType<>(CopyLumenFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GenerateRandomArtifactFunction>> GENERATE_RANDOM_ARTIFACT_FUNCTION =
            LOOT_FUNCTION_REGISTER.register("generate_random_artifact", () -> new LootItemFunctionType<>(GenerateRandomArtifactFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GenerateRandomArtifactTypeFunction>> GENERATE_RANDOM_ARTIFACT_TYPE_FUNCTION =
            LOOT_FUNCTION_REGISTER.register("generate_random_artifact_type", () -> new LootItemFunctionType<>(GenerateRandomArtifactTypeFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GenerateDynamismGemRollsFunction>> GENERATE_DYNAMISM_GEM_ROLLS_FUNCTION =
            LOOT_FUNCTION_REGISTER.register("generate_dynamism_gem_rolls", () -> new LootItemFunctionType<>(GenerateDynamismGemRollsFunction.CODEC));


    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> PLAYER_NEARBY_CONDITION =
            LOOT_CONDITION_REGISTER.register("player_nearby", () -> new LootItemConditionType(PlayerNearbyCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> LOCKABLE_TILE_ENTITY_CONDITION =
            LOOT_CONDITION_REGISTER.register("lockable_tile_entity", () -> new LootItemConditionType(LockableTileEntityCondition.CODEC));

    public static final Supplier<MapCodec<TeleportDropsFunction>> GLOBAL_TELEPORT_DROPS_FUNCTION =
            GLOBAL_LOOT_FUNCTION_REGISTER.register("teleport_drops", () -> TeleportDropsFunction.CODEC);
    public static final Supplier<MapCodec<SmeltLootFunction>> GLOBAL_SMELT_LOOT_FUNCTION =
            GLOBAL_LOOT_FUNCTION_REGISTER.register("smelt_loot", () -> SmeltLootFunction.CODEC);
}
