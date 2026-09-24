/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.usage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingUsageTypesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingUsageBlockBreak
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingUsageDamageDealt extends LumenBindingUsage {

    public static final MapCodec<LumenBindingUsageDamageDealt> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecFields(inst).and(
                    Codec.BOOL.fieldOf("direct").forGetter(LumenBindingUsageDamageDealt::isDirect)
            ).apply(inst, LumenBindingUsageDamageDealt::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingUsageDamageDealt> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            LumenBindingUsageDamageDealt::getLumenCost,
            ByteBufCodecs.FLOAT,
            LumenBindingUsageDamageDealt::getConsumptionChance,
            ByteBufCodecs.BOOL,
            LumenBindingUsageDamageDealt::isDirect,
            LumenBindingUsageDamageDealt::new);

    private final boolean direct;

    protected LumenBindingUsageDamageDealt(int lumenCost, float consumptionChance, boolean direct) {
        super(lumenCost, consumptionChance);
        this.direct = direct;
    }

    public static LumenBindingUsageDamageDealt of(int lumenCost, float consumptionChance, boolean direct) {
        return new LumenBindingUsageDamageDealt(lumenCost, consumptionChance, direct);
    }

    public boolean isDirect() {
        return this.direct;
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingUsageDamageDealt::onDamageDealtDirectly);
    }

    private static void onDamageDealtDirectly(LivingDamageEvent.Post event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            if (attacker.level().isClientSide()) return;

            forEachUsage(attacker, LumenBindingUsageDamageDealt.class, (stack, usage) -> {
                if (usage.isDirect() != event.getSource().isDirect()) return;

                drainUsedLumen(attacker, stack, usage, 1F);
            });
        }
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingUsageTypesAS.DAMAGE_DEALT;
    }
}
