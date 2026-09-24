/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.config.server.PerkConfig;
import hellfirepvp.astralsorcery.common.util.data.SidedReference;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkLevelManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkLevelManager {

    private static final PerkLevelManager INSTANCE = new PerkLevelManager();

    private final SidedReference<LevelData> levelData = new SidedReference<>();

    private PerkLevelManager() {}

    public static PerkLevelManager getInstance() {
        return INSTANCE;
    }

    public void clearCache(LogicalSide side) {
        levelData.setData(side, null);
    }

    public void initializeClientLevels(int maxLevel) {
        this.levelData.setData(LogicalSide.CLIENT, new LevelData(maxLevel));
    }

    public void initializeServerLevels() {
        int maxLevel = PerkConfig.CONFIG.perkLevelCap.getAsInt();
        this.levelData.setData(LogicalSide.SERVER, new LevelData(maxLevel));
    }

    public int getLevel(double totalExp, @Nullable Player player, LogicalSide side) {
        return getLevel(Mth.lfloor(totalExp), player, side);
    }

    private int getLevel(long totalExp, @Nullable Player player, LogicalSide side) {
        if (totalExp <= 0) {
            return 1;
        }
        int levelCap = getMaxLevel(side, player);

        return this.levelData.getData(side).map(data -> {
            for (int i = 1; i <= levelCap; i++) {
                if (totalExp < data.totalExpLevelRequired.getOrDefault(i, Long.MAX_VALUE)) {
                    return i;
                }
            }
            return levelCap;
        }).orElse(1);
    }

    public long getExpForLevel(int targetLevel, @Nullable Player player, LogicalSide side) {
        if (targetLevel <= 1) {
            return 0;
        }
        int levelCap = getMaxLevel(side, player);

        return this.levelData.getData(side).map(data -> {
            int level = targetLevel;
            if (level > levelCap) {
                level = levelCap;
            }
            return data.totalExpLevelRequired.get(level);
        }).orElse(0L);
    }

    public float getNextLevelPercent(double totalExp, @Nullable Player player, LogicalSide side) {
        int level = getLevel(totalExp, player, side);
        if (level >= getMaxLevel(side, player)) {
            return 1F; //Done.
        }
        return this.levelData.getData(side).map(data -> {
            long nextLevel = data.totalExpLevelRequired.getOrDefault(level, 0L);
            long prevLevel = data.totalExpLevelRequired.getOrDefault(level - 1, 0L);
            return ((float) (totalExp - prevLevel)) / ((float) (nextLevel - prevLevel));
        }).orElse(1F);
    }

    public int getMaxLevel(LogicalSide side, @Nullable Player player) {
        return this.levelData.getData(side).map(data -> data.maxLevel).orElse(1);
    }

    private static class LevelData {

        private final Map<Integer, Long> totalExpLevelRequired = new HashMap<>();
        private final int maxLevel;

        public LevelData(int maxLevel) {
            this.maxLevel = maxLevel;
            this.buildLevelRequirements();
        }

        private void buildLevelRequirements() {
            if (this.totalExpLevelRequired.isEmpty()) {
                for (int i = 1; i <= this.maxLevel; i++) {
                    long prev = this.totalExpLevelRequired.getOrDefault(i - 1, 0L);
                    this.totalExpLevelRequired.put(i, prev + 150L + 100L * Mth.floor(Math.pow(1.2F, i)));
                }
            }
        }
    }
}
