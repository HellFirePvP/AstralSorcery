/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelManager
 * Created by HellFirePvP
 * Date: 12.12.2016 / 00:33
 */
public class PerkLevelManager extends ConfigEntry {

    public static int LEVEL_CAP = 30;
    public static final PerkLevelManager INSTANCE = new PerkLevelManager();

    private Map<Integer, Long> totalExpLevelRequired = new HashMap<>();

    private PerkLevelManager() {
        super(Section.PERK_LEVELS, "level");
    }

    private void ensureLevels() {
        if (totalExpLevelRequired.isEmpty()) {
            for (int i = 1; i <= LEVEL_CAP; i++) {
                long prev = totalExpLevelRequired.getOrDefault(i - 1, 0L);
                totalExpLevelRequired.put(i, prev + 150L + (MathHelper.lfloor(Math.pow(2, (i / 2) + 3))));
            }
        }
    }

    public int getLevel(double totalExp) {
        return getLevel(MathHelper.lfloor(totalExp));
    }

    public int getLevel(long totalExp) {
        ensureLevels();

        if (totalExp <= 0) {
            return 1;
        }
        for (int i = 1; i <= LEVEL_CAP; i++) {
            if (totalExp < totalExpLevelRequired.getOrDefault(i, Long.MAX_VALUE)) {
                return i;
            }
        }
        return LEVEL_CAP;
    }

    public long getExpForLevel(int level) {
        ensureLevels();

        if (level <= 1) {
            return 0;
        }
        if (level > LEVEL_CAP) {
            level = LEVEL_CAP;
        }
        return totalExpLevelRequired.get(level);
    }

    public float getNextLevelPercent(double totalExp) {
        ensureLevels();

        int level = getLevel(totalExp);
        if (level >= LEVEL_CAP) {
            return 1F; //Done.
        }
        long nextLevel = this.totalExpLevelRequired.getOrDefault(level, 0L);
        long prevLevel = this.totalExpLevelRequired.getOrDefault(level - 1, 0L);
        return ((float) (totalExp - prevLevel)) / ((float) (nextLevel - prevLevel));
    }

    public int getLevelCap() {
        return LEVEL_CAP;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        this.totalExpLevelRequired.clear();

        LEVEL_CAP = cfg.getInt(getKey() + "Cap", getConfigurationSection(), LEVEL_CAP, 1, 100, "Sets the max level for the perk tree levels.");
    }
}
