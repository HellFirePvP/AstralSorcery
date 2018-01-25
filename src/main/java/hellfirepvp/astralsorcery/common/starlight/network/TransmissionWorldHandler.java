/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.astralsorcery.common.block.network.IBlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.DataLightBlockEndpoints;
import hellfirepvp.astralsorcery.common.data.DataLightConnections;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionWorldHandler
 * Created by HellFirePvP
 * Date: 05.08.2016 / 11:02
 */
public class TransmissionWorldHandler {

    private static final Random rand = new Random();

    //If a source looses all chunks/all chunks in its network get unloaded it doesn't need to broadcast starlight anymore
    //This map exists to associate a certain chunkPosition to the involved networks in it.
    private Map<ChunkPos, List<IIndependentStarlightSource>> involvedSourceMap = new HashMap<>();

    //The counterpart to check faster
    //Removing a source here will also stop production!
    private Map<IIndependentStarlightSource, List<ChunkPos>> activeChunkMap = new HashMap<>();

    private Map<IIndependentStarlightSource, TransmissionChain> cachedSourceChain = new HashMap<>(); //The distribution source chain.

    private Map<BlockPos, List<IIndependentStarlightSource>> posToSourceMap = new HashMap<>();

    //Contains a list of source positions whose sources currently calculate their network.
    private List<BlockPos> sourcePosBuilding = new LinkedList<>();

    private final Object accessLock = new Object();
    private final World world;

    public TransmissionWorldHandler(World world) {
        this.world = world;
    }

    public void tick() {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(world);

        for (Tuple<BlockPos, IIndependentStarlightSource> sourceTuple : handler.getAllSources()) {
            BlockPos at = sourceTuple.key;
            IIndependentStarlightSource source = sourceTuple.value;

            synchronized (accessLock) {
                if(!cachedSourceChain.containsKey(source)) {
                    if(!sourcePosBuilding.contains(at)) {
                        sourcePosBuilding.add(at);
                        buildSourceNetworkThreaded(source, handler, at);
                    }
                }

                List<ChunkPos> activeChunks = activeChunkMap.get(source);
                if(activeChunks == null || activeChunks.isEmpty()) {
                    continue; //Not producing anything.
                }

                TransmissionChain chain = cachedSourceChain.get(source);
                double starlight = source.produceStarlightTick(world, at);
                IWeakConstellation type = source.getStarlightType();
                if (type == null) continue;

                Map<BlockPos, Float> lossMultipliers = chain.getLossMultipliers();
                for (ITransmissionReceiver rec : chain.getEndpointsNodes()) {
                    BlockPos pos = rec.getPos();
                    Float multiplier = lossMultipliers.get(pos);
                    if (multiplier != null) {
                        rec.onStarlightReceive(world, MiscUtils.isChunkLoaded(world, new ChunkPos(pos)), type, starlight * multiplier);
                    }
                }

                if(starlight > 0.1D) {
                    for (IPrismTransmissionNode node : chain.getTransmissionUpdateList()) {
                        node.onTransmissionTick(world);
                    }
                }

                Iterator<BlockPos> iterator = chain.getUncheckedEndpointsBlock().iterator();
                while (iterator.hasNext()) {
                    BlockPos endPointPos = iterator.next();
                    if (MiscUtils.isChunkLoaded(world, new ChunkPos(endPointPos))) {
                        IBlockState endState = world.getBlockState(endPointPos);
                        Block b = endState.getBlock();
                        if (b instanceof IBlockStarlightRecipient) {
                            Float multiplier = lossMultipliers.get(endPointPos);
                            if (multiplier != null) {
                                ((IBlockStarlightRecipient) b).receiveStarlight(world, rand, endPointPos, type, starlight * multiplier);
                            }
                        } else {
                            StarlightNetworkRegistry.IStarlightBlockHandler handle = StarlightNetworkRegistry.getStarlightHandler(world, endPointPos, endState);
                            if(handle != null) {
                                Float multiplier = lossMultipliers.get(endPointPos);
                                if (multiplier != null) {
                                    handle.receiveStarlight(world, rand, endPointPos, type, starlight * multiplier);
                                }
                            } else {
                                chain.updatePosAsResolved(world, endPointPos);
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildSourceNetworkThreaded(IIndependentStarlightSource source, WorldNetworkHandler handler, BlockPos sourcePos) {
        TransmissionChain.threadedBuildTransmissionChain(this, source, handler, sourcePos);
    }

    void threadTransmissionChainCallback(TransmissionChain chain, IIndependentStarlightSource source, WorldNetworkHandler handle, BlockPos sourcePos) {
        synchronized (accessLock) {
            sourcePosBuilding.remove(sourcePos);

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
            for (BlockPos pos : chain.getLossMultipliers().keySet()) {
                List<IIndependentStarlightSource> sources = posToSourceMap.get(pos);
                if(sources == null) {
                    sources = new LinkedList<>();
                    posToSourceMap.put(pos, sources);
                }
                sources.add(source);
            }
            List<IIndependentStarlightSource> sources = posToSourceMap.get(sourcePos);
            if(sources == null) {
                sources = new LinkedList<>();
                posToSourceMap.put(sourcePos, sources);
            }
            sources.add(source);
        }
    }

    //Fired if the node's state related to the network changes.
    //Break all networks associated with that node to trigger recalculations as needed.
    public void notifyTransmissionNodeChange(IPrismTransmissionNode node) {
        BlockPos pos = node.getPos();
        synchronized (accessLock) {
            List<IIndependentStarlightSource> sources = posToSourceMap.get(pos);
            if(sources != null) {
                new ArrayList<>(sources).forEach(this::breakSourceNetwork);
            }
        }
    }

    //Remove a source from the network to trigger recalculation!
    public void breakSourceNetwork(IIndependentStarlightSource source) {
        synchronized (accessLock) {
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
                for (BlockPos pos : knownChain.getLossMultipliers().keySet()) {
                    List<IIndependentStarlightSource> sources = posToSourceMap.get(pos);
                    if(sources != null) {
                        sources.remove(source);
                        if(sources.isEmpty()) {
                            posToSourceMap.remove(pos);
                        }
                    }
                }
                Thread tr = new Thread(() -> {
                    DataLightConnections connections = SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_CONNECTIONS);
                    connections.removeOldConnectionsThreaded(world.provider.getDimension(), knownChain.getFoundConnections());
                });
                tr.start();
                Thread t = new Thread(() -> {
                    DataLightBlockEndpoints connections = SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS);
                    connections.removeEndpoints(world.provider.getDimension(), knownChain.getResolvedNormalBlockPositions());
                });
                t.start();
            }
            activeChunkMap.remove(source);
            cachedSourceChain.remove(source);
        }
    }

    public void informChunkUnload(ChunkPos pos) {
        synchronized (accessLock) {
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
    }

    public void informChunkLoad(ChunkPos pos) {
        synchronized (accessLock) {
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
    }

    //Free memory before removing the object
    public void clear(int dimId) {
        synchronized (accessLock) {
            this.activeChunkMap.clear();
            this.cachedSourceChain.clear();
            this.involvedSourceMap.clear();
            this.posToSourceMap.clear();
            DataLightConnections connections = SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_CONNECTIONS);
            connections.clearDimensionPositions(dimId);
            DataLightBlockEndpoints endpoints = SyncDataHolder.getDataServer(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS);
            endpoints.clearDimensionEndpoints(dimId);
        }
    }

}
