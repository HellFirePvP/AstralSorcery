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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingUsageBlockBreak
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingUsageHealthRecovered extends LumenBindingUsage {

    public static final MapCodec<LumenBindingUsageHealthRecovered> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecFields(inst).apply(inst, LumenBindingUsageHealthRecovered::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingUsageHealthRecovered> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            LumenBindingUsageHealthRecovered::getLumenCost,
            ByteBufCodecs.FLOAT,
            LumenBindingUsageHealthRecovered::getConsumptionChance,
            LumenBindingUsageHealthRecovered::new);

    protected LumenBindingUsageHealthRecovered(int lumenCost, float consumptionChance) {
        super(lumenCost, consumptionChance);
    }

    public static LumenBindingUsageHealthRecovered of(int lumenCost, float consumptionChance) {
        return new LumenBindingUsageHealthRecovered(lumenCost, consumptionChance);
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingUsageHealthRecovered::onHealthRecovered);
    }

    private static void onHealthRecovered(LivingHealEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        drainAll(event.getEntity(), 1F, LumenBindingUsageHealthRecovered.class);
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingUsageTypesAS.HEALTH_RECOVERED;
    }
}
