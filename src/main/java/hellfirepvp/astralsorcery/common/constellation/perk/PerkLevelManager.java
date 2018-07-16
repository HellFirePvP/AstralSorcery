/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelManager
 * Created by HellFirePvP
 * Date: 12.12.2016 / 00:33
 */
public class PerkLevelManager {

    public static final PerkLevelManager INSTANCE = new PerkLevelManager();

    private final int LEVEL_CAP = 20;
    private Map<Integer, Integer> totalExpLevelRequired = new HashMap<>();

    private PerkLevelManager() {
        setupLevels();
    }

    private void setupLevels() {
        for (int i = 1; i <= LEVEL_CAP; i++) {
            int prev = totalExpLevelRequired.getOrDefault(i - 1, 0);
            totalExpLevelRequired.put(i, prev + 50 + (MathHelper.floor(Math.pow(2, (i / 3) + 4))));
        }
    }

    public int getLevel(int totalExp) {
        if (totalExp <= 0) {
            return 1;
        }
        for (int i = 1; i <= LEVEL_CAP; i++) {
            if (totalExp < totalExpLevelRequired.getOrDefault(i, Integer.MAX_VALUE)) {
                return i;
            }
        }
        return LEVEL_CAP;
    }

    public int getExpForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        if (level > LEVEL_CAP) {
            level = LEVEL_CAP;
        }
        return totalExpLevelRequired.get(level);
    }

    public float getNextLevelPercent(double totalExp) {
        int level = getLevel(MathHelper.floor(totalExp));
        if (level >= LEVEL_CAP) {
            return 1F; //Done.
        }
        int nextLevel = this.totalExpLevelRequired.getOrDefault(level, 0);
        int prevLevel = this.totalExpLevelRequired.getOrDefault(level - 1, 0);
        return ((float) (totalExp - prevLevel)) / ((float) (nextLevel - prevLevel));
    }

    public int getLevelCap() {
        return LEVEL_CAP;
    }
}
