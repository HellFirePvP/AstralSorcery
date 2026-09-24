/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lumen.Lumen;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerProgressTestAccess
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PlayerProgressTestAccess extends PlayerProgress {

    private static final PlayerProgressTestAccess INSTANCE = new PlayerProgressTestAccess();

    public static PlayerProgressTestAccess get() {
        return INSTANCE;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    protected void ensureElementaryLumen() {}

    @Override
    public void mergeKnowledgeShare(PlayerProgress otherProgress) {}

    @Override
    public Set<BaseConstellation> getSeenConstellations() {
        return Collections.emptySet();
    }

    @Override
    public Set<BaseConstellation> getKnownConstellations() {
        return Collections.emptySet();
    }

    @Override
    public Set<BaseConstellation> getSeenFocalPoints() {
        return Collections.emptySet();
    }

    @Override
    public Set<Lumen> getDiscoveredLumen() {
        return Collections.emptySet();
    }

    @Override
    public boolean isFlagSet(ResearchFlag flag) {
        return false;
    }

    @Override
    public Set<ResearchFlag> getKnownFlags() {
        return Collections.emptySet();
    }

    @Override
    public ResearchTier getTierReached() {
        return ResearchTier.first();
    }

    @Override
    public PlayerPerkData getPerkData() {
        return PlayerPerkData.blankData();
    }

    @Override
    public Optional<BaseConstellation> getAttunedConstellation() {
        return Optional.empty();
    }

    @Override
    public boolean hasLearnedToNavigateTome() {
        return false;
    }

    @Override
    public boolean wasOnceAttuned() {
        return false;
    }

    @Override
    public boolean hasReceivedTome() {
        return true;
    }
}
