/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.data.config.entry.PerkConfig;
import hellfirepvp.astralsorcery.common.util.SidedReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
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

    private static final SidedReference<LevelData> LEVEL_DATA = new SidedReference<>();

    private PerkLevelManager() {}

    public static void clearCache(LogicalSide side) {
        LEVEL_DATA.setData(side, null);
    }

    @OnlyIn(Dist.CLIENT)
    public static void receiveLevelCap(int maxLevel) {
        LEVEL_DATA.setData(LogicalSide.CLIENT, new LevelData(maxLevel));
    }

    public static void loadPerkLevels() {
        LEVEL_DATA.setData(LogicalSide.SERVER, new LevelData(PerkConfig.CONFIG.perkLevelCap.get()));
    }

    public static int getLevel(double totalExp, PlayerEntity player, LogicalSide side) {
        return getLevel(MathHelper.lfloor(totalExp), player, side);
    }

    private static int getLevel(long totalExp, PlayerEntity player, LogicalSide side) {
        if (totalExp <= 0) {
            return 1;
        }
        int levelCap = getLevelCap(side, player);

        return LEVEL_DATA.getData(side).map(data -> {
            for (int i = 1; i <= levelCap; i++) {
                if (totalExp < data.totalExpLevelRequired.getOrDefault(i, Long.MAX_VALUE)) {
                    return i;
                }
            }
            return levelCap;
        }).orElse(1);
    }

    public static long getExpForLevel(int targetLevel, PlayerEntity player, LogicalSide side) {
        if (targetLevel <= 1) {
            return 0;
        }
        int levelCap = getLevelCap(side, player);

        return LEVEL_DATA.getData(side).map(data -> {
            int level = targetLevel;
            if (level > levelCap) {
                level = levelCap;
            }
            return data.totalExpLevelRequired.get(level);
        }).orElse(0L);
    }

    public static float getNextLevelPercent(double totalExp, PlayerEntity player, LogicalSide side) {
        int level = getLevel(totalExp, player, side);
        if (level >= getLevelCap(side, player)) {
            return 1F; //Done.
        }
        return LEVEL_DATA.getData(side).map(data -> {
            long nextLevel = data.totalExpLevelRequired.getOrDefault(level, 0L);
            long prevLevel = data.totalExpLevelRequired.getOrDefault(level - 1, 0L);
            return ((float) (totalExp - prevLevel)) / ((float) (nextLevel - prevLevel));
        }).orElse(1F);
    }

    public static int getLevelCap(LogicalSide side, @Nullable PlayerEntity player) {
        return LEVEL_DATA.getData(side).map(data -> data.levelCap).orElse(1);
    }

    private static class LevelData {

        private final Map<Integer, Long> totalExpLevelRequired = new HashMap<>();
        private final int levelCap;

        public LevelData(int levelCap) {
            this.levelCap = levelCap;
            this.buildLevelRequirements();
        }

        private void buildLevelRequirements() {
            if (this.totalExpLevelRequired.isEmpty()) {
                for (int i = 1; i <= this.levelCap; i++) {
                    long prev = this.totalExpLevelRequired.getOrDefault(i - 1, 0L);
                    this.totalExpLevelRequired.put(i, prev + 150L + 100L * MathHelper.floor(Math.pow(1.2F, i)));
                }
            }
        }
    }
}
