/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.ArtifactCondition;
import hellfirepvp.astralsorcery.common.artifact.ArtifactEffect;
import hellfirepvp.astralsorcery.common.artifact.ArtifactType;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.data.sync.SyncData;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.effect.LumenBindingEffect;
import hellfirepvp.astralsorcery.common.lumen.binding.usage.LumenBindingUsage;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeLimiter;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.reader.PerkAttributeTypeReader;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.perk.tree.PerkDataType;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.recipe.altar.effect.AltarEffect;
import hellfirepvp.astralsorcery.common.recipe.altar.output.AltarRecipeOutputModifier;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResult;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.LiquidStarlightRecipeOutputModifier;
import hellfirepvp.astralsorcery.common.research.condition.ResearchNodeCondition;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.focal.node.FocalPointNode;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RegistriesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RegistriesAS {

    public static final ResourceKey<Registry<BaseConstellation>> KEY_CONSTELLATIONS = registryKey("constellations");
    public static final ResourceKey<Registry<Lumen>> KEY_LUMEN = registryKey("lumen");
    public static final ResourceKey<Registry<CrystalProperty>> KEY_CRYSTAL_PROPERTIES = registryKey("crystal_properties");
    public static final ResourceKey<Registry<TransmissionNodeProvider<?>>> KEY_TRANSMISSION_NODES = registryKey("transmission_nodes");
    public static final ResourceKey<Registry<AltarEffect>> KEY_ALTAR_EFFECTS = registryKey("altar_effects");
    public static final ResourceKey<Registry<PerkType<?>>> KEY_PERK_TYPES = registryKey("perk_types");
    public static final ResourceKey<Registry<PerkDataType<?>>> KEY_PERK_DATA_TYPES = registryKey("perk_data_types");
    public static final ResourceKey<Registry<PerkAttributeTypeReader.Type>> KEY_PERK_ATTRIBUTE_TYPE_READERS = registryKey("perk_attribute_type_readers");
    public static final ResourceKey<Registry<PerkAttributeType>> KEY_PERK_ATTRIBUTE_TYPES = registryKey("perk_attribute_types");
    public static final ResourceKey<Registry<PerkAttributeConverter>> KEY_PERK_CONVERTERS = registryKey("perk_converters");
    public static final ResourceKey<Registry<PerkAttributeModifier>> KEY_PERK_CUSTOM_MODIFIERS = registryKey("perk_custom_modifiers");
    public static final ResourceKey<Registry<ModifierSourceProvider<?>>> KEY_PERK_MODIFIER_SOURCES = registryKey("perk_modifier_sources");
    public static final ResourceKey<Registry<PerkAttributeLimiter.Limit>> KEY_PERK_ATTRIBUTE_LIMITS = registryKey("perk_attribute_limits");

    public static final ResourceKey<Registry<TomePage.TomePageType<?>>> KEY_TOME_PAGE_TYPES = registryKey("tome_page_types");
    public static final ResourceKey<Registry<SyncData.Type<?, ?, ?, ?>>> KEY_SYNC_DATA_TYPES = registryKey("sync_data_types");
    public static final ResourceKey<Registry<FocalPointNode.Type<?>>> KEY_FOCAL_NODE_TYPES = registryKey("focal_node_types");
    public static final ResourceKey<Registry<ResearchNodeCondition.Type<?>>> KEY_RESEARCH_NODE_CONDITION_TYPES = registryKey("research_node_condition_types");
    public static final ResourceKey<Registry<AltarRecipeOutputModifier.Type<?>>> KEY_ALTAR_OUTPUT_MODIFIER_TYPES = registryKey("altar_output_modifier_types");
    public static final ResourceKey<Registry<LiquidStarlightRecipeOutputModifier.Type<?>>> KEY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES = registryKey("liquid_starlight_output_modifier_types");
    public static final ResourceKey<Registry<LiquidInteractionResult.Type<?>>> KEY_LIQUID_INTERACTION_RESULT_TYPES = registryKey("liquid_interaction_result_types");
    public static final ResourceKey<Registry<PerkRequirement.Type<?>>> KEY_PERK_REQUIREMENT_TYPES = registryKey("perk_requirement_types");
    public static final ResourceKey<Registry<ArtifactType>> KEY_ARTIFACT_TYPES = registryKey("artifact_types");
    public static final ResourceKey<Registry<ArtifactCondition.Type<?>>> KEY_ARTIFACT_CONDITION_TYPES = registryKey("artifact_condition_types");
    public static final ResourceKey<Registry<ArtifactEffect.Type<?>>> KEY_ARTIFACT_EFFECT_TYPES = registryKey("artifact_effect_types");
    public static final ResourceKey<Registry<LumenBindingUsage.Type<?>>> KEY_LUMEN_BINDING_USAGE_TYPES = registryKey("lumen_binding_usage_types");
    public static final ResourceKey<Registry<LumenBindingEffect.Type<?>>> KEY_LUMEN_BINDING_EFFECT_TYPES = registryKey("lumen_binding_effect_types");

    public static final Registry<BaseConstellation> REGISTRY_CONSTELLATIONS = new RegistryBuilder<>(KEY_CONSTELLATIONS).sync(true).create();
    public static final Registry<Lumen> REGISTRY_LUMEN = new RegistryBuilder<>(KEY_LUMEN).sync(true).create();
    public static final Registry<CrystalProperty> REGISTRY_CRYSTAL_PROPERTIES = new RegistryBuilder<>(KEY_CRYSTAL_PROPERTIES).sync(true).create();
    public static final Registry<TransmissionNodeProvider<?>> REGISTRY_TRANSMISSION_NODES = new RegistryBuilder<>(KEY_TRANSMISSION_NODES).create();
    public static final Registry<AltarEffect> REGISTRY_ALTAR_EFFECTS = new RegistryBuilder<>(KEY_ALTAR_EFFECTS).sync(true).create();
    public static final Registry<PerkType<?>> REGISTRY_PERK_TYPES = new RegistryBuilder<>(KEY_PERK_TYPES).sync(true).create();
    public static final Registry<PerkDataType<?>> REGISTRY_PERK_DATA_TYPES = new RegistryBuilder<>(KEY_PERK_DATA_TYPES).sync(true).create();
    public static final Registry<PerkAttributeTypeReader.Type> REGISTRY_PERK_ATTRIBUTE_TYPE_READERS = new RegistryBuilder<>(KEY_PERK_ATTRIBUTE_TYPE_READERS).sync(true).create();
    public static final Registry<PerkAttributeType> REGISTRY_PERK_ATTRIBUTE_TYPES = new RegistryBuilder<>(KEY_PERK_ATTRIBUTE_TYPES).sync(true).create();
    public static final Registry<PerkAttributeConverter> REGISTRY_PERK_CONVERTERS = new RegistryBuilder<>(KEY_PERK_CONVERTERS).sync(true).create();
    public static final Registry<PerkAttributeModifier> REGISTRY_PERK_CUSTOM_MODIFIERS = new RegistryBuilder<>(KEY_PERK_CUSTOM_MODIFIERS).sync(true).create();
    public static final Registry<ModifierSourceProvider<?>> REGISTRY_PERK_MODIFIER_SOURCES = new RegistryBuilder<>(KEY_PERK_MODIFIER_SOURCES).sync(true).create();
    public static final Registry<PerkAttributeLimiter.Limit> REGISTRY_PERK_ATTRIBUTE_LIMITS = new RegistryBuilder<>(KEY_PERK_ATTRIBUTE_LIMITS).sync(true).create();

    public static final Registry<TomePage.TomePageType<?>> REGISTRY_TOME_PAGE_TYPES = new RegistryBuilder<>(KEY_TOME_PAGE_TYPES).sync(true).create();
    public static final Registry<SyncData.Type<?, ?, ?, ?>> REGISTRY_SYNC_DATA_TYPES = new RegistryBuilder<>(KEY_SYNC_DATA_TYPES).sync(true).create();
    public static final Registry<FocalPointNode.Type<?>> REGISTRY_FOCAL_NODE_TYPES = new RegistryBuilder<>(KEY_FOCAL_NODE_TYPES).sync(true).create();
    public static final Registry<ResearchNodeCondition.Type<?>> REGISTRY_RESEARCH_NODE_CONDITION_TYPES = new RegistryBuilder<>(KEY_RESEARCH_NODE_CONDITION_TYPES).sync(true).create();
    public static final Registry<AltarRecipeOutputModifier.Type<?>> REGISTRY_ALTAR_OUTPUT_MODIFIER_TYPES = new RegistryBuilder<>(KEY_ALTAR_OUTPUT_MODIFIER_TYPES).sync(true).create();
    public static final Registry<LiquidStarlightRecipeOutputModifier.Type<?>> REGISTRY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES = new RegistryBuilder<>(KEY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES).sync(true).create();
    public static final Registry<LiquidInteractionResult.Type<?>> REGISTRY_LIQUID_INTERACTION_RESULT_TYPES = new RegistryBuilder<>(KEY_LIQUID_INTERACTION_RESULT_TYPES).sync(true).create();
    public static final Registry<PerkRequirement.Type<?>> REGISTRY_PERK_REQUIREMENT_TYPES = new RegistryBuilder<>(KEY_PERK_REQUIREMENT_TYPES).sync(true).create();
    public static final Registry<ArtifactType> REGISTRY_ARTIFACT_TYPES = new RegistryBuilder<>(KEY_ARTIFACT_TYPES).sync(true).create();
    public static final Registry<ArtifactCondition.Type<?>> REGISTRY_ARTIFACT_CONDITION_TYPES = new RegistryBuilder<>(KEY_ARTIFACT_CONDITION_TYPES).sync(true).create();
    public static final Registry<ArtifactEffect.Type<?>> REGISTRY_ARTIFACT_EFFECT_TYPES = new RegistryBuilder<>(KEY_ARTIFACT_EFFECT_TYPES).sync(true).create();
    public static final Registry<LumenBindingUsage.Type<?>> REGISTRY_LUMEN_BINDING_USAGE_TYPES = new RegistryBuilder<>(KEY_LUMEN_BINDING_USAGE_TYPES).sync(true).create();
    public static final Registry<LumenBindingEffect.Type<?>> REGISTRY_LUMEN_BINDING_EFFECT_TYPES = new RegistryBuilder<>(KEY_LUMEN_BINDING_EFFECT_TYPES).sync(true).create();

    private static <T> ResourceKey<Registry<T>> registryKey(String name) {
        return ResourceKey.createRegistryKey(AstralSorcery.key(name));
    }
}
