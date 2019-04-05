/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import hellfirepvp.astralsorcery.common.base.RockCrystalHandler;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LegacyDataMigration
 * Created by HellFirePvP
 * Date: 05.04.2019 / 19:05
 */
public class LegacyDataMigration {

    public static void migrateRockCrystalData(Consumer<String> msgOut) {
        for (WorldServer world : DimensionManager.getWorlds()) {

            RockCrystalBuffer data = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
            Map<ChunkPos, List<BlockPos>> crystalData = data.getCrystalPositions();
            int totalChunkCount = crystalData.size();

            if (totalChunkCount > 0) {
                msgOut.accept("Migrating rock crystal data for dimension " + world.provider.getDimension());
                msgOut.accept(totalChunkCount + " chunks of crystals found!");

                int chunkCount = 0;
                int migrated = 0;
                int failed = 0;
                Iterator<List<BlockPos>> iterator = crystalData.values().iterator();
                while (iterator.hasNext()) {
                    List<BlockPos> positionList = iterator.next();
                    chunkCount++;

                    int failedThisChunk = 0;
                    for (BlockPos position : positionList) {
                        if (RockCrystalHandler.INSTANCE.addOre(world, position, true)) {
                            migrated++;
                        } else {
                            failed++;
                            failedThisChunk++;
                        }
                    }
                    if (failedThisChunk == 0) {
                        iterator.remove();
                    }

                    if (chunkCount % 500 == 0) {
                        msgOut.accept("Migrated " + chunkCount + "/" + totalChunkCount + " chunks...");
                    }
                }

                msgOut.accept("Migrated " + migrated + " entries successfully. " + failed + " entries failed to be transferred!");
            }

            data.markDirty();
        }
    }

}
