package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.IBlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionWorldHandler
 * Created by HellFirePvP
 * Date: 05.08.2016 / 11:02
 */
public class TransmissionWorldHandler {

    //If a source looses all chunks/all chunks in its network get unloaded it doesn't need to broadcast starlight anymore
    //This map exists to associate a certain chunkPosition to the involved networks in it.
    private Map<ChunkPos, List<IIndependentStarlightSource>> involvedSourceMap = new HashMap<>();

    //The counterpart to check faster
    //Removing a source here will also stop production!
    private Map<IIndependentStarlightSource, List<ChunkPos>> activeChunkMap = new HashMap<>();

    private Map<IIndependentStarlightSource, TransmissionChain> cachedSourceChain = new HashMap<>(); //The distribution source chain.

    private final World world;

    public TransmissionWorldHandler(World world) {
        this.world = world;
    }

    public void tick() {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(world);

        for (Tuple<BlockPos, IIndependentStarlightSource> sourceTuple : handler.getAllSources()) {
            BlockPos at = sourceTuple.key;
            IIndependentStarlightSource source = sourceTuple.value;
            if(!cachedSourceChain.containsKey(source)) {
                buildSourceNetwork(source, handler, at);
            }

            List<ChunkPos> activeChunks = activeChunkMap.get(source);
            if(activeChunks == null || activeChunks.isEmpty()) {
                continue; //Not producing anything.
            }

            TransmissionChain chain = cachedSourceChain.get(source);
            double starlight = source.produceStarlightTick(world, at);
            Constellation type = source.getStarlightType();
            Map<BlockPos, Float> lossMultipliers = chain.getLossMultipliers();
            for (ITransmissionReceiver rec : chain.getEndpointsNodes()) {
                BlockPos pos = rec.getPos();
                Float multiplier = lossMultipliers.get(pos);
                if (multiplier != null) {
                    rec.onStarlightReceive(world, MiscUtils.isChunkLoaded(world, new ChunkPos(pos)), type, starlight * multiplier);
                }
            }
            for (BlockPos endPointPos : chain.getUncheckedEndpointsBlock()) {
                if(MiscUtils.isChunkLoaded(world, new ChunkPos(endPointPos))) {
                    Block b = world.getBlockState(endPointPos).getBlock();
                    if(b instanceof IBlockStarlightRecipient) {
                        Float multiplier = lossMultipliers.get(endPointPos);
                        if (multiplier != null) {
                            ((IBlockStarlightRecipient) b).receiveStarlight(world, endPointPos, type, starlight * multiplier);
                        }
                    }
                }
            }
        }
    }

    private void buildSourceNetwork(IIndependentStarlightSource source, WorldNetworkHandler handler, BlockPos sourcePos) {
        TransmissionChain chain = TransmissionChain.buildFromSource(handler, sourcePos);

        cachedSourceChain.put(source, chain);
        List<ChunkPos> activeChunks = new LinkedList<>();
        for (ChunkPos pos : chain.getInvolvedChunks()) {
            List<IIndependentStarlightSource> sources = involvedSourceMap.get(pos);
            if(sources == null) {
                sources = new LinkedList<>();
                involvedSourceMap.put(pos, sources);
            }
            sources.add(source);
            if(MiscUtils.isChunkLoaded(world, pos)) {
                activeChunks.add(pos);
            }
        }
        if(!activeChunks.isEmpty()) {
            activeChunkMap.put(source, activeChunks);
        }
    }

    //Remove a source from the network to trigger recalculation!
    public void breakSourceNetwork(IIndependentStarlightSource source, BlockPos pos) {
        TransmissionChain knownChain = cachedSourceChain.get(source);
        if(knownChain != null) {
            for (ChunkPos chPos : knownChain.getInvolvedChunks()) {
                List<IIndependentStarlightSource> sources = involvedSourceMap.get(chPos);
                if(sources != null) {
                    sources.remove(source);
                    if(sources.isEmpty()) {
                        involvedSourceMap.remove(chPos);
                    }
                }
            }
        }
        activeChunkMap.remove(source);
        cachedSourceChain.remove(source);
    }

    public void informChunkUnload(ChunkPos pos) {
        List<IIndependentStarlightSource> sources = involvedSourceMap.get(pos);
        if(sources != null) {
            for (IIndependentStarlightSource source : sources) {
                List<ChunkPos> activeChunks = activeChunkMap.get(source);
                if (activeChunks != null) {
                    activeChunks.remove(pos);
                    if(activeChunks.isEmpty()) {
                        activeChunkMap.remove(source);
                    }
                }
            }
        }
    }

    public void informChunkLoad(ChunkPos pos) {
        List<IIndependentStarlightSource> sources = involvedSourceMap.get(pos);
        if(sources != null) {
            for (IIndependentStarlightSource source : sources) {
                TransmissionChain chain = cachedSourceChain.get(source);
                if(chain != null) {
                    if(chain.getInvolvedChunks().contains(pos)) {
                        if(activeChunkMap.containsKey(source)) {
                            List<ChunkPos> positions = activeChunkMap.get(source);
                            if(!positions.contains(pos)) positions.add(pos);
                        } else {
                            List<ChunkPos> positions = new LinkedList<>();
                            positions.add(pos);
                            activeChunkMap.put(source, positions);
                        }
                    }
                }
            }
        }
    }

    //Free memory before removing the object
    public void clear() {
        this.activeChunkMap.clear();
        this.cachedSourceChain.clear();
        this.involvedSourceMap.clear();
    }

}
