/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.property.AttunePlayerProperty;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerProgress
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PlayerProgress {

    public static final Codec<PlayerProgress> SAVE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SetCodec.of(RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec()).fieldOf("seen").forGetter(PlayerProgress::getSeenConstellations),
            SetCodec.of(RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec()).fieldOf("known").forGetter(PlayerProgress::getKnownConstellations),
            SetCodec.of(RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec()).fieldOf("seen_focal_points").forGetter(PlayerProgress::getSeenFocalPoints),
            SetCodec.of(RegistriesAS.REGISTRY_LUMEN.byNameCodec()).fieldOf("discovered_lumen").forGetter(PlayerProgress::getDiscoveredLumen),
            CodecUtil.defaulted(SetCodec.of(CodecUtil.enumCodec(ResearchFlag.class)), "known_flags", HashSet::new, PlayerProgress::getKnownFlags),
            ResearchTier.CODEC.fieldOf("tier").forGetter(PlayerProgress::getTierReached),
            PlayerPerkData.PERKTREE_HASH_CODEC.fieldOf("perk_data").forGetter(PlayerProgress::getPerkData),
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("attuned").forGetter(PlayerProgress::getAttunedConstellation),
            Codec.BOOL.fieldOf("learned_to_navigate_tome").forGetter(PlayerProgress::hasLearnedToNavigateTome),
            Codec.BOOL.fieldOf("was_attuned").forGetter(PlayerProgress::wasOnceAttuned),
            Codec.BOOL.fieldOf("received_tome").forGetter(PlayerProgress::hasReceivedTome)
    ).apply(inst, PlayerProgress::new));

    public static final Codec<PlayerProgress> SHARE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SetCodec.of(RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec()).fieldOf("seen").forGetter(PlayerProgress::getSeenConstellations),
            SetCodec.of(RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec()).fieldOf("known").forGetter(PlayerProgress::getKnownConstellations),
            SetCodec.of(RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec()).fieldOf("seen_focal_points").forGetter(PlayerProgress::getSeenFocalPoints),
            SetCodec.of(RegistriesAS.REGISTRY_LUMEN.byNameCodec()).fieldOf("discovered_lumen").forGetter(PlayerProgress::getDiscoveredLumen),
            CodecUtil.defaulted(SetCodec.of(CodecUtil.enumCodec(ResearchFlag.class)), "known_flags", HashSet::new, PlayerProgress::getKnownFlags),
            ResearchTier.CODEC.fieldOf("tier").forGetter(PlayerProgress::getTierReached),
            Codec.BOOL.fieldOf("was_attuned").forGetter(PlayerProgress::wasOnceAttuned)
    ).apply(inst, PlayerProgress::new));

    private final Set<BaseConstellation> seenConstellations = new HashSet<>();
    private final Set<BaseConstellation> knownConstellations = new HashSet<>();
    private final Set<BaseConstellation> seenFocalPoints = new HashSet<>();
    private final Set<Lumen> discoveredLumen = new HashSet<>();
    private final Set<ResearchFlag> knownFlags = new HashSet<>();
    private ResearchTier tierReached;
    private final PlayerPerkData perkData;

    @Nullable
    private BaseConstellation attunedConstellation;
    private boolean learnedToNavigateTome;
    private boolean wasOnceAttuned;
    private boolean tomeReceived;

    PlayerProgress(Set<BaseConstellation> seenConstellations,
                   Set<BaseConstellation> knownConstellations,
                   Set<BaseConstellation> seenFocalPoints,
                   Set<Lumen> discoveredLumen,
                   Set<ResearchFlag> knownFlags,
                   ResearchTier tierReached,
                   PlayerPerkData perkData,
                   Optional<BaseConstellation> attunedConstellation,
                   boolean learnedToNavigateTome,
                   boolean wasOnceAttuned,
                   boolean tomeReceived) {
        this.seenConstellations.addAll(seenConstellations);
        this.knownConstellations.addAll(knownConstellations);
        this.seenFocalPoints.addAll(seenFocalPoints);
        this.discoveredLumen.addAll(discoveredLumen);
        this.knownFlags.addAll(knownFlags);
        this.tierReached = tierReached;
        this.perkData = perkData;
        this.attunedConstellation = attunedConstellation.orElse(null);
        this.learnedToNavigateTome = learnedToNavigateTome;
        this.wasOnceAttuned = wasOnceAttuned;
        this.tomeReceived = tomeReceived;
        this.ensureElementaryLumen();
    }

    protected void ensureElementaryLumen() {
        RegistriesAS.REGISTRY_LUMEN.forEach(lumen -> {
            if (lumen.isElementary()) {
                this.discoverLumen(lumen);
            }
        });
    }

    private PlayerProgress(Set<BaseConstellation> seenConstellations,
                           Set<BaseConstellation> knownConstellations,
                           Set<BaseConstellation> seenFocalPoints,
                           Set<Lumen> discoveredLumen,
                           Set<ResearchFlag> knownFlags,
                           ResearchTier tierReached,
                           boolean wasOnceAttuned) {
        this(seenConstellations, knownConstellations, seenFocalPoints, discoveredLumen, knownFlags, tierReached, PlayerPerkData.blankData(),
                Optional.empty(), false, wasOnceAttuned, false);
    }

    protected PlayerProgress() {
        this(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), ResearchTier.first(), PlayerPerkData.blankData(),
                Optional.empty(), false, false, false);
    }

    public static PlayerProgress blankProgress() {
        return new PlayerProgress();
    }

    public void mergeKnowledgeShare(PlayerProgress otherProgress) {
        otherProgress.getSeenConstellations().forEach(this::memorizeConstellation);
        otherProgress.getKnownConstellations().forEach(this::discoverConstellation);
        otherProgress.getSeenFocalPoints().forEach(this::memorizeFocalPoint);
        otherProgress.getDiscoveredLumen().forEach(this::discoverLumen);
        otherProgress.getKnownFlags().stream().filter(ResearchFlag::isShareable).forEach(this::setKnownFlag);
        if (otherProgress.getTierReached().isThisLater(this.tierReached)) {
            this.tierReached = otherProgress.getTierReached();
        }
        if (otherProgress.wasOnceAttuned()) {
            this.wasOnceAttuned = true;
        }
    }

    public boolean isValid() {
        return true;
    }

    public Set<BaseConstellation> getSeenConstellations() {
        return Collections.unmodifiableSet(this.seenConstellations);
    }

    public Set<BaseConstellation> getKnownConstellations() {
        return Collections.unmodifiableSet(this.knownConstellations);
    }

    public Set<BaseConstellation> getSeenFocalPoints() {
        return Collections.unmodifiableSet(this.seenFocalPoints);
    }

    public Set<Lumen> getDiscoveredLumen() {
        return Collections.unmodifiableSet(this.discoveredLumen);
    }

    public boolean isFlagSet(ResearchFlag flag) {
        return this.knownFlags.contains(flag);
    }

    public Set<ResearchFlag> getKnownFlags() {
        return Collections.unmodifiableSet(this.knownFlags);
    }

    public ResearchTier getTierReached() {
        return this.tierReached;
    }

    public PlayerPerkData getPerkData() {
        return this.perkData;
    }

    public Optional<BaseConstellation> getAttunedConstellation() {
        return Optional.ofNullable(this.attunedConstellation);
    }

    public Collection<AbstractPerk<?>> getDependentPerks(AbstractPerk<?> perk, LogicalSide side) {
        return AttunePlayerProperty.getRootPerk(this.getAttunedConstellation(), side)
                .map(rootPerk -> this.perkData.getDependentPerks(this, perk, rootPerk, side))
                .orElse(Collections.emptyList());
    }

    public boolean hasLearnedToNavigateTome() {
        return this.learnedToNavigateTome;
    }

    public boolean wasOnceAttuned() {
        return this.wasOnceAttuned;
    }

    public boolean hasReceivedTome() {
        return this.tomeReceived;
    }

    public boolean isAttuned() {
        return this.getAttunedConstellation().isPresent();
    }

    protected void setProgression(ResearchTier tier) {
        this.tierReached = tier;
    }

    protected void setLearnedToNavigateTome(boolean learnedToNavigateTome) {
        this.learnedToNavigateTome = learnedToNavigateTome;
    }

    protected void setWasOnceAttuned(boolean wasOnceAttuned) {
        this.wasOnceAttuned = wasOnceAttuned;
    }

    protected void setAttunedConstellation(@Nullable BaseConstellation cst) {
        this.attunedConstellation = cst;
        this.wasOnceAttuned |= cst != null;
    }

    protected void setTomeReceived(boolean tomeReceived) {
        this.tomeReceived = tomeReceived;
    }

    public boolean hasSeenConstellation(BaseConstellation cst) {
        return this.getSeenConstellations().contains(cst);
    }

    public boolean hasDiscoveredConstellation(BaseConstellation cst) {
        return this.getKnownConstellations().contains(cst);
    }

    public boolean hasSeenFocalPoint(BaseConstellation cst) {
        return this.getSeenFocalPoints().contains(cst);
    }

    public boolean hasDiscoveredLumen(Lumen lumen) {
        return this.getDiscoveredLumen().contains(lumen);
    }

    protected boolean memorizeConstellation(BaseConstellation cst) {
        return this.seenConstellations.add(cst);
    }

    protected boolean discoverConstellation(BaseConstellation cst) {
        this.memorizeConstellation(cst);
        return this.knownConstellations.add(cst);
    }

    protected boolean memorizeFocalPoint(BaseConstellation cst) {
        return this.seenFocalPoints.add(cst);
    }

    protected boolean discoverLumen(Lumen lumen) {
        return this.discoveredLumen.add(lumen);
    }

    protected boolean setKnownFlag(ResearchFlag flag) {
        return this.knownFlags.add(flag);
    }
}
