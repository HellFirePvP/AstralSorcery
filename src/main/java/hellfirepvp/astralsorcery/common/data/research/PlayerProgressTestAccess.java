/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerProgressTestAccess
 * Created by HellFirePvP
 * Date: 31.10.2017 / 23:40
 */
public class PlayerProgressTestAccess extends PlayerProgress {

    @Override
    public List<ResearchProgression> getResearchProgression() {
        return Lists.newArrayList();
    }

    @Override
    public List<String> getSeenConstellations() {
        return Lists.newArrayList();
    }

    @Override
    public List<String> getKnownConstellations() {
        return Lists.newArrayList();
    }

    @Override
    public double getAlignmentCharge() {
        return 0;
    }

    @Override
    public IMajorConstellation getAttunedConstellation() {
        return null;
    }

    @Override
    public int getAlignmentLevel() {
        return 0;
    }

    @Override
    public int getNextFreeLevel() {
        return 0;
    }

    @Override
    public ProgressionTier getTierReached() {
        return ProgressionTier.DISCOVERY;
    }

    @Override
    public Map<ConstellationPerk, Integer> getAppliedPerks() {
        return Collections.emptyMap();
    }

    @Override
    public boolean hasConstellationDiscovered(String constellation) {
        return false;
    }

    @Override
    public boolean hasFreeAlignmentLevel() {
        return false;
    }

    @Override
    public boolean hasPerkUnlocked(ConstellationPerks perk) {
        return false;
    }

    @Override
    public boolean hasPerkUnlocked(ConstellationPerk perk) {
        return false;
    }

    @Override
    public boolean wasOnceAttuned() {
        return false;
    }

    @Override
    public boolean isPerkActive(ConstellationPerk perk) {
        return false;
    }

}
