/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
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
    public List<ResearchProgression> getResearchProgression() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getSeenConstellations() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getKnownConstellations() {
        return Collections.emptyList();
    }

    @Override
    public double getPerkExp() {
        return 0;
    }

    @Override
    public int getPerkLevel(EntityPlayer player) {
        return 0;
    }

    @Override
    public float getPercentToNextLevel(EntityPlayer player) {
        return 0F;
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
    public List<AbstractPerk> getAppliedPerks() {
        return Collections.emptyList();
    }

    @Override
    public List<AbstractPerk> getSealedPerks() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public NBTTagCompound getPerkData(AbstractPerk perk) {
        return null;
    }

    @Override
    public List<SextantFinder.TargetObject> getUsedTargets() {
        return Collections.emptyList();
    }

    @Override
    public void useTarget(SextantFinder.TargetObject target) {}

    @Override
    public boolean hasConstellationDiscovered(String constellation) {
        return false;
    }

    @Override
    public boolean grantFreeAllocationPoint(String freePointToken) {
        return false;
    }

    @Override
    public List<String> getFreePointTokens() {
        return Collections.emptyList();
    }

    @Override
    public int getAvailablePerkPoints(EntityPlayer player) {
        return 0;
    }

    @Override
    public boolean hasFreeAllocationPoint(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean hasPerkUnlocked(AbstractPerk perk) {
        return false;
    }

    @Override
    public boolean isPerkSealed(AbstractPerk perk) {
        return false;
    }

    @Override
    public boolean wasOnceAttuned() {
        return false;
    }

}
