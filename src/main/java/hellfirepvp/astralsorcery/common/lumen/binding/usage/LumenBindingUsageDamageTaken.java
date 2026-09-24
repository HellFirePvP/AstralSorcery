/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.usage;

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
public class LumenBindingUsageDamageTaken extends LumenBindingUsage {

    public static final MapCodec<LumenBindingUsageDamageTaken> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecFields(inst).apply(inst, LumenBindingUsageDamageTaken::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingUsageDamageTaken> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            LumenBindingUsageDamageTaken::getLumenCost,
            ByteBufCodecs.FLOAT,
            LumenBindingUsageDamageTaken::getConsumptionChance,
            LumenBindingUsageDamageTaken::new);

    protected LumenBindingUsageDamageTaken(int lumenCost, float consumptionChance) {
        super(lumenCost, consumptionChance);
    }

    public static LumenBindingUsageDamageTaken of(int lumenCost, float consumptionChance) {
        return new LumenBindingUsageDamageTaken(lumenCost, consumptionChance);
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingUsageDamageTaken::onDamageTaken);
    }

    private static void onDamageTaken(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity attacked) {
            if (attacked.level().isClientSide()) return;

            drainAll(attacked, 1F, LumenBindingUsageDamageTaken.class);
        }
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingUsageTypesAS.DAMAGE_TAKEN;
    }
}
