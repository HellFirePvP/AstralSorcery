/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.level;

import hellfirepvp.astralsorcery.common.config.server.GeneralConfig;
import hellfirepvp.astralsorcery.common.entity.EntityShootingStar;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.fml.LogicalSide;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ShootingStarHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ShootingStarHandler {

    private final LevelSkyContext ctx;

    private final Set<UUID> spawnedPlayerStars = new HashSet<>();
    private int lastTrackedDay = -1;

    ShootingStarHandler(LevelSkyContext ctx) {
        this.ctx = ctx;
    }

    public void tick(Level level) {
        if (!(level instanceof ServerLevel sLevel)) return;
        DimensionType type = sLevel.dimensionType();
        if (!type.hasSkyLight() || type.hasCeiling() || !type.natural()) return;

        int dayLength = GeneralConfig.CONFIG.dayLength.get();
        int currentDay = (int) (level.dayTime() / dayLength);
        if (currentDay != this.lastTrackedDay) {
            this.lastTrackedDay = currentDay;
            this.spawnedPlayerStars.clear();
        }

        int dayTime = (int) (sLevel.getDayTime() % dayLength);
        RandomSource rand = this.ctx.getRandom(hash(currentDay, dayTime));

        int midnightTick = Math.round(dayLength * 0.75F);
        int spawnRange = dayLength / 12;
        int perTickSpawnChance = dayLength / 6;
        if (dayTime >= (midnightTick - spawnRange) && dayTime <= (midnightTick + spawnRange)) {
            sLevel.players().forEach(sPlayer -> {
                if (ResearchManager.getProgress(sPlayer, LogicalSide.SERVER).getTierReached().isThisLaterOrEqual(ResearchTier.ILLUMINATION)) {
                    if (!this.spawnedPlayerStars.contains(sPlayer.getUUID()) && rand.nextInt(perTickSpawnChance) == 0) {
                        this.spawnedPlayerStars.add(sPlayer.getUUID());

                        Vector3 dir = Vector3.random(rand).setY(0).normalize().multiply(0.15F + rand.nextFloat() * 0.04F);
                        Vector3 pos = new Vector3(sPlayer).setY(EntityShootingStar.getYLevelCutoff(sLevel) + 40 + rand.nextInt(30));
                        EntityShootingStar shootingStar = EntityShootingStar.create(sLevel, pos, dir);
                        sLevel.addFreshEntity(shootingStar);
                    }
                }
            });
        }
    }

    private long hash(int day, int dayTime) {
        long hash = ((long) day << 32) | (dayTime & 0xFFFFFFFFL);
        hash = (hash ^ (hash >>> 16)) * 0x85ebca6bL;
        hash = (hash ^ (hash >>> 13)) * 0xc2b2ae35L;
        hash = hash ^ (hash >>> 16);
        return hash;
    }
}
