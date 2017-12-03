/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.retrogen;

import hellfirepvp.astralsorcery.common.CommonProxy;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RetroGenController
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:18
 */
public class RetroGenController {

    private static Map<Integer, List<ChunkPos>> retroGenActive = new HashMap<>();
    private static Map<Integer, List<ChunkPos>> queuedPopulation = new HashMap<>();

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        retroGenActive.remove(event.getWorld().provider.getDimension());
        queuedPopulation.remove(event.getWorld().provider.getDimension());
    }

    //Clean up the mess we create.
    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        World w = event.getWorld();
        if(w.isRemote) return;
        ChunkPos pos = event.getChunk().getPos();
        int dimId = w.provider.getDimension();

        List<ChunkPos> queue = queuedPopulation.computeIfAbsent(dimId, (id) -> new LinkedList<>());
        queue.add(pos);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        World w = event.getWorld();
        if(w.isRemote) return;
        ChunkPos pos = event.getChunk().getPos();
        int dimId = w.provider.getDimension();

        List<ChunkPos> active = retroGenActive.computeIfAbsent(dimId, (id) -> new LinkedList<>());
        if(!event.getChunk().isTerrainPopulated() || active.contains(pos)) {
            visitChunkPopulation(w);
            return;
        }

        List<ChunkPos> queue = queuedPopulation.computeIfAbsent(dimId, (id) -> new LinkedList<>());
        queue.add(pos);
        visitChunkPopulation(w);
    }

    private void visitChunkPopulation(World w) {
        int dimId = w.provider.getDimension();
        List<ChunkPos> queue = queuedPopulation.computeIfAbsent(dimId, (id) -> new LinkedList<>());
        List<ChunkPos> active = retroGenActive.computeIfAbsent(dimId, (id) -> new LinkedList<>());
        Iterator<ChunkPos> iterator = queue.iterator();
        while (iterator.hasNext()) {
            ChunkPos pos = iterator.next();
            int chX = pos.x;
            int chZ = pos.z;

            if(w.isChunkGeneratedAt(chX + 1, chZ) &&
                    w.isChunkGeneratedAt(chX, chZ + 1) &&
                    w.isChunkGeneratedAt(chX + 1, chZ + 1)) {
                Integer chunkVersion = -1;
                if(((WorldServer) w).getChunkProvider().chunkLoader.isChunkGeneratedAt(pos.x, pos.z)) {
                    chunkVersion = ChunkVersionController.instance.getGenerationVersion(pos);
                    if(chunkVersion == null) {
                        return;
                    }
                }
                active.add(pos);
                CommonProxy.worldGenerator.handleRetroGen(w, pos, chunkVersion);
                active.remove(pos);

                iterator.remove();
            }
        }
    }

}
