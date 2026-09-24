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
import hellfirepvp.astralsorcery.common.util.RandomMobEffectInstance;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingHitAddEffectEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingHitAddEffectEffect extends LumenBindingEffect {

    public static final MapCodec<LumenBindingHitAddEffectEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RandomMobEffectInstance.CODEC.fieldOf("effect").forGetter(LumenBindingHitAddEffectEffect::getEffect),
            Codec.FLOAT.fieldOf("chance").forGetter(LumenBindingHitAddEffectEffect::getChance)
    ).apply(inst, LumenBindingHitAddEffectEffect::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingHitAddEffectEffect> STREAM_CODEC = StreamCodec.composite(
            RandomMobEffectInstance.STREAM_CODEC,
            LumenBindingHitAddEffectEffect::getEffect,
            ByteBufCodecs.FLOAT,
            LumenBindingHitAddEffectEffect::getChance,
            LumenBindingHitAddEffectEffect::new);

    private final RandomMobEffectInstance effect;
    private final float chance;

    private LumenBindingHitAddEffectEffect(RandomMobEffectInstance effect, float chance) {
        this.effect = effect;
        this.chance = chance;
    }

    public static LumenBindingHitAddEffectEffect of(RandomMobEffectInstance effect, float chance) {
        return new LumenBindingHitAddEffectEffect(effect, chance);
    }

    public static LumenBindingHitAddEffectEffect of(Holder<MobEffect> effect, IntRange durationRange, IntRange amplifierRange, float chance) {
        return of(RandomMobEffectInstance.of(effect, durationRange, amplifierRange), chance);
    }

    public RandomMobEffectInstance getEffect() {
        return this.effect;
    }

    private float getChance() {
        return this.chance;
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        if (this.getChance() >= 1F) {
            return List.of(Component.translatable("lumen.binding.astralsorcery.on_hit_effect", this.getEffect().getDisplay()));
        }
        return List.of(Component.translatable("lumen.binding.astralsorcery.on_hit_effect.chance", this.getEffect().getDisplay()));
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingHitAddEffectEffect::onDamageDealt);
    }

    private static void onDamageDealt(LivingDamageEvent.Post event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            if (attacker.level().isClientSide()) return;

            RandomSource rand = RandomSource.create();
            forEachEffect(attacker, LumenBindingHitAddEffectEffect.class, (stack, effect) -> {
                event.getEntity().addEffect(effect.getEffect().createEffect(rand));
            });
        }
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.HIT_ADD_EFFECT;
    }
}
