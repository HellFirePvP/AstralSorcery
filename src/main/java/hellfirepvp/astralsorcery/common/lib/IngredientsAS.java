/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.ingredient.*;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IngredientsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IngredientsAS {

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPE_REGISTER =
            DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<IngredientBridge>> INGREDIENT_BRIDGE =
            INGREDIENT_TYPE_REGISTER.register("bridge", () -> new IngredientType<>(IngredientBridge.CODEC));

    public static final DeferredHolder<IngredientType<?>, IngredientType<IsEnchantedIngredient>> IS_ENCHANTED =
            INGREDIENT_TYPE_REGISTER.register("is_enchanted", () -> new IngredientType<>(IsEnchantedIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<? extends IsStableArtifactIngredient>> IS_STABLE_ARTIFACT =
            INGREDIENT_TYPE_REGISTER.register("is_stable_artifact", () -> new IngredientType<>(IsStableArtifactIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<? extends IsFlagSetIngredient>> IS_FLAG_SET =
            INGREDIENT_TYPE_REGISTER.register("is_flag_set", () -> new IngredientType<>(IsFlagSetIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<? extends IsLumenBindableIngredient>> LUMEN_BINDABLE =
            INGREDIENT_TYPE_REGISTER.register("lumen_bindable", () -> new IngredientType<>(IsLumenBindableIngredient.CODEC));
    public static final DeferredHolder<IngredientType<?>, IngredientType<? extends HasStoredLumenIngredient>> STORED_LUMEN =
            INGREDIENT_TYPE_REGISTER.register("has_stored_lumen", () -> new IngredientType<>(HasStoredLumenIngredient.CODEC));
}
