/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.altar.output.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarRecipeOutputTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarRecipeOutputTypesAS {

    public static final DeferredRegister<AltarRecipeOutputModifier.Type<?>> OUTPUT_MODIFIER_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_ALTAR_OUTPUT_MODIFIER_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputUpdateResearchTier>> UPDATE_RESEARCH_TIER =
            OUTPUT_MODIFIER_REGISTER.register("update_research_tier", () -> AltarOutputUpdateResearchTier.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputSetBlock>> SET_BLOCK =
            OUTPUT_MODIFIER_REGISTER.register("set_block", () -> AltarOutputSetBlock.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputSetDataComponent<?>>> SET_DATA_COMPONENT =
            OUTPUT_MODIFIER_REGISTER.register("set_data_component", () -> AltarOutputSetDataComponent.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputReplaceWithInput>> REPLACE_WITH_INPUT =
            OUTPUT_MODIFIER_REGISTER.register("replace_with_input", () -> AltarOutputReplaceWithInput.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputIncreaseEnchantments>> INCREASE_ENCHANTMENTS =
            OUTPUT_MODIFIER_REGISTER.register("increase_enchantments", () -> AltarOutputIncreaseEnchantments.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputAddGemModifier>> ADD_GEM_MODIFIER =
            OUTPUT_MODIFIER_REGISTER.register("add_gem_modifier", () -> AltarOutputAddGemModifier.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputAddEnchantmentModifier>> ADD_ENCHANTMENT_MODIFIER =
            OUTPUT_MODIFIER_REGISTER.register("add_enchantment_modifier", () -> AltarOutputAddEnchantmentModifier.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputGenerateIdentifier>> GENERATE_IDENTIFIER =
            OUTPUT_MODIFIER_REGISTER.register("generate_identifier", () -> AltarOutputGenerateIdentifier.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputMergeCrystalProperties>> MERGE_CRYSTAL_PROPERTIES =
            OUTPUT_MODIFIER_REGISTER.register("merge_crystal_properties", () -> AltarOutputMergeCrystalProperties.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputSetFlag>> SET_FLAG =
            OUTPUT_MODIFIER_REGISTER.register("set_flag", () -> AltarOutputSetFlag.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputGenerateArtifactShardLoot>> GENERATE_ARTIFACT_SHARD_LOOT =
            OUTPUT_MODIFIER_REGISTER.register("generate_artifact_shard_loot", () -> AltarOutputGenerateArtifactShardLoot.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputCopyDataComponents>> COPY_DATA_COMPONENTS =
            OUTPUT_MODIFIER_REGISTER.register("copy_data_components", () -> AltarOutputCopyDataComponents.TYPE);
    public static final DeferredHolder<AltarRecipeOutputModifier.Type<?>, AltarRecipeOutputModifier.Type<AltarOutputSetCrystalCount>> SET_CRYSTAL_COUNT =
            OUTPUT_MODIFIER_REGISTER.register("set_crystal_count", () -> AltarOutputSetCrystalCount.TYPE);

}
