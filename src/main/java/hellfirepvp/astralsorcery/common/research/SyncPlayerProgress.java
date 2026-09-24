/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SyncPlayerProgress
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SyncPlayerProgress {

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerProgress> SYNC_CODEC = StreamCodec.of(SyncPlayerProgress::write, SyncPlayerProgress::read);

    private final Set<BaseConstellation> seenConstellations = new HashSet<>();
    private final Set<BaseConstellation> knownConstellations = new HashSet<>();
    private final Set<BaseConstellation> seenFocalPoints = new HashSet<>();
    private final Set<Lumen> discoveredLumen = new HashSet<>();
    private final Set<ResearchFlag> knownFlags = new HashSet<>();
    private final ResearchTier tierReached;
    private final SyncPlayerPerkData perkData;
    @Nullable
    private final BaseConstellation attunedConstellation;
    private final boolean hasLearnedToNavigateTome;
    private final boolean wasOnceAttuned;
    private final boolean tomeReceived;

    private SyncPlayerProgress(Set<BaseConstellation> seenConstellations,
                               Set<BaseConstellation> knownConstellations,
                               Set<BaseConstellation> seenFocalPoints,
                               Set<Lumen> discoveredLumen,
                               Set<ResearchFlag> knownFlags,
                               ResearchTier tierReached,
                               SyncPlayerPerkData perkData,
                               Optional<BaseConstellation> attunedConstellation,
                               boolean hasLearnedToNavigateTome,
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
        this.hasLearnedToNavigateTome = hasLearnedToNavigateTome;
        this.wasOnceAttuned = wasOnceAttuned;
        this.tomeReceived = tomeReceived;
    }

    public static SyncPlayerProgress sync(PlayerProgress progress) {
        return new SyncPlayerProgress(
                Set.copyOf(progress.getSeenConstellations()),
                Set.copyOf(progress.getKnownConstellations()),
                Set.copyOf(progress.getSeenFocalPoints()),
                Set.copyOf(progress.getDiscoveredLumen()),
                Set.copyOf(progress.getKnownFlags()),
                progress.getTierReached(),
                SyncPlayerPerkData.sync(progress.getPerkData()),
                progress.getAttunedConstellation(),
                progress.hasLearnedToNavigateTome(),
                progress.wasOnceAttuned(),
                progress.hasReceivedTome()
        );
    }

    private Set<BaseConstellation> getSeenConstellations() {
        return this.seenConstellations;
    }

    private Set<BaseConstellation> getKnownConstellations() {
        return this.knownConstellations;
    }

    private Set<BaseConstellation> getSeenFocalPoints() {
        return this.seenFocalPoints;
    }

    private Set<Lumen> getDiscoveredLumen() {
        return this.discoveredLumen;
    }

    private Set<ResearchFlag> getKnownFlags() {
        return this.knownFlags;
    }

    private ResearchTier getTierReached() {
        return this.tierReached;
    }

    private SyncPlayerPerkData getPerkData() {
        return this.perkData;
    }

    private Optional<BaseConstellation> getAttunedConstellation() {
        return Optional.ofNullable(this.attunedConstellation);
    }

    private boolean hasLearnedToNavigateTome() {
        return this.hasLearnedToNavigateTome;
    }

    private boolean wasOnceAttuned() {
        return this.wasOnceAttuned;
    }

    private boolean hasReceivedTome() {
        return this.tomeReceived;
    }

    public PlayerProgress load() {
        return new PlayerProgress(
                this.getSeenConstellations(),
                this.getKnownConstellations(),
                this.getSeenFocalPoints(),
                this.getDiscoveredLumen(),
                this.getKnownFlags(),
                this.getTierReached(),
                this.getPerkData().load(),
                this.getAttunedConstellation(),
                this.hasLearnedToNavigateTome(),
                this.wasOnceAttuned(),
                this.hasReceivedTome()
        );
    }

    private static void write(RegistryFriendlyByteBuf buf, SyncPlayerProgress prog) {
        var cstCodec = ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS);
        var lumenCodec = ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN);

        cstCodec.apply(SetCodec.streamOp()).encode(buf, prog.getSeenConstellations());
        cstCodec.apply(SetCodec.streamOp()).encode(buf, prog.getKnownConstellations());
        cstCodec.apply(SetCodec.streamOp()).encode(buf, prog.getSeenFocalPoints());
        lumenCodec.apply(SetCodec.streamOp()).encode(buf, prog.getDiscoveredLumen());
        CodecUtil.enumStreamCodec(ResearchFlag.class).apply(SetCodec.streamOp()).encode(buf, prog.getKnownFlags());
        ResearchTier.STREAM_CODEC.encode(buf, prog.getTierReached());
        SyncPlayerPerkData.STREAM_CODEC.encode(buf, prog.getPerkData());

        ByteBufCodecs.optional(cstCodec).encode(buf, prog.getAttunedConstellation());
        ByteBufCodecs.BOOL.encode(buf, prog.hasLearnedToNavigateTome());
        ByteBufCodecs.BOOL.encode(buf, prog.wasOnceAttuned());
        ByteBufCodecs.BOOL.encode(buf, prog.hasReceivedTome());
    }

    private static SyncPlayerProgress read(RegistryFriendlyByteBuf buf) {
        var cstCodec = ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS);
        var lumenCodec = ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN);

        Set<BaseConstellation> seen = cstCodec.apply(SetCodec.streamOp()).decode(buf);
        Set<BaseConstellation> known = cstCodec.apply(SetCodec.streamOp()).decode(buf);
        Set<BaseConstellation> seenFocalPoints = cstCodec.apply(SetCodec.streamOp()).decode(buf);
        Set<Lumen> discoveredLumen = lumenCodec.apply(SetCodec.streamOp()).decode(buf);
        Set<ResearchFlag> flags = CodecUtil.enumStreamCodec(ResearchFlag.class).apply(SetCodec.streamOp()).decode(buf);
        ResearchTier tier = ResearchTier.STREAM_CODEC.decode(buf);
        SyncPlayerPerkData perkData = SyncPlayerPerkData.STREAM_CODEC.decode(buf);

        Optional<BaseConstellation> attuned = ByteBufCodecs.optional(cstCodec).decode(buf);
        boolean learnedToNavigateTome = ByteBufCodecs.BOOL.decode(buf);
        boolean wasAttuned = ByteBufCodecs.BOOL.decode(buf);
        boolean receivedTome = ByteBufCodecs.BOOL.decode(buf);
        return new SyncPlayerProgress(seen, known, seenFocalPoints, discoveredLumen, flags, tier, perkData, attuned, learnedToNavigateTome, wasAttuned, receivedTome);
    }
}
