/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.binding.effect.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingEffectTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingEffectTypesAS {

    public static final DeferredRegister<LumenBindingEffect.Type<?>> LUMEN_BINDING_USAGE_TYPES_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_LUMEN_BINDING_EFFECT_TYPES, AstralSorcery.MODID);

    public static final LumenBindingEffect.DeferredType<LumenBindingEffect> NONE =
            register("none", MapCodec.unit(LumenBindingEffect.NONE), StreamCodec.unit(LumenBindingEffect.NONE));
    public static final LumenBindingEffect.DeferredType<CombinedLumenBindingEffect> COMBINED =
            register("combined", CombinedLumenBindingEffect.CODEC, CombinedLumenBindingEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingDynamicModifierEffect> DYNAMIC_MODIFIER =
            register("dynamic_modifier", LumenBindingDynamicModifierEffect.CODEC, LumenBindingDynamicModifierEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingEffectEffectiveness> EFFECTIVENESS =
            register("effectiveness", LumenBindingEffectEffectiveness.CODEC, LumenBindingEffectEffectiveness.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingHitAddEffectEffect> HIT_ADD_EFFECT =
            register("hit_add_effect", LumenBindingHitAddEffectEffect.CODEC, LumenBindingHitAddEffectEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingPlaceLightEffect> PLACE_LIGHT =
            register("place_light", LumenBindingPlaceLightEffect.CODEC, LumenBindingPlaceLightEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingProjectileAccuracyEffect> PROJECTILE_ACCURACY =
            register("projectile_accuracy", LumenBindingProjectileAccuracyEffect.CODEC, LumenBindingProjectileAccuracyEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingCollectDropsEffect> COLLECT_DROPS =
            register("collect_drops", LumenBindingCollectDropsEffect.CODEC, LumenBindingCollectDropsEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingAbsorbDamageEffect> ABSORB_DAMAGE =
            register("absorb_damage", LumenBindingAbsorbDamageEffect.CODEC, LumenBindingAbsorbDamageEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingDamageBurstEffect> ENERGY_BURST =
            register("damage_burst", LumenBindingDamageBurstEffect.CODEC, LumenBindingDamageBurstEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingExtendMobEffectsEffect> EXTEND_MOB_EFFECTS =
            register("extend_mob_effects", LumenBindingExtendMobEffectsEffect.CODEC, LumenBindingExtendMobEffectsEffect.STREAM_CODEC);
    public static final LumenBindingEffect.DeferredType<LumenBindingAoeCropGrowthEffect> CHRONO_RIPENING =
            register("aoe_crop_growth", LumenBindingAoeCropGrowthEffect.CODEC, LumenBindingAoeCropGrowthEffect.STREAM_CODEC);

    private static <T extends LumenBindingEffect> LumenBindingEffect.DeferredType<T> register(String name,
                                                                                              MapCodec<T> codec,
                                                                                              StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new LumenBindingEffect.DeferredType<>(LUMEN_BINDING_USAGE_TYPES_REGISTER.register(name,
                () -> new LumenBindingEffect.Type<>(codec, streamCodec)));
    }
}
