/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.world.CachedWorldData;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightUpdateHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightNetworkBuffer
 * Created by HellFirePvP
 * Date: 03.08.2016 / 00:10
 */
public class LightNetworkBuffer extends CachedWorldData {

    private Map<ChunkPos, ChunkNetworkData> chunkSortedData = new HashMap<>();
    private Map<BlockPos, IIndependentStarlightSource> starlightSources = new HashMap<>();
    private Collection<Tuple<BlockPos, IIndependentStarlightSource>> cachedSourceTuples = null;

    private List<ChunkPos> queueRemoval = new LinkedList<>();

    public LightNetworkBuffer() {
        super(WorldCacheManager.SaveKey.LIGHT_NETWORK);
    }

    public WorldNetworkHandler getNetworkHandler(World world) {
        return new WorldNetworkHandler(this, world);
    }

    @Override
    public void updateTick(World world) {
        cleanupQueuedChunks();

        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(world);

        Iterator<Map.Entry<BlockPos, IIndependentStarlightSource>> iterator = starlightSources.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, IIndependentStarlightSource> entry = iterator.next();
            BlockPos pos = entry.getKey();
            ChunkPos chPos = new ChunkPos(pos);
            IIndependentStarlightSource source = entry.getValue();

            if (MiscUtils.isChunkLoaded(world, chPos)) {
                TileEntity te = world.getTileEntity(pos); //Safe to do now.
                if (te != null) {
                    if (te instanceof IStarlightSource) {
                        if(((IStarlightSource) te).needToUpdateStarlightSource()) {
                            source.informTileStateChange((IStarlightSource) te);
                            ((IStarlightSource) te).markUpdated();
                            if(handle != null) {
                                handle.breakSourceNetwork(source);
                            }
                        }
                    } else {
                        AstralSorcery.log.warn("[AstralSorcery] Cached source at " + pos + " but didn't find the TileEntity!");
                        AstralSorcery.log.warn("[AstralSorcery] Purging cache entry and removing erroneous block!");
                        IBlockState there = world.getBlockState(pos);
                        AstralSorcery.log.warn("[AstralSorcery] Block that gets purged: " + there.getBlock().getUnlocalizedName() + " with meta " + there.getBlock().getMetaFromState(there));
                        iterator.remove();
                        world.setBlockToAir(pos);
                        ChunkNetworkData data = getChunkData(chPos);
                        if(data != null) {
                            data.removeSourceTile(pos);
                        }
                        continue;
                    }
                }
            }

            //source.onUpdate(world, pos);
        }
    }

    @Override
    public void onLoad(World world) {

        if(Config.performNetworkIntegrityCheck) {
            AstralSorcery.log.info("[LightNetworkIntegrityCheck] Performing StarlightNetwork integrity check for world " + world.provider.getDimension());
            List<IPrismTransmissionNode> invalidRemoval = new LinkedList<>();

            for (ChunkNetworkData data : chunkSortedData.values()) {
                for (ChunkSectionNetworkData secData : data.sections.values()) {
                    for (IPrismTransmissionNode node : secData.getAllTransmissionNodes()) {
                        TileEntity te = world.getTileEntity(node.getPos());
                        if(te == null || !(te instanceof IStarlightTransmission)) {
                            invalidRemoval.add(node);
                            continue;
                        }
                        IStarlightTransmission ism = (IStarlightTransmission) te;
                        IPrismTransmissionNode newNode = ism.provideTransmissionNode(node.getPos());
                        if(!node.getClass().isAssignableFrom(newNode.getClass())) {
                            invalidRemoval.add(node);
                            continue;
                        }

                        if(node.needsUpdate()) {
                            StarlightUpdateHandler.getInstance().addNode(world, node);
                        }
                        node.postLoad(world);
                    }
                }
            }

            AstralSorcery.log.info("[LightNetworkIntegrityCheck] Performed StarlightNetwork integrity check. Found " + invalidRemoval.size() + " invalid transmission nodes.");
            for (IPrismTransmissionNode node : invalidRemoval) {
                removeTransmission(node.getPos());
            }
            AstralSorcery.log.info("[LightNetworkIntegrityCheck] Removed invalid transmission nodes from the network.");

        } else {
            for (ChunkNetworkData data : chunkSortedData.values()) {
                for (ChunkSectionNetworkData secData : data.sections.values()) {
                    for (IPrismTransmissionNode node : secData.getAllTransmissionNodes()) {
                        if(node.needsUpdate()) {
                            StarlightUpdateHandler.getInstance().addNode(world, node);
                        }
                        node.postLoad(world);
                    }
                }
            }
        }
    }

    private void cleanupQueuedChunks() {
        for (ChunkPos pos : queueRemoval) {
            ChunkNetworkData data = getChunkData(pos);
            if(data != null && data.isEmpty()) {
                chunkSortedData.remove(pos);
            }
        }
        queueRemoval.clear();
    }

    @Nullable
    private ChunkNetworkData getChunkData(ChunkPos pos) {
        return chunkSortedData.get(pos);
    }

    @Nullable
    public ChunkSectionNetworkData getSectionData(BlockPos pos) {
        return getSectionData(new ChunkPos(pos), (pos.getY() & 255) >> 4);
    }

    @Nullable
    public ChunkSectionNetworkData getSectionData(ChunkPos chPos, int yLevel) {
        ChunkNetworkData data = chunkSortedData.get(chPos);
        if(data == null) return null;
        return data.getSection(yLevel);
    }

    @Nullable
    public IIndependentStarlightSource getSource(BlockPos at) {
        return starlightSources.get(at);
    }

    public Collection<Tuple<BlockPos, IIndependentStarlightSource>> getAllSources() {
        if(cachedSourceTuples == null) {
            Collection<Tuple<BlockPos, IIndependentStarlightSource>> cache = new LinkedList<>();
            for (Map.Entry<BlockPos, IIndependentStarlightSource> entry : starlightSources.entrySet()) {
                cache.add(new Tuple<>(entry.getKey(), entry.getValue()));
            }
            this.cachedSourceTuples = Collections.unmodifiableCollection(cache);
        }
        return cachedSourceTuples;
    }

    public void createNewChunkData(@Nonnull ChunkPos pos) {
        chunkSortedData.put(pos, new ChunkNetworkData());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        starlightSources.clear();
        chunkSortedData.clear();
        cachedSourceTuples = null;

        if(nbt.hasKey("chunkSortedData")) {
            NBTTagList list = nbt.getTagList("chunkSortedData", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound posTag = list.getCompoundTagAt(i);
                int chX = posTag.getInteger("chX");
                int chZ = posTag.getInteger("chZ");
                ChunkPos pos = new ChunkPos(chX, chZ);
                ChunkNetworkData data = ChunkNetworkData.loadFromNBT(posTag.getTagList("netData", 10));
                chunkSortedData.put(pos, data);
            }
        }

        if(nbt.hasKey("sources")) {
            NBTTagList list = nbt.getTagList("sources", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound sourcePos = list.getCompoundTagAt(i);
                BlockPos at = NBTUtils.readBlockPosFromNBT(sourcePos);
                ChunkSectionNetworkData section = getSectionData(at);
                if(section == null) {
                    AstralSorcery.log.warn("[AstralSorcery] Expected source tile at " + at + " but didn't even find chunk section!");
                } else {
                    IPrismTransmissionNode node = section.getTransmissionNode(at);
                    if(node == null) {
                        AstralSorcery.log.warn("[AstralSorcery] Expected source tile at " + at + " but didn't find a transmission node!");
                        continue;
                    }
                    if(!(node instanceof ITransmissionSource)) {
                        AstralSorcery.log.warn("[AstralSorcery] Expected source tile at " + at + " but transmission node isn't a source!");
                        continue;
                    }
                    NBTTagCompound comp = sourcePos.getCompoundTag("source");
                    String identifier = comp.getString("sTypeId");
                    SourceClassRegistry.SourceProvider provider = SourceClassRegistry.getProvider(identifier);
                    if(provider == null) {
                        AstralSorcery.log.warn("[AstralSorcery] Couldn't load source tile at " + at + " - invalid identifier: " + identifier);
                        continue;
                    }
                    IIndependentStarlightSource source = provider.provideEmptySource();
                    source.readFromNBT(comp);
                    this.starlightSources.put(at, source);
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        cleanupQueuedChunks();

        NBTTagList list = new NBTTagList();
        for (ChunkPos pos : chunkSortedData.keySet()) {
            ChunkNetworkData data = chunkSortedData.get(pos);
            NBTTagCompound posTag = new NBTTagCompound();
            posTag.setInteger("chX", pos.x);
            posTag.setInteger("chZ", pos.z);
            NBTTagList netData = new NBTTagList();
            data.writeToNBT(netData);
            posTag.setTag("netData", netData);
            list.appendTag(posTag);
        }
        nbt.setTag("chunkSortedData", list);

        NBTTagList sourceList = new NBTTagList();
        for (BlockPos pos : starlightSources.keySet()) {
            NBTTagCompound sourceTag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, sourceTag);
            NBTTagCompound source = new NBTTagCompound();
            IIndependentStarlightSource sourceNode = starlightSources.get(pos);
            try {
                sourceNode.writeToNBT(source);
            } catch (Exception exc) {
                AstralSorcery.log.warn("[AstralSorcery] Couldn't write source-node data for network node at " + pos.toString() + "!");
                AstralSorcery.log.warn("[AstralSorcery] This is a major problem. To be perfectly save, consider making a backup, then break or mcedit the tileentity out and place a proper/new one...");
                continue;
            }
            source.setString("sTypeId", sourceNode.getProvider().getIdentifier());
            sourceTag.setTag("source", source);
            sourceList.appendTag(sourceTag);
        }
        nbt.setTag("sources", sourceList);
    }

    //Network changing
    public void addSource(IStarlightSource source, BlockPos pos) {
        ChunkPos chPos = new ChunkPos(pos);
        ChunkNetworkData data = getChunkData(chPos);
        if(data == null) {
            createNewChunkData(chPos);
            data = getChunkData(chPos);
        }
        data.addSourceTile(pos, source);

        IIndependentStarlightSource newSource = addIndependentSource(pos, source);
        if(newSource != null) {
            Map<BlockPos, IIndependentStarlightSource> copyTr = Collections.unmodifiableMap(new HashMap<>(starlightSources));
            Thread tr = new Thread(() -> threadedUpdateSourceProximity(copyTr));
            tr.setName("StarlightNetwork-UpdateThread");
            tr.start();
        }

        markDirty();
    }

    private void threadedUpdateSourceProximity(Map<BlockPos, IIndependentStarlightSource> copyTr) {
        try {
            for (Map.Entry<BlockPos, IIndependentStarlightSource> sourceTuple : copyTr.entrySet()) {
                sourceTuple.getValue().threadedUpdateProximity(sourceTuple.getKey(), copyTr);
            }
        } catch (Exception exc) {
            AstralSorcery.log.warn("[AstralSorcery] Failed to update proximity status for source nodes.");
            exc.printStackTrace();
        }
    }

    public void addTransmission(IStarlightTransmission transmission, BlockPos pos) {
        ChunkPos chPos = new ChunkPos(pos);
        ChunkNetworkData data = getChunkData(chPos);
        if(data == null) {
            createNewChunkData(chPos);
            data = getChunkData(chPos);
        }
        data.addTransmissionTile(pos, transmission);

        markDirty();
    }

    public void removeSource(BlockPos pos) {
        ChunkPos chPos = new ChunkPos(pos);
        ChunkNetworkData data = getChunkData(chPos);
        if(data == null) return; //Uuuuhm. what happened here.
        data.removeSourceTile(pos);

        removeIndependentSource(pos);

        Map<BlockPos, IIndependentStarlightSource> copyTr = Collections.unmodifiableMap(new HashMap<>(starlightSources));
        Thread tr = new Thread(() -> threadedUpdateSourceProximity(copyTr));
        tr.setName("StarlightNetwork-UpdateThread");
        tr.start();

        checkIntegrity(chPos);
        markDirty();
    }

    public void removeTransmission(BlockPos pos) {
        ChunkPos chPos = new ChunkPos(pos);
        ChunkNetworkData data = getChunkData(chPos);
        if(data == null) return; //Not that i'm sad, it's just... uhm..
        data.removeTransmissionTile(pos);

        checkIntegrity(chPos);
        markDirty();
    }

    private void checkIntegrity(ChunkPos chPos) {
        ChunkNetworkData data = getChunkData(chPos);
        if(data == null) return;

        data.checkIntegrity(); //Integrity of sections

        if(data.isEmpty()) {
            queueRemoval.add(chPos);
        }
    }

    @Nullable
    private IIndependentStarlightSource addIndependentSource(BlockPos pos, IStarlightSource source) {
        this.cachedSourceTuples = null;

        IPrismTransmissionNode node = source.getNode();
        if(node instanceof ITransmissionSource) {
            IIndependentStarlightSource sourceNode = ((ITransmissionSource) node).provideNewIndependentSource(source);
            this.starlightSources.put(pos, sourceNode);
            return sourceNode;
        }
        return null;
    }

    private void removeIndependentSource(BlockPos pos) {
        this.starlightSources.remove(pos);
        this.cachedSourceTuples = null;
    }

    public static class ChunkNetworkData {

        private Map<Integer, ChunkSectionNetworkData> sections = new HashMap<>();

        //private Map<BlockPos, IStarlightTransmission> transmissionTiles = new HashMap<>();
        //private Map<BlockPos, IStarlightSource> sourceTiles = new HashMap<>();

        private static ChunkNetworkData loadFromNBT(NBTTagList netData) {
            ChunkNetworkData data = new ChunkNetworkData();
            /*if(tag.hasKey("sectionList")) {
                NBTTagList list = tag.getTagList("sectionList", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound section = list.getCompoundTagAt(i);
                    int yLevel = section.getInteger("yLevel");
                    NBTTagCompound sectionData = section.getCompoundTag("sectionData");
                    ChunkSectionNetworkData networkData = ChunkSectionNetworkData.loadFromNBT(sectionData);
                    data.sections.put(yLevel, networkData);
                }
            }*/
            for (int i = 0; i < netData.tagCount(); i++) {
                NBTTagCompound section = netData.getCompoundTagAt(i);
                int yLevel = section.getInteger("yLevel");
                NBTTagList sectionData = section.getTagList("sectionData", 10);
                ChunkSectionNetworkData networkData = ChunkSectionNetworkData.loadFromNBT(sectionData);
                data.sections.put(yLevel, networkData);
            }
            return data;
        }

        private void writeToNBT(NBTTagList netData) {
            for (Integer yLevel : sections.keySet()) {
                ChunkSectionNetworkData sectionData = sections.get(yLevel);
                NBTTagList sectionTag = new NBTTagList();
                sectionData.writeToNBT(sectionTag);

                NBTTagCompound section = new NBTTagCompound();
                section.setInteger("yLevel", yLevel);
                section.setTag("sectionData", sectionTag);
                netData.appendTag(section);
            }
        }

        //Also allows for passing invalid yLevels outside of 0 to 15
        @Nullable
        public ChunkSectionNetworkData getSection(int yLevel) {
            return sections.get(yLevel);
        }

        public void checkIntegrity() {
            Iterator<Integer> iterator = sections.keySet().iterator();
            while (iterator.hasNext()) {
                Integer yLevel = iterator.next();
                ChunkSectionNetworkData data = sections.get(yLevel);
                if(data.isEmpty()) iterator.remove();
            }
        }

        public boolean isEmpty() {
            return sections.isEmpty(); //No section -> no data
        }

        private ChunkSectionNetworkData getOrCreateSection(int yLevel) {
            ChunkSectionNetworkData section = getSection(yLevel);
            if(section == null) {
                section = new ChunkSectionNetworkData();
                sections.put(yLevel, section);
            }
            return section;
        }

        private void removeSourceTile(BlockPos pos) {
            int yLevel = (pos.getY() & 255) >> 4;
            ChunkSectionNetworkData section = getSection(yLevel);
            if(section == null) return; //Uhm
            section.removeSourceTile(pos);
        }

        private void removeTransmissionTile(BlockPos pos) {
            int yLevel = (pos.getY() & 255) >> 4;
            ChunkSectionNetworkData section = getSection(yLevel);
            if(section == null) return; //Guess we don't remove anything then?
            section.removeTransmissionTile(pos);
        }

        private void addSourceTile(BlockPos pos, IStarlightSource source) {
            int yLevel = (pos.getY() & 255) >> 4;
            ChunkSectionNetworkData section = getOrCreateSection(yLevel);
            section.addSourceTile(pos, source);
        }

        private void addTransmissionTile(BlockPos pos, IStarlightTransmission transmission) {
            int yLevel = (pos.getY() & 255) >> 4;
            ChunkSectionNetworkData section = getOrCreateSection(yLevel);
            section.addTransmissionTile(pos, transmission);
        }

    }

    public static class ChunkSectionNetworkData {

        private Map<BlockPos, IPrismTransmissionNode> nodes = new HashMap<>();

        private static ChunkSectionNetworkData loadFromNBT(NBTTagList sectionData) {
            ChunkSectionNetworkData netData = new ChunkSectionNetworkData();
            /*if(tag.hasKey("nodeList")) {
                NBTTagList listNodes = tag.getTagList("nodeList", 10);
                for (int i = 0; i < listNodes.tagCount(); i++) {
                    NBTTagCompound nodeComp = listNodes.getCompoundTagAt(i);
                    BlockPos pos = NBTUtils.readBlockPosFromNBT(nodeComp);

                    NBTTagCompound prismComp = nodeComp.getCompoundTag("nodeTag");
                    String nodeIdentifier = prismComp.getString("trNodeId");
                    TransmissionClassRegistry.TransmissionProvider provider = TransmissionClassRegistry.getProvider(nodeIdentifier);
                    if(provider == null) {
                        AstralSorcery.log.warn("Couldn't load node tile at " + pos + " - invalid identifier: " + nodeIdentifier);
                        continue;
                    }
                    IPrismTransmissionNode node = provider.provideEmptyNode();
                    node.readFromNBT(prismComp);
                    netData.nodes.put(pos, node);
                }
            }*/
            for (int i = 0; i < sectionData.tagCount(); i++) {
                NBTTagCompound nodeComp = sectionData.getCompoundTagAt(i);
                BlockPos pos = NBTUtils.readBlockPosFromNBT(nodeComp);

                NBTTagCompound prismComp = nodeComp.getCompoundTag("nodeTag");
                String nodeIdentifier = prismComp.getString("trNodeId");
                TransmissionClassRegistry.TransmissionProvider provider = TransmissionClassRegistry.getProvider(nodeIdentifier);
                if(provider == null) {
                    AstralSorcery.log.warn("[AstralSorcery] Couldn't load node tile at " + pos + " - invalid identifier: " + nodeIdentifier);
                    continue;
                }
                IPrismTransmissionNode node = provider.provideEmptyNode();
                node.readFromNBT(prismComp);
                netData.nodes.put(pos, node);
            }
            return netData;
        }

        private void writeToNBT(NBTTagList sectionData) {
            for (Map.Entry<BlockPos, IPrismTransmissionNode> node : nodes.entrySet()) {
                try {
                    NBTTagCompound nodeComp = new NBTTagCompound();
                    NBTUtils.writeBlockPosToNBT(node.getKey(), nodeComp);

                    NBTTagCompound prismComp = new NBTTagCompound();
                    IPrismTransmissionNode prismNode = node.getValue();
                    prismNode.writeToNBT(prismComp);
                    prismComp.setString("trNodeId", prismNode.getProvider().getIdentifier());

                    nodeComp.setTag("nodeTag", prismComp);
                    sectionData.appendTag(nodeComp);
                } catch (Exception exc) {
                    try {
                        BlockPos at = node.getKey();
                        AstralSorcery.log.warn("[AstralSorcery] Couldn't write node data for network node at " + at.toString() + "!");
                        AstralSorcery.log.warn("[AstralSorcery] This is a major problem. To be perfectly save, consider making a backup, then break or mcedit the tileentity out and place a proper/new one...");
                    } catch (Exception exc2) {
                        try {
                            BlockPos at = node.getValue().getPos();
                            AstralSorcery.log.warn("[AstralSorcery] Couldn't write node data for network node at " + at.toString() + "!");
                            AstralSorcery.log.warn("[AstralSorcery] This is a major problem. To be perfectly save, consider making a backup, then break or mcedit the tileentity out and place a proper/new one...");
                        } catch (Exception exc3) {
                            //Duh. we don't have much information if everything's inaccessible
                            AstralSorcery.log.warn("[AstralSorcery] Couldn't write node data for a network node! Skipping...");
                        }
                    }
                }
            }
        }

        public boolean isEmpty() {
            return nodes.isEmpty();
        }

        public Collection<IPrismTransmissionNode> getAllTransmissionNodes() {
            return Collections.unmodifiableCollection(nodes.values());
        }

        @Nullable
        public IPrismTransmissionNode getTransmissionNode(BlockPos at) {
            return nodes.get(at);
        }

        private void removeSourceTile(BlockPos pos) {
            //sourceTiles.remove(pos);
            removeNode(pos);
        }

        private void removeTransmissionTile(BlockPos pos) {
            //transmissionTiles.remove(pos);
            removeNode(pos);
        }

        private void removeNode(BlockPos pos) {
            nodes.remove(pos);
        }

        private void addSourceTile(BlockPos pos, IStarlightSource source) {
            //sourceTiles.put(pos, source);
            addNode(pos, source);
        }

        private void addTransmissionTile(BlockPos pos, IStarlightTransmission transmission) {
            //transmissionTiles.put(pos, transmission);
            addNode(pos, transmission);
        }

        private void addNode(BlockPos pos, IStarlightTransmission transmission) {
            nodes.put(pos, transmission.provideTransmissionNode(pos));
        }

    }

}
