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
import hellfirepvp.astralsorcery.common.util.FlagExecutor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingDamageBurstEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingDamageBurstEffect extends LumenBindingEffect {

    public static final MapCodec<LumenBindingDamageBurstEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("radius_blocks").forGetter(LumenBindingDamageBurstEffect::getRadiusBlocks),
            Codec.FLOAT.fieldOf("damage_multiplier").forGetter(LumenBindingDamageBurstEffect::getDamageMultiplier)
    ).apply(inst, LumenBindingDamageBurstEffect::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingDamageBurstEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            LumenBindingDamageBurstEffect::getRadiusBlocks,
            ByteBufCodecs.FLOAT,
            LumenBindingDamageBurstEffect::getDamageMultiplier,
            LumenBindingDamageBurstEffect::new);

    private final float radiusBlocks;
    private final float damageMultiplier;

    private LumenBindingDamageBurstEffect(float radiusBlocks, float damageMultiplier) {
        this.radiusBlocks = radiusBlocks;
        this.damageMultiplier = damageMultiplier;
    }

    public static LumenBindingDamageBurstEffect of(float radiusBlocks, float damageMultiplier) {
        return new LumenBindingDamageBurstEffect(radiusBlocks, damageMultiplier);
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingDamageBurstEffect::onDamagePost);
    }

    private static void onDamagePost(LivingDamageEvent.Post event) {
        if (FlagExecutor.isFlagSet(FlagExecutor.Flag.AOE_DAMAGE_BURST)) return;

        if (!(event.getSource().getDirectEntity() instanceof ServerPlayer attackerSPlayer)) return;
        LivingEntity victim = event.getEntity();
        if (victim.getHealth() > 0) return;

        forEachEffect(attackerSPlayer, LumenBindingDamageBurstEffect.class, (stack, effect) -> {
            float burstDamage = victim.getMaxHealth() * effect.getDamageMultiplier();
            if (burstDamage <= 0) return;

            AABB searchBox = victim.getBoundingBox().inflate(effect.getRadiusBlocks());
            FlagExecutor.run(FlagExecutor.Flag.AOE_DAMAGE_BURST, () ->
                victim.level().<LivingEntity>getEntitiesOfClass(LivingEntity.class, searchBox)
                        .stream()
                        .filter(e -> e != attackerSPlayer && e != victim)
                        .forEach(e -> e.hurt(event.getSource(), burstDamage))
            );
        });
    }

    public float getRadiusBlocks() {
        return this.radiusBlocks;
    }

    public float getDamageMultiplier() {
        return this.damageMultiplier;
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return List.of(Component.translatable("lumen.binding.astralsorcery.damage_burst", Math.round(this.damageMultiplier * 100)));
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.ENERGY_BURST;
    }
}
