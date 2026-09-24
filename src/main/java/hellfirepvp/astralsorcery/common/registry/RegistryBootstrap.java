/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lib.types.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import static hellfirepvp.astralsorcery.common.lib.RegistriesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RegistryBootstrap
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RegistryBootstrap {

    public static void registerDeferredRegisterEvents(IEventBus modLifecycleBus) {
        modLifecycleBus.addListener(RegistryBootstrap::buildRegistries);

        // ------------------ VANILLA ------------------ //
        BlocksAS.BLOCK_REGISTER.register(modLifecycleBus);
        ItemsAS.ITEM_REGISTER.register(modLifecycleBus);
        EntityDataSerializersAS.DATA_SERIALIZERS_REGISTER.register(modLifecycleBus);
        EntitiesAS.ENTITY_REGISTER.register(modLifecycleBus);
        TileEntitiesAS.TILE_REGISTER.register(modLifecycleBus);
        MobEffectsAS.MOB_EFFECT_REGISTER.register(modLifecycleBus);
        CreativeTabsAS.CREATIVE_TAB_REGISTER.register(modLifecycleBus);
        DataComponentsAS.COMPONENT_TYPE_REGISTER.register(modLifecycleBus);
        LootAS.LOOT_FUNCTION_REGISTER.register(modLifecycleBus);
        LootAS.LOOT_CONDITION_REGISTER.register(modLifecycleBus);
        LootAS.GLOBAL_LOOT_FUNCTION_REGISTER.register(modLifecycleBus);
        MenuTypesAS.MENU_TYPE_REGISTER.register(modLifecycleBus);
        IngredientsAS.INGREDIENT_TYPE_REGISTER.register(modLifecycleBus);
        RecipeTypesAS.RECIPE_TYPE_REGISTER.register(modLifecycleBus);
        RecipeTypesAS.RECIPE_SERIALIZER_REGISTER.register(modLifecycleBus);
        FluidsAS.FLUID_REGISTER.register(modLifecycleBus);
        FluidsAS.FLUID_TYPE_REGISTER.register(modLifecycleBus);
        SoundsAS.SOUND_REGISTER.register(modLifecycleBus);
        WorldGenAS.FEATURE_REGISTER.register(modLifecycleBus);
        WorldGenAS.PLACEMENT_REGISTER.register(modLifecycleBus);
        WorldGenAS.STRUCTURE_REGISTER.register(modLifecycleBus);
        WorldGenAS.STRUCTURE_PROCESSOR_REGISTER.register(modLifecycleBus);

        // ------------------ CUSTOM ------------------- //
        ConstellationsAS.CONSTELLATION_REGISTER.register(modLifecycleBus);
        LumenAS.LUMEN_REGISTER.register(modLifecycleBus);
        CrystalPropertiesAS.CRYSTAL_PROPERTY_REGISTER.register(modLifecycleBus);
        StarlightNetworkNodesAS.NODE_REGISTER.register(modLifecycleBus);
        //TODO altar effect registry
        PerkTypesAS.PERK_TYPE_REGISTER.register(modLifecycleBus);
        PerkDataTypesAS.PERK_DATA_TYPE_REGISTER.register(modLifecycleBus);
        PerksAS.PERK_ATTRIBUTE_TYPE_REGISTER.register(modLifecycleBus);
        PerksAS.PERK_ATTRIBUTE_TYPE_READER_REGISTER.register(modLifecycleBus);
        PerksAS.PERK_CONVERTER_REGISTER.register(modLifecycleBus);
        PerksAS.PERK_CUSTOM_MODIFIER_REGISTER.register(modLifecycleBus);
        PerksAS.MODIFIER_SOURCES.register(modLifecycleBus);
        PerksAS.PERK_ATTRIBUTE_LIMITS.register(modLifecycleBus);

        TomePageTypesAS.PAGE_REGISTER.register(modLifecycleBus);
        SyncDataTypesAS.SYNC_DATA_REGISTER.register(modLifecycleBus);
        FocalNodeTypesAS.FOCAL_NODE_REGISTER.register(modLifecycleBus);
        AltarEffectsAS.ALTAR_EFFECT_REGISTER.register(modLifecycleBus);
        ResearchNodeConditionTypesAS.RESEARCH_NODE_CONDITION_REGISTER.register(modLifecycleBus);
        AltarRecipeOutputTypesAS.OUTPUT_MODIFIER_REGISTER.register(modLifecycleBus);
        LiquidStarlightRecipeOutputTypesAS.OUTPUT_MODIFIER_REGISTER.register(modLifecycleBus);
        LiquidInteractionResultTypesAS.RESULT_TYPE_REGISTER.register(modLifecycleBus);
        PerkRequirementsAS.PERK_REQUIREMENT_REGISTER.register(modLifecycleBus);
        ArtifactTypesAS.ARTIFACT_TYPES_REGISTER.register(modLifecycleBus);
        ArtifactConditionTypesAS.ARTIFACT_CONDITION_TYPES_REGISTER.register(modLifecycleBus);
        ArtifactEffectTypesAS.ARTIFACT_EFFECT_TYPES_REGISTER.register(modLifecycleBus);
        LumenBindingUsageTypesAS.LUMEN_BINDING_USAGE_TYPES_REGISTER.register(modLifecycleBus);
        LumenBindingEffectTypesAS.LUMEN_BINDING_USAGE_TYPES_REGISTER.register(modLifecycleBus);

        // ------------------ OBSERVER ----------------- //
        ObserversAS.OBSERVER_REGISTER.register(modLifecycleBus);
    }

    private static void buildRegistries(NewRegistryEvent event) {
        event.register(REGISTRY_CONSTELLATIONS);
        event.register(REGISTRY_LUMEN);
        event.register(REGISTRY_CRYSTAL_PROPERTIES);
        event.register(REGISTRY_TRANSMISSION_NODES);
        event.register(REGISTRY_ALTAR_EFFECTS);
        event.register(REGISTRY_PERK_TYPES);
        event.register(REGISTRY_PERK_DATA_TYPES);
        event.register(REGISTRY_PERK_ATTRIBUTE_TYPE_READERS);
        event.register(REGISTRY_PERK_ATTRIBUTE_TYPES);
        event.register(REGISTRY_PERK_CONVERTERS);
        event.register(REGISTRY_PERK_CUSTOM_MODIFIERS);
        event.register(REGISTRY_PERK_MODIFIER_SOURCES);
        event.register(REGISTRY_PERK_ATTRIBUTE_LIMITS);

        event.register(REGISTRY_TOME_PAGE_TYPES);
        event.register(REGISTRY_SYNC_DATA_TYPES);
        event.register(REGISTRY_FOCAL_NODE_TYPES);
        event.register(REGISTRY_RESEARCH_NODE_CONDITION_TYPES);
        event.register(REGISTRY_ALTAR_OUTPUT_MODIFIER_TYPES);
        event.register(REGISTRY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES);
        event.register(REGISTRY_LIQUID_INTERACTION_RESULT_TYPES);
        event.register(REGISTRY_PERK_REQUIREMENT_TYPES);
        event.register(REGISTRY_ARTIFACT_TYPES);
        event.register(REGISTRY_ARTIFACT_CONDITION_TYPES);
        event.register(REGISTRY_ARTIFACT_EFFECT_TYPES);
        event.register(REGISTRY_LUMEN_BINDING_USAGE_TYPES);
        event.register(REGISTRY_LUMEN_BINDING_EFFECT_TYPES);
    }
}
