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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.text.DecimalFormat;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingAbsorbDamageEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingAbsorbDamageEffect extends LumenBindingEffect {

    public static final MapCodec<LumenBindingAbsorbDamageEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("threshold").forGetter(LumenBindingAbsorbDamageEffect::getThreshold),
            Codec.INT.fieldOf("duration_ticks").forGetter(LumenBindingAbsorbDamageEffect::getDurationTicks),
            Codec.INT.fieldOf("amplifier").forGetter(LumenBindingAbsorbDamageEffect::getAmplifier),
            Codec.INT.fieldOf("cooldown_ticks").forGetter(LumenBindingAbsorbDamageEffect::getCooldownTicks)
    ).apply(inst, LumenBindingAbsorbDamageEffect::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingAbsorbDamageEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            LumenBindingAbsorbDamageEffect::getThreshold,
            ByteBufCodecs.INT,
            LumenBindingAbsorbDamageEffect::getDurationTicks,
            ByteBufCodecs.INT,
            LumenBindingAbsorbDamageEffect::getAmplifier,
            ByteBufCodecs.INT,
            LumenBindingAbsorbDamageEffect::getCooldownTicks,
            LumenBindingAbsorbDamageEffect::new);

    private static final DecimalFormat HEALTH_FORMAT = new DecimalFormat("#.#");
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    private final float threshold;
    private final int durationTicks;
    private final int amplifier;
    private final int cooldownTicks;

    private LumenBindingAbsorbDamageEffect(float threshold, int durationTicks, int amplifier, int cooldownTicks) {
        this.threshold = threshold;
        this.durationTicks = durationTicks;
        this.amplifier = amplifier;
        this.cooldownTicks = cooldownTicks;
    }

    public static LumenBindingAbsorbDamageEffect of(float threshold, int durationTicks, int amplifier, int cooldownTicks) {
        return new LumenBindingAbsorbDamageEffect(threshold, durationTicks, amplifier, cooldownTicks);
    }

    public static void clearServer() {
        cooldowns.clear();
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingAbsorbDamageEffect::onDamageTaken);
    }

    private static void onDamageTaken(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) return;

        long gameTime = sPlayer.level().getGameTime();
        UUID uuid = sPlayer.getUUID();
        if (cooldowns.getOrDefault(uuid, 0L) > gameTime) return;

        forEachEffect(sPlayer, LumenBindingAbsorbDamageEffect.class, (stack, effect) -> {
            if (event.getNewDamage() >= effect.getThreshold() && effect.getDurationTicks() > 0) {
                return effect;
            }
            return null;
        }).stream().max(Comparator.comparing(LumenBindingAbsorbDamageEffect::getDurationTicks))
                .ifPresent(effect -> {
                    cooldowns.put(uuid, gameTime + effect.getCooldownTicks());
                    sPlayer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, effect.getDurationTicks(), effect.getAmplifier(), true, false, false));
                });
    }

    public float getThreshold() {
        return this.threshold;
    }

    public int getDurationTicks() {
        return this.durationTicks;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return List.of(Component.translatable("lumen.binding.astralsorcery.absorb_damage",
                HEALTH_FORMAT.format(this.threshold),
                this.getAmplifier() + 1));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.ABSORB_DAMAGE;
    }
}
