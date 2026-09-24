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
import hellfirepvp.astralsorcery.common.util.MobEffectUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingExtendMobEffectsEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingExtendMobEffectsEffect extends LumenBindingEffect {

    public static final MapCodec<LumenBindingExtendMobEffectsEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("extension_ticks").forGetter(LumenBindingExtendMobEffectsEffect::getExtensionTicks),
            Codec.INT.fieldOf("cooldown_ticks").forGetter(LumenBindingExtendMobEffectsEffect::getCooldownTicks)
    ).apply(inst, LumenBindingExtendMobEffectsEffect::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingExtendMobEffectsEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            LumenBindingExtendMobEffectsEffect::getExtensionTicks,
            ByteBufCodecs.INT,
            LumenBindingExtendMobEffectsEffect::getCooldownTicks,
            LumenBindingExtendMobEffectsEffect::new);

    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    private final int extensionTicks;
    private final int cooldownTicks;

    private LumenBindingExtendMobEffectsEffect(int extensionTicks, int cooldownTicks) {
        this.extensionTicks = extensionTicks;
        this.cooldownTicks = cooldownTicks;
    }

    public static LumenBindingExtendMobEffectsEffect of(int extensionTicks) {
        return of(extensionTicks, extensionTicks);
    }

    public static LumenBindingExtendMobEffectsEffect of(int extensionTicks, int cooldownTicks) {
        return new LumenBindingExtendMobEffectsEffect(extensionTicks, cooldownTicks);
    }

    public static void clearServer() {
        cooldowns.clear();
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingExtendMobEffectsEffect::onDamageTaken);
    }

    private static void onDamageTaken(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) return;

        long gameTime = sPlayer.level().getGameTime();
        UUID uuid = sPlayer.getUUID();
        if (cooldowns.getOrDefault(uuid, 0L) > gameTime) return;

        forEachEffect(sPlayer, LumenBindingExtendMobEffectsEffect.class, (stack, effect) -> effect)
                .stream()
                .max(Comparator.comparing(effect -> effect.getExtensionTicks()))
                .ifPresent(effect -> {
                    cooldowns.put(uuid, gameTime + effect.getCooldownTicks());

                    List<MobEffectInstance> toExtend = sPlayer.getActiveEffects().stream()
                            .filter(inst -> inst.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL)
                            .map(inst -> MobEffectUtil.newDuration(inst, inst.getDuration() + effect.getExtensionTicks()))
                            .toList();
                    toExtend.forEach(sPlayer::addEffect);
                });
    }

    public int getExtensionTicks() {
        return this.extensionTicks;
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return List.of(Component.translatable("lumen.binding.astralsorcery.extend_mob_effects", Mth.floor(this.extensionTicks / 20F)));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.EXTEND_MOB_EFFECTS;
    }
}

