/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.data.config.entry.PerkConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelManager
 * Created by HellFirePvP
 * Date: 02.06.2019 / 01:59
 */
public class PerkLevelManager {

    private static final PerkLevelManager INSTANCE = new PerkLevelManager();

    private Map<Integer, Long> totalExpLevelRequired = new HashMap<>();
    private int lastKnownLevelCap = -1;
    private int levelCap = 40;

    private PerkLevelManager() {}

    public static PerkLevelManager getInstance() {
        return INSTANCE;
    }

    private void ensureLevels() {
        this.levelCap = PerkConfig.CONFIG.perkLevelCap.get();
        
        if (totalExpLevelRequired.isEmpty() || lastKnownLevelCap != this.levelCap) {
            this.totalExpLevelRequired.clear();
            this.lastKnownLevelCap = this.levelCap;
            
            for (int i = 1; i <= this.levelCap; i++) {
                long prev = this.totalExpLevelRequired.getOrDefault(i - 1, 0L);
                this.totalExpLevelRequired.put(i, prev + 150L + 100L * MathHelper.floor(Math.pow(1.2F, i)));
            }
        }
    }

    public int getLevel(double totalExp, PlayerEntity player) {
        return getLevel(MathHelper.lfloor(totalExp), player);
    }

    private int getLevel(long totalExp, PlayerEntity player) {
        ensureLevels();

        if (totalExp <= 0) {
            return 1;
        }

        int levelCap = getLevelCapFor(player);

        for (int i = 1; i <= levelCap; i++) {
            if (totalExp < this.totalExpLevelRequired.getOrDefault(i, Long.MAX_VALUE)) {
                return i;
            }
        }
        return levelCap;
    }

    public long getExpForLevel(int level, PlayerEntity player) {
        ensureLevels();

        if (level <= 1) {
            return 0;
        }
        int levelCap = getLevelCapFor(player);

        if (level > levelCap) {
            level = levelCap;
        }
        return totalExpLevelRequired.get(level);
    }

    public float getNextLevelPercent(double totalExp, PlayerEntity player) {
        ensureLevels();

        int level = getLevel(totalExp, player);
        if (level >= this.levelCap) {
            return 1F; //Done.
        }
        long nextLevel = this.totalExpLevelRequired.getOrDefault(level, 0L);
        long prevLevel = this.totalExpLevelRequired.getOrDefault(level - 1, 0L);
        return ((float) (totalExp - prevLevel)) / ((float) (nextLevel - prevLevel));
    }

    public static int getLevelCapFor(PlayerEntity player) {
        //if (Mods.GAMESTAGES.isPresent() && Mods.CRAFTTWEAKER.isPresent()) {
        //    return resolveLevelCap(player);
        //}
        return getInstance().levelCap;
    }

    //@Optional.Method(modid = "gamestages")
    //private static int resolveLevelCap(PlayerEntity player) {
    //    if (player == null) {
    //        return this.levelCap;
    //    }
    //    int highestFound = -1;

    //    IStageData data = GameStageHelper.getPlayerData(player);
    //    if (data == null) {
    //        return this.levelCap;
    //    }
    //    for (String stage : data.getStages()) {
    //        int cap = GameStageTweaks.getMaxCap(stage);
    //        if (cap > highestFound) {
    //            highestFound = cap;
    //        }
    //    }
    //    return highestFound > -1 ? highestFound : this.levelCap;
    //}

}
