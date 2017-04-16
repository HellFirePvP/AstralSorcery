/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.retrogen;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.ChunkVersionBuffer;
import hellfirepvp.astralsorcery.common.world.AstralWorldGenerator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RetroGenController
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:18
 */
public class RetroGenController {

    private static List<ChunkPos> retroGenActive = new LinkedList<>();

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        ChunkPos pos = event.getChunk().getChunkCoordIntPair();
        if(event.getWorld().isRemote || !event.getChunk().isTerrainPopulated() || retroGenActive.contains(pos)) return;

        Integer chunkVersion = -1;
        if(((AnvilChunkLoader) ((WorldServer) event.getWorld()).getChunkProvider().chunkLoader).chunkExists(event.getWorld(), pos.chunkXPos, pos.chunkZPos)) {
            chunkVersion = ChunkVersionController.instance.getGenerationVersion(pos);
            if(chunkVersion == null) {
                AstralSorcery.log.info("[AstralSorcery] No ChunkVersion found for Chunk: " + pos.toString() + " - Skipping RetroGen...");
                return;
            }
        }
        AstralSorcery.log.info("[AstralSorcery] Attempting AstralSorcery retrogen for chunk "+ pos.toString() +
                " - Version " + chunkVersion + " -> " + AstralWorldGenerator.CURRENT_WORLD_GENERATOR_VERSION +
                " - Stack: " + retroGenActive.size());
        retroGenActive.add(pos);
        CommonProxy.worldGenerator.handleRetroGen(event.getWorld(), pos, chunkVersion);
        retroGenActive.remove(pos);
    }

}
