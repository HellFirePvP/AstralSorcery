/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerProgressTestAccess
 * Created by HellFirePvP
 * Date: 31.10.2017 / 23:40
 */
public class PlayerProgressTestAccess extends PlayerProgress {

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public Collection<ResearchProgression> getResearchProgression() {
        return Collections.emptyList();
    }

    @Override
    public List<ResourceLocation> getSeenConstellations() {
        return Collections.emptyList();
    }

    @Override
    public List<ResourceLocation> getKnownConstellations() {
        return Collections.emptyList();
    }

    @Override
    public List<ResourceLocation> getStoredConstellationPapers() {
        return Collections.emptyList();
    }

    @Override
    public PlayerPerkData getPerkData() {
        return new PlayerPerkData();
    }

    @Override
    public IMajorConstellation getAttunedConstellation() {
        return null;
    }

    @Override
    public ProgressionTier getTierReached() {
        return ProgressionTier.DISCOVERY;
    }

    @Override
    public boolean hasSeenConstellation(ResourceLocation constellation) {
        return false;
    }

    @Override
    public boolean hasConstellationDiscovered(ResourceLocation constellation) {
        return false;
    }

    @Override
    public boolean wasOnceAttuned() {
        return false;
    }

    @Override
    public boolean didReceiveTome() {
        return true; //Fake players always did get the tome already.
    }

    @Override
    public boolean doPerkAbilities() {
        return false;
    }
}
