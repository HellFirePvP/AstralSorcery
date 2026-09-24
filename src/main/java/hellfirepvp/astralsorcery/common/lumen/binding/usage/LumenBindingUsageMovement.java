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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingUsageBlockBreak
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingUsageMovement extends LumenBindingUsage {

    public static final MapCodec<LumenBindingUsageMovement> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecFields(inst).and(inst.group(
                    Codec.FLOAT.fieldOf("stat_multiplier").forGetter(LumenBindingUsageMovement::getStatMultiplier),
                    ResourceLocation.CODEC.listOf().fieldOf("stat_id").forGetter(LumenBindingUsageMovement::getStatIds)
            )).apply(inst, LumenBindingUsageMovement::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingUsageMovement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            LumenBindingUsageMovement::getLumenCost,
            ByteBufCodecs.FLOAT,
            LumenBindingUsageMovement::getConsumptionChance,
            ByteBufCodecs.FLOAT,
            LumenBindingUsageMovement::getStatMultiplier,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
            LumenBindingUsageMovement::getStatIds,
            LumenBindingUsageMovement::new);

    private final Map<ResourceLocation, Integer> lastTrackedMap = new HashMap<>();

    private final List<ResourceLocation> statIds = new ArrayList<>();
    private final float statMultiplier;

    protected LumenBindingUsageMovement(int lumenCost, float consumptionChance, float statMultiplier, List<ResourceLocation> statIds) {
        super(lumenCost, consumptionChance);
        this.statMultiplier = statMultiplier;
        this.resolveStatIds(statIds);
    }

    private void resolveStatIds(List<ResourceLocation> statIds) {
        for (ResourceLocation statId : statIds) {
            Stats.CUSTOM.iterator().forEachRemaining(stat -> {
                if (stat.getValue().equals(statId)) {
                    this.statIds.add(stat.getValue());
                }
            });
        }
    }

    public static LumenBindingUsageMovement of(int lumenCost, float consumptionChance, float statMultiplier, ResourceLocation... statIds) {
        return new LumenBindingUsageMovement(lumenCost, consumptionChance, statMultiplier, Arrays.asList(statIds));
    }

    public List<ResourceLocation> getStatIds() {
        return Collections.unmodifiableList(this.statIds);
    }

    public float getStatMultiplier() {
        return this.statMultiplier;
    }

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LumenBindingUsageMovement::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        if (sPlayer.level().isClientSide()) return;

        forEachUsage(sPlayer, LumenBindingUsageMovement.class, (stack, usage) -> {
            StatsCounter stats = sPlayer.getStats();
            usage.statIds.forEach(statId -> {
                int value = stats.getValue(Stats.CUSTOM.get(statId));
                if (!usage.lastTrackedMap.containsKey(statId)) {
                    usage.lastTrackedMap.put(statId, value);
                } else {
                    int lastTrackedValue = usage.lastTrackedMap.get(statId);
                    if (value > lastTrackedValue) {
                        int diff = value - lastTrackedValue;
                        usage.lastTrackedMap.put(statId, value);

                        float diffConsume = (diff / 100F) * usage.statMultiplier;
                        drainUsedLumen(sPlayer, stack, usage, diffConsume);
                    }
                }
            });
        });
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingUsageTypesAS.MOVEMENT;
    }
}
