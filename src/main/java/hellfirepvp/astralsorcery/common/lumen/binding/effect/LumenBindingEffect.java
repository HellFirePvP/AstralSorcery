/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBinding;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.stream.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class LumenBindingEffect {

    public static final Codec<LumenBindingEffect> CODEC = RegistriesAS.REGISTRY_LUMEN_BINDING_EFFECT_TYPES.byNameCodec()
            .dispatch(LumenBindingEffect::unwrapType, LumenBindingEffect.Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingEffect> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN_BINDING_EFFECT_TYPES)
            .dispatch(LumenBindingEffect::unwrapType, LumenBindingEffect.Type::streamCodec);

    public static final LumenBindingEffect NONE = new LumenBindingEffect() {
        @Override
        public DeferredType<?> getType() {
            return LumenBindingEffectTypesAS.NONE;
        }
    };

    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return List.of();
    }

    protected float getEffectMultiplier(ItemStack stack, LogicalSide side) {
        StoredLumenComponent storedLumenCmp = stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
        return 1F + storedLumenCmp.getBinding(side, LumenAS.PRISMATIC)
                .flatMap(type -> type.getBinding(stack))
                .map(LumenBinding::getEffect)
                .filter(binding -> binding instanceof LumenBindingEffectEffectiveness)
                .map(binding -> (LumenBindingEffectEffectiveness) binding)
                .map(LumenBindingEffectEffectiveness::getEffectMultiplier)
                .orElse(0F);
    }

    public static <T extends LumenBindingEffect> void forEachEffect(LivingEntity entity, Class<T> usageType, BiConsumer<ItemStack, T> consumer) {
        forEachEffect(entity, usageType, (stack, effect) -> {
            consumer.accept(stack, effect);
            return List.of();
        });
    }

    public static <T extends LumenBindingEffect, R> List<R> forEachEffect(LivingEntity entity, Class<T> usageType, BiFunction<ItemStack, T, R> fn) {
        return Streams.of(entity.getAllSlots())
                .filter(stack -> !stack.isEmpty())
                .map(stack -> forEachEffectTypes(entity, stack, (lumen, binding) -> {
                    List<LumenBindingEffect> effects = new ArrayList<>();
                    effects.add(binding.getEffect());
                    if (binding.getEffect() instanceof CombinedLumenBindingEffect combinedEffects) {
                        effects.addAll(combinedEffects.getEffects());
                    }
                    List<R> results = new ArrayList<>();
                    for (LumenBindingEffect usage : effects) {
                        if (usageType.isInstance(usage)) {
                            R res = fn.apply(stack, usageType.cast(usage));
                            if (res != null) {
                                results.add(res);
                            }
                        }
                    }
                    return results;
                })).reduce(new ArrayList<>(), (result, append) -> {
                    result.addAll(append);
                    return result;
                });
    }

    private static <R> List<R> forEachEffectTypes(LivingEntity entity, ItemStack stack, BiFunction<Lumen, LumenBinding, List<R>> consumeFn) {
        StoredLumenComponent storedLumenCmp = stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
        if (storedLumenCmp.isEmpty()) return List.of();
        LogicalSide side = SidedHelper.getSide(entity);

        List<R> results = new ArrayList<>();
        for (Lumen bound : storedLumenCmp.getActiveBindings()) {
            storedLumenCmp.getBinding(side, bound)
                    .flatMap(bindingType -> bindingType.getActiveBinding(stack, entity))
                    .map(binding -> consumeFn.apply(bound, binding))
                    .ifPresent(results::addAll);
        }
        return results;
    }

    public abstract DeferredType<?> getType();

    public Type<?> unwrapType() {
        return this.getType().holder().get();
    }

    public record Type<T extends LumenBindingEffect>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {}

    public record DeferredType<T extends LumenBindingEffect>(
            DeferredHolder<LumenBindingEffect.Type<?>, LumenBindingEffect.Type<T>> holder) {}
}
