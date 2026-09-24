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
import hellfirepvp.astralsorcery.common.recipe.liquid.output.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightRecipeOutputTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightRecipeOutputTypesAS {

    public static final DeferredRegister<LiquidStarlightRecipeOutputModifier.Type<?>> OUTPUT_MODIFIER_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_LIQUID_STARLIGHT_OUTPUT_MODIFIER_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<LiquidStarlightRecipeOutputModifier.Type<?>, LiquidStarlightRecipeOutputModifier.Type<LiquidStarlightOutputDropItem>> DROP_ITEM =
            OUTPUT_MODIFIER_REGISTER.register("drop_item", () -> LiquidStarlightOutputDropItem.TYPE);
    public static final DeferredHolder<LiquidStarlightRecipeOutputModifier.Type<?>, LiquidStarlightRecipeOutputModifier.Type<LiquidStarlightOutputMergeCrystal>> MERGE_CRYSTAL =
            OUTPUT_MODIFIER_REGISTER.register("merge_crystal", () -> LiquidStarlightOutputMergeCrystal.TYPE);
    public static final DeferredHolder<LiquidStarlightRecipeOutputModifier.Type<?>, LiquidStarlightRecipeOutputModifier.Type<LiquidStarlightOutputFormCrystalCluster>> FORM_CRYSTAL_CLUSTER =
            OUTPUT_MODIFIER_REGISTER.register("form_crystal_cluster", () -> LiquidStarlightOutputFormCrystalCluster.TYPE);
    public static final DeferredHolder<LiquidStarlightRecipeOutputModifier.Type<?>, LiquidStarlightRecipeOutputModifier.Type<LiquidStarlightOutputFormGemCrystalCluster>> FORM_GEM_CRYSTAL_CLUSTER =
            OUTPUT_MODIFIER_REGISTER.register("form_gem_crystal_cluster", () -> LiquidStarlightOutputFormGemCrystalCluster.TYPE);
    public static final DeferredHolder<LiquidStarlightRecipeOutputModifier.Type<?>, LiquidStarlightRecipeOutputModifier.Type<LiquidStarlightOutputGrowSize>> GROW_SIZE =
            OUTPUT_MODIFIER_REGISTER.register("grow_size", () -> LiquidStarlightOutputGrowSize.TYPE);
    public static final DeferredHolder<LiquidStarlightRecipeOutputModifier.Type<?>, LiquidStarlightRecipeOutputModifier.Type<LiquidStarlightOutputBindLumen>> BIND_LUMEN =
            OUTPUT_MODIFIER_REGISTER.register("bind_lumen", () -> LiquidStarlightOutputBindLumen.TYPE);
    public static final DeferredHolder<LiquidStarlightRecipeOutputModifier.Type<?>, LiquidStarlightRecipeOutputModifier.Type<LiquidStarlightOutputFillLumen>> FILL_LUMEN =
            OUTPUT_MODIFIER_REGISTER.register("fill_lumen", () -> LiquidStarlightOutputFillLumen.TYPE);

}
