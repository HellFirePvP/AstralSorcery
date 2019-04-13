/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.retrogen;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.world.AstralWorldGenerator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RetroGenController
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:18
 */
public class RetroGenController {

    private static boolean inPopulation = false;
    private static boolean inCascade = false;

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        World w = event.getWorld();
        if (w.isRemote) return;
        Chunk ch = event.getChunk();

        if (!event.getChunk().isTerrainPopulated()) {
            return;
        }

        visitChunkPopulation(ch.getWorld(), ch.getPos());
    }

    private void visitChunkPopulation(World w, ChunkPos pos) {
        if (inPopulation) return;
        int chX = pos.x;
        int chZ = pos.z;

        if (w.isChunkGeneratedAt(chX + 1, chZ) &&
                w.isChunkGeneratedAt(chX, chZ + 1) &&
                w.isChunkGeneratedAt(chX + 1, chZ + 1)) {

            Integer chunkVersion = -1;
            if (((WorldServer) w).getChunkProvider().chunkLoader.isChunkGeneratedAt(chX, chZ)) {
                chunkVersion = ChunkVersionController.instance.getGenerationVersion(pos);
                if (chunkVersion == null) {
                    return;
                }
            }

            if (chunkVersion >= AstralWorldGenerator.CURRENT_WORLD_GENERATOR_VERSION) {
                return;
            }

            inPopulation = true;
            CommonProxy.worldGenerator.handleRetroGen(w, pos, chunkVersion);
            inPopulation = false;
        }

        if (inCascade) {
            return;
        }
        inCascade = true;
        if (w.isChunkGeneratedAt(chX - 1, chZ)) {
            visitChunkPopulation(w, new ChunkPos(chX - 1, chZ));
        }
        if (w.isChunkGeneratedAt(chX, chZ - 1)) {
            visitChunkPopulation(w, new ChunkPos(chX, chZ - 1));
        }
        if (w.isChunkGeneratedAt(chX - 1, chZ - 1)) {
            visitChunkPopulation(w, new ChunkPos(chX - 1, chZ - 1));
        }
        inCascade = false;
    }

}
