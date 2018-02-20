/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerkLevelManager
 * Created by HellFirePvP
 * Date: 12.12.2016 / 00:33
 */
public class ConstellationPerkLevelManager {

    public static int[] levelsRequired = new int[8];
    public static int[] levelsRequiredClientCache = new int[8];

    /*public static int getExpNeededForNext(EntityPlayer player, Side side) {
        return getExpNeededForNext(ResearchManager.getProgress(player, side));
    }

    public static float getPercentProgressToNextLevel(EntityPlayer player, Side side) {
        return getPercentProgressToNextLevel(ResearchManager.getProgress(player, side));
    }*/

    //0 to 8 for now.
    public static int getAlignmentLevel(PlayerProgress progress) {
        double charge = progress.getAlignmentCharge();
        if(charge >= levelsRequired[levelsRequired.length - 1]) {
            return levelsRequired.length;
        }
        int i;
        for (i = 0; i < levelsRequired.length; i++) {
            if(charge < levelsRequired[i]) break;
        }
        return i;
    }

    public static float getPercToNextLevel(PlayerProgress progress) {
        double current = progress.getAlignmentCharge();
        int level = progress.getAlignmentLevel();
        if(level >= levelsRequired.length) return 1F;
        if(level < 0) return 0F;

        double left = current - (level - 1 < 0 ? 0 : levelsRequired[level - 1]);
        double reqNext;
        if(level >= levelsRequired.length) {
            return 1F;
        } else {
            reqNext = levelsRequired[level] - (level - 1 < 0 ? 0 : levelsRequired[level - 1]);
        }
        return (float) (left / reqNext);
    }

    /*public static int getExpNeededForNext(PlayerProgress progress) {
        if(progress == null) return -1;
        int perks = progress.getAppliedPerks().size();
        //if(perks > 8 || perks + 1 > 8) return -1;
        int expReqCurrent = levelsRequired[perks];
        int expReqPrev = (perks == 0) ? 0 : levelsRequired[perks - 1];

        return expReqCurrent - expReqPrev - progress.getAlignmentCharge();
    }

    public static float getPercentProgressToNextLevel(PlayerProgress progress) {
        if(progress == null) return 0F;
        int reqNext = getExpNeededForNext(progress);
        if(reqNext <= 0) return 0F;
        int perks = progress.getAppliedPerks().size();
        int have = levelsRequired[perks - 1];
        return Math.min(1F, ((float) levelsRequired[perks] - have) / ((float) levelsRequired[perks] - levelsRequired[perks - 1]));
    }

    public static boolean canUnlockNextPerk(PlayerProgress progress) {
        if(progress == null) return false;
        int reqNext = getExpNeededForNext(progress);
        if(reqNext <= 0) return false;
        int perks = progress.getAppliedPerks().size();
        return progress.getAlignmentCharge() >= levelsRequired[perks];
    }*/

    //Super ugly code. UGH FFS.
    public static ConfigEntry getLevelConfigurations() {
        return new ConfigEntry(ConfigEntry.Section.PERK_LEVELS, "levels") {
            @Override
            public void loadFromConfig(Configuration cfg) {
                int[] levelBuf = new int[8];
                levelBuf[0] = cfg.getInt(getKey() + "_Level_1", getConfigurationSection(), 15, 1, Integer.MAX_VALUE,  "Defines the amount of Alignment needed for level: 1");
                levelBuf[1] = cfg.getInt(getKey() + "_Level_2", getConfigurationSection(), 45, 2, Integer.MAX_VALUE,  "Defines the amount of Alignment needed for level: 2");
                levelBuf[2] = cfg.getInt(getKey() + "_Level_3", getConfigurationSection(), 85, 3, Integer.MAX_VALUE,  "Defines the amount of Alignment needed for level: 3");
                levelBuf[3] = cfg.getInt(getKey() + "_Level_4", getConfigurationSection(), 130, 4, Integer.MAX_VALUE, "Defines the amount of Alignment needed for level: 4");
                levelBuf[4] = cfg.getInt(getKey() + "_Level_5", getConfigurationSection(), 180, 5, Integer.MAX_VALUE, "Defines the amount of Alignment needed for level: 5");
                levelBuf[5] = cfg.getInt(getKey() + "_Level_6", getConfigurationSection(), 245, 6, Integer.MAX_VALUE, "(Unused YET) Defines the amount of Alignment needed for level: 6");
                levelBuf[6] = cfg.getInt(getKey() + "_Level_7", getConfigurationSection(), 325, 7, Integer.MAX_VALUE, "(Unused YET) Defines the amount of Alignment needed for level: 7");
                levelBuf[7] = cfg.getInt(getKey() + "_Level_8", getConfigurationSection(), 400, 8, Integer.MAX_VALUE, "(Unused YET) Defines the amount of Alignment needed for level: 8");
                for (int i = 0; i < 7; i++) {
                    int prev = levelBuf[i];
                    int next = levelBuf[i + 1];
                    if(prev >= next) {
                        throw new IllegalStateException("Illegal AstralSorcery config state: Alignment required for level " + (i + 2) + " is lower than alignment needed for level " + (i + 1));
                    }
                }
                levelsRequired = levelBuf;
                levelsRequiredClientCache = new int[levelsRequired.length];
                System.arraycopy(levelsRequired, 0, levelsRequiredClientCache, 0, levelsRequired.length);
            }
        };
    }

}
