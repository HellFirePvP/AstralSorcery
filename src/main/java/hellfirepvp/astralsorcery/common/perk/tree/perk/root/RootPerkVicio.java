/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.root;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tick.TickablePerk;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.RootPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.DiminishingMultiplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RootPerkVicio
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RootPerkVicio extends RootPerk<AbstractPerk.Data> implements TickablePerk {

    public static final MapCodec<RootPerkVicio> CODEC = RecordCodecBuilder.mapCodec(inst -> perkRootFields(inst).apply(inst, RootPerkVicio::new));
    public static final PerkType<RootPerkVicio> TYPE =
            PerkType.of(RootPerkVicio.CODEC, PerkDataTypesAS.DEFAULT_DATA, RootPerkVicio::new);
    public static final Config CONFIG = new Config("root.vicio");

    private static final List<TrackedStat> trackedStats = new ArrayList<>();

    private final Map<ResourceLocation, Map<UUID, Integer>> moveTrackMap = new HashMap<>();

    private RootPerkVicio(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.ROOT, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), ConstellationsAS.VICIO.get());
    }

    protected RootPerkVicio(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers, BaseConstellation constellation) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers, constellation);
    }

    @Override
    protected Config getConfig() {
        return CONFIG;
    }

    @Override
    protected DiminishingMultiplier createMultiplier() {
        return DiminishingMultiplier.of()
                .multiplierReGainTime(200)
                .multiplierReGainRate(0.065F)
                .multiplierLossRate(0.003F)
                .minMultiplier(0.15F)
                .build();
    }

    @Override
    protected void removePerkLogic(Player player, LogicalSide dist) {
        super.removePerkLogic(player, dist);

        if (dist.isServer()) {
            this.moveTrackMap.values().forEach(map -> map.remove(player.getUUID()));
        }
    }

    @Override
    public void clearCaches(LogicalSide side) {
        super.clearCaches(side);

        if (side.isServer()) {
            this.moveTrackMap.clear();
        }
    }

    @Override
    public void tick(Player player, LogicalSide side) {
        if (!side.isServer()) return;
        if (!(player instanceof ServerPlayer sPlayer)) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        PerkAttributeMap perkMap = PerkManager.getOrCreateAttributes(sPlayer);
        StatsCounter stats = sPlayer.getStats();

        float moved = 0F;
        for (TrackedStat tracked : trackedStats) {
            int value = stats.getValue(Stats.CUSTOM.get(tracked.stat));
            int lastValue = this.moveTrackMap.computeIfAbsent(tracked.stat, s -> new HashMap<>()).computeIfAbsent(player.getUUID(), u -> value);
            if (value > lastValue) {
                moved += (value - lastValue) * tracked.gainMultiplier;
                this.moveTrackMap.get(tracked.stat).put(player.getUUID(), value);
            }
        }

        if (moved <= 0) return;

        float xp = moved * 0.01F;
        xp *= this.getExpMultiplier();
        xp *= this.getDiminishingMultiplier(sPlayer);
        xp *= perkMap.getModifier(sPlayer, progress, PerksAS.AttributeTypes.PERK_EFFECT);
        xp *= perkMap.getModifier(sPlayer, progress, PerksAS.AttributeTypes.PERK_EXPERIENCE);

        xp = AttributeEvent.postProcessModded(sPlayer, PerksAS.AttributeTypes.PERK_EXPERIENCE, xp);

        ResearchHelper.addPerkExp(sPlayer, xp);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    static {
        trackStat(Stats.WALK_ONE_CM, 0.8F);
        trackStat(Stats.CROUCH_ONE_CM, 1.4F);
        trackStat(Stats.SPRINT_ONE_CM, 0.5F);
        trackStat(Stats.WALK_ON_WATER_ONE_CM, 0.9F);
        trackStat(Stats.FALL_ONE_CM, 0.1F);
        trackStat(Stats.CLIMB_ONE_CM, 1F);
        trackStat(Stats.FLY_ONE_CM, 0.2F);
        trackStat(Stats.WALK_UNDER_WATER_ONE_CM, 1.1F);
        trackStat(Stats.MINECART_ONE_CM, 0.02F);
        trackStat(Stats.BOAT_ONE_CM, 0.1F);
        trackStat(Stats.PIG_ONE_CM, 0.7F);
        trackStat(Stats.HORSE_ONE_CM, 0.4F);
        trackStat(Stats.AVIATE_ONE_CM, 0.4F);
        trackStat(Stats.SWIM_ONE_CM, 0.7F);
        trackStat(Stats.STRIDER_ONE_CM, 1.1F);
    }

    public static void trackStat(ResourceLocation stat, float gainMultiplier) {
        trackedStats.add(new TrackedStat(stat, gainMultiplier));
    }

    public record TrackedStat(ResourceLocation stat, float gainMultiplier) {}
}
