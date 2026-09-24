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
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerProgressFullAccess
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PlayerProgressFullAccess extends PlayerProgress {

    private static final PlayerProgressFullAccess INSTANCE = new PlayerProgressFullAccess();

    public static PlayerProgressFullAccess get() {
        return INSTANCE;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected void ensureElementaryLumen() {}

    @Override
    public void mergeKnowledgeShare(PlayerProgress otherProgress) {}

    @Override
    public Set<BaseConstellation> getSeenConstellations() {
        return RegistriesAS.REGISTRY_CONSTELLATIONS.stream().collect(Collectors.toSet());
    }

    @Override
    public Set<BaseConstellation> getKnownConstellations() {
        return RegistriesAS.REGISTRY_CONSTELLATIONS.stream().collect(Collectors.toSet());
    }

    @Override
    public Set<BaseConstellation> getSeenFocalPoints() {
        return RegistriesAS.REGISTRY_CONSTELLATIONS.stream()
                .filter(cst -> cst.is(TagsAS.Constellations.MAY_BE_FOCAL_POINT))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Lumen> getDiscoveredLumen() {
        return RegistriesAS.REGISTRY_LUMEN.stream().collect(Collectors.toSet());
    }

    @Override
    public boolean isFlagSet(ResearchFlag flag) {
        return true;
    }

    @Override
    public Set<ResearchFlag> getKnownFlags() {
        return Set.of(ResearchFlag.values());
    }

    @Override
    public ResearchTier getTierReached() {
        return ResearchTier.last();
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
        return true;
    }

    @Override
    public boolean wasOnceAttuned() {
        return true;
    }

    @Override
    public boolean hasReceivedTome() {
        return true;
    }
}
