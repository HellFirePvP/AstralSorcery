/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RandomMobEffectInstance
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RandomMobEffectInstance {

    // Yea i know this codec situation here is bad. i cba to make a separate storage object to serialize indefinitely nested effects.
    // so this is what you get for now, does its job. PR something better if it bothers you that much :^)
    private static final Codec<RandomMobEffectInstance> SINGLE_CODEC = RecordCodecBuilder.create(inst -> effectFields(inst)
            .apply(inst, (effect, durationRange, amplifierRange) -> new RandomMobEffectInstance(effect, durationRange, amplifierRange, Optional.empty())));
    public static final Codec<RandomMobEffectInstance> CODEC = RecordCodecBuilder.create(inst -> effectFields(inst).and(
            SINGLE_CODEC.optionalFieldOf("hidden_effect").forGetter(RandomMobEffectInstance::getHiddenEffect)
    ).apply(inst, RandomMobEffectInstance::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RandomMobEffectInstance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT),
            RandomMobEffectInstance::getEffect,
            IntRange.STREAM_CODEC,
            RandomMobEffectInstance::getDurationRange,
            IntRange.STREAM_CODEC,
            RandomMobEffectInstance::getAmplifierRange,
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)),
            randomEffect -> randomEffect.getHiddenEffect().map(RandomMobEffectInstance::getEffect),
            ByteBufCodecs.optional(IntRange.STREAM_CODEC),
            randomEffect -> randomEffect.getHiddenEffect().map(RandomMobEffectInstance::getDurationRange),
            ByteBufCodecs.optional(IntRange.STREAM_CODEC),
            randomEffect -> randomEffect.getHiddenEffect().map(RandomMobEffectInstance::getAmplifierRange),
            RandomMobEffectInstance::new);

    private final Holder<MobEffect> effect;
    private final IntRange durationRange, amplifierRange;
    @Nullable
    private final RandomMobEffectInstance hiddenEffect;

    private RandomMobEffectInstance(Holder<MobEffect> effect, IntRange durationRange, IntRange amplifierRange,
                                    Optional<Holder<MobEffect>> hiddenEffect, Optional<IntRange> hiddenDurationRange, Optional<IntRange> hiddenAmplifierRange) {
        this.effect = effect;
        this.durationRange = durationRange;
        this.amplifierRange = amplifierRange;
        this.hiddenEffect = hiddenEffect.map(effectHolder -> new RandomMobEffectInstance(effectHolder,
                hiddenDurationRange.orElse(IntRange.of(0, 0)),
                hiddenAmplifierRange.orElse(IntRange.of(0, 0)),
                Optional.empty()))
                .orElse(null);
    }
    private RandomMobEffectInstance(Holder<MobEffect> effect, IntRange durationRange, IntRange amplifierRange, Optional<RandomMobEffectInstance> hiddenEffect) {
        this.effect = effect;
        this.durationRange = durationRange;
        this.amplifierRange = amplifierRange;
        this.hiddenEffect = hiddenEffect.orElse(null);
    }

    public static RandomMobEffectInstance of(Holder<MobEffect> effect, IntRange durationRange, IntRange amplifierRange, @Nullable RandomMobEffectInstance hiddenEffect) {
        return new RandomMobEffectInstance(effect, durationRange, amplifierRange, Optional.ofNullable(hiddenEffect));
    }

    public static RandomMobEffectInstance of(Holder<MobEffect> effect, IntRange durationRange, IntRange amplifierRange) {
        return of(effect, durationRange, amplifierRange, null);
    }

    private static Products.P3<RecordCodecBuilder.Mu<RandomMobEffectInstance>, Holder<MobEffect>, IntRange, IntRange> effectFields(RecordCodecBuilder.Instance<RandomMobEffectInstance> instance) {
        return instance.group(
                MobEffect.CODEC.fieldOf("effect").forGetter(RandomMobEffectInstance::getEffect),
                IntRange.CODEC.fieldOf("duration_range").forGetter(RandomMobEffectInstance::getDurationRange),
                IntRange.CODEC.fieldOf("amplifier_range").forGetter(RandomMobEffectInstance::getAmplifierRange)
        );
    }

    public Holder<MobEffect> getEffect() {
        return this.effect;
    }

    public IntRange getDurationRange() {
        return this.durationRange;
    }

    public IntRange getAmplifierRange() {
        return this.amplifierRange;
    }

    public Optional<RandomMobEffectInstance> getHiddenEffect() {
        return Optional.ofNullable(this.hiddenEffect);
    }

    public MobEffectInstance createEffect(RandomSource rand) {
        int duration = this.durationRange.getRandom(rand);
        MobEffectInstance hidden = this.getHiddenEffect()
                .map(effect -> effect.createEffect(rand))
                .map(effect -> MobEffectUtil.newDuration(effect, effect.getDuration() + duration))
                .orElse(null);
        return new MobEffectInstance(this.getEffect(),
                duration,
                this.amplifierRange.getRandom(rand),
                true, true, true, hidden);
    }

    public Component getDisplay() {
        MutableComponent name = Component.translatable(this.effect.value().getDescriptionId());
        int minAmp = this.amplifierRange.getMinInclusive();
        int maxAmp = this.amplifierRange.getMaxInclusive();
        if (minAmp == maxAmp) {
            if (minAmp == 0) {
                return name;
            } else {
                Component cmp = Component.translatable("potion.amplifier.display." + minAmp);
                return Component.translatable("potion.withAmplifier", name, cmp);
            }
        } else {
            Component minCmp = Component.translatable("potion.amplifier.display." + minAmp);
            Component maxCmp = Component.translatable("potion.amplifier.display." + maxAmp);
            Component amplifierComp = Component.translatable("potion.amplifier.display.range", minCmp, maxCmp);
            return Component.translatable("potion.withAmplifier", name, amplifierComp);
        }
    }
}
