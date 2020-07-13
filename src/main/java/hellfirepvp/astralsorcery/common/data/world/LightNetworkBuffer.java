/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.entry.LightNetworkConfig;
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
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockStateHelper;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.base.SectionWorldData;
import hellfirepvp.observerlib.common.data.base.WorldSection;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightNetworkBuffer
 * Created by HellFirePvP
 * Date: 03.08.2016 / 00:10
 */
public class LightNetworkBuffer extends SectionWorldData<LightNetworkBuffer.ChunkNetworkData> {

    private Map<BlockPos, IIndependentStarlightSource> starlightSources = new HashMap<>();
    private Collection<Tuple<BlockPos, IIndependentStarlightSource>> cachedSourceTuples = null;

    private Set<BlockPos> queueRemoval = new HashSet<>();

    public LightNetworkBuffer(WorldCacheDomain.SaveKey<?> key) {
        super(key, PRECISION_CHUNK);
    }

    public WorldNetworkHandler getNetworkHandler(World world) {
        return new WorldNetworkHandler(this, world);
    }

    @Override
    protected ChunkNetworkData createNewSection(int sectionX, int sectionZ) {
        return new ChunkNetworkData(sectionX, sectionZ);
    }

    @Override
    public void updateTick(World world) {
        cleanupQueuedChunks();

        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(world);

        Iterator<Map.Entry<BlockPos, IIndependentStarlightSource>> iterator = starlightSources.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, IIndependentStarlightSource> entry = iterator.next();
            BlockPos pos = entry.getKey();
            IIndependentStarlightSource source = entry.getValue();

            MiscUtils.executeWithChunk(world, pos, () -> {
                TileEntity te = world.getTileEntity(pos); //Safe to do now.
                if (te != null) {
                    if (te instanceof IStarlightSource) {
                        if (((IStarlightSource) te).needsToRefreshNetworkChain()) {
                            if (handle != null) {
                                handle.breakSourceNetwork(source);
                            }
                            ((IStarlightSource) te).markChainRebuilt();
                        }
                    } else {
                        BlockState actual = world.getBlockState(pos);
                        AstralSorcery.log.warn("Cached source at " + pos + " but didn't find the TileEntity!");
                        AstralSorcery.log.warn("Purging cache entry and removing erroneous block!");
                        AstralSorcery.log.warn("Block that gets purged: " + BlockStateHelper.serialize(actual));
                        iterator.remove();
                        if (world.setBlockState(pos, actual.getFluidState().getBlockState())) {
                            ChunkNetworkData data = getSection(pos);
                            if (data != null) {
                                data.removeSourceTile(pos);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onLoad(IWorld world) {
        super.onLoad(world);

        if (LightNetworkConfig.CONFIG.performNetworkIntegrityCheck.get()) {
            AstralSorcery.log.info("[LightNetworkIntegrityCheck] Performing StarlightNetwork integrity check for world " + world.getDimension().getType().getId());
            List<IPrismTransmissionNode> invalidRemoval = new LinkedList<>();

            for (ChunkNetworkData data : getSections()) {
                for (ChunkSectionNetworkData secData : data.sections.values()) {
                    for (IPrismTransmissionNode node : secData.getAllTransmissionNodes()) {
                        TileEntity te = world.getTileEntity(node.getLocationPos());
                        if (!(te instanceof IStarlightTransmission)) {
                            invalidRemoval.add(node);
                            continue;
                        }
                        IStarlightTransmission ism = (IStarlightTransmission) te;
                        IPrismTransmissionNode newNode = ism.provideTransmissionNode(node.getLocationPos());
                        if (!node.getClass().isAssignableFrom(newNode.getClass())) {
                            invalidRemoval.add(node);
                            continue;
                        }

                        if (node.needsUpdate()) {
                            StarlightUpdateHandler.getInstance().addNode(world, node);
                        }
                        node.postLoad(world);
                    }
                }
            }

            AstralSorcery.log.info("[LightNetworkIntegrityCheck] Performed StarlightNetwork integrity check. Found " + invalidRemoval.size() + " invalid transmission nodes.");
            for (IPrismTransmissionNode node : invalidRemoval) {
                removeTransmission(node.getLocationPos());
            }
            AstralSorcery.log.info("[LightNetworkIntegrityCheck] Removed invalid transmission nodes from the network.");

        } else {
            for (ChunkNetworkData data : getSections()) {
                for (ChunkSectionNetworkData secData : data.sections.values()) {
                    for (IPrismTransmissionNode node : secData.getAllTransmissionNodes()) {
                        if (node.needsUpdate()) {
                            StarlightUpdateHandler.getInstance().addNode(world, node);
                        }
                        node.postLoad(world);
                    }
                }
            }
        }
    }

    private void cleanupQueuedChunks() {
        for (BlockPos pos : queueRemoval) {
            ChunkNetworkData data = getSection(pos);
            if (data != null && data.isEmpty()) {
                this.removeSection(data);
            }
        }
        queueRemoval.clear();
    }

    @Nullable
    public ChunkSectionNetworkData getSectionData(BlockPos pos) {
        ChunkNetworkData data = getSection(pos);
        if (data == null) return null;
        return data.getSection(pos.getY() >> 4);
    }

    @Nullable
    public IIndependentStarlightSource getSource(BlockPos at) {
        return starlightSources.get(at);
    }

    public Collection<Tuple<BlockPos, IIndependentStarlightSource>> getAllSources() {
        if (cachedSourceTuples == null) {
            Collection<Tuple<BlockPos, IIndependentStarlightSource>> cache = new LinkedList<>();
            for (Map.Entry<BlockPos, IIndependentStarlightSource> entry : starlightSources.entrySet()) {
                cache.add(new Tuple<>(entry.getKey(), entry.getValue()));
            }
            this.cachedSourceTuples = Collections.unmodifiableCollection(cache);
        }
        return cachedSourceTuples;
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        starlightSources.clear();
        cachedSourceTuples = null;

        if (nbt.contains("sources")) {
            ListNBT list = nbt.getList("sources", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT sourcePos = list.getCompound(i);
                BlockPos at = NBTHelper.readBlockPosFromNBT(sourcePos);

                CompoundNBT comp = sourcePos.getCompound("source");
                ResourceLocation identifier = new ResourceLocation(comp.getString("sTypeId"));
                SourceClassRegistry.SourceProvider provider = SourceClassRegistry.getProvider(identifier);
                if (provider == null) {
                    AstralSorcery.log.warn("Couldn't load source tile at " + at + " - invalid identifier: " + identifier);
                    continue;
                }
                IIndependentStarlightSource source = provider.provideEmptySource();
                source.readFromNBT(comp);
                this.starlightSources.put(at, source);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        cleanupQueuedChunks();

        ListNBT sourceList = new ListNBT();
        for (BlockPos pos : starlightSources.keySet()) {
            CompoundNBT sourceTag = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(pos, sourceTag);
            CompoundNBT source = new CompoundNBT();
            IIndependentStarlightSource sourceNode = starlightSources.get(pos);
            try {
                sourceNode.writeToNBT(source);
            } catch (Exception exc) {
                AstralSorcery.log.warn("Couldn't write source-node data for network node at " + pos.toString() + "!");
                AstralSorcery.log.warn("This is a major problem. To be perfectly save, consider making a backup, then break or mcedit the tileentity out and place a proper/new one...");
                continue;
            }
            source.putString("sTypeId", sourceNode.getProvider().getIdentifier().toString());
            sourceTag.put("source", source);
            sourceList.add(sourceTag);
        }
        nbt.put("sources", sourceList);
    }

    //Network changing
    public void addSource(IStarlightSource source, BlockPos pos) {
        ChunkNetworkData data = getOrCreateSection(pos);
        data.addSourceTile(pos, source);

        IIndependentStarlightSource newSource = addIndependentSource(pos, source);
        if (newSource != null) {
            Map<BlockPos, IIndependentStarlightSource> copyTr = Collections.unmodifiableMap(new HashMap<>(starlightSources));
            Thread tr = new Thread(() -> threadedUpdateSourceProximity(copyTr));
            tr.setName("StarlightNetwork-UpdateThread");
            tr.start();
        }

        markDirty(data);
    }

    private void threadedUpdateSourceProximity(Map<BlockPos, IIndependentStarlightSource> copyTr) {
        try {
            for (Map.Entry<BlockPos, IIndependentStarlightSource> sourceTuple : copyTr.entrySet()) {
                sourceTuple.getValue().threadedUpdateProximity(sourceTuple.getKey(), copyTr);
            }
        } catch (Exception exc) {
            AstralSorcery.log.warn("Failed to update proximity status for source nodes.");
            exc.printStackTrace();
        }
    }

    public void addTransmission(IStarlightTransmission transmission, BlockPos pos) {
        ChunkNetworkData data = getOrCreateSection(pos);
        data.addTransmissionTile(pos, transmission);

        markDirty(data);
    }

    public void removeSource(BlockPos pos) {
        ChunkNetworkData data = getSection(pos);
        if (data == null) return; //Uuuuhm. what happened here.
        data.removeSourceTile(pos);

        removeIndependentSource(pos);

        Map<BlockPos, IIndependentStarlightSource> copyTr = Collections.unmodifiableMap(new HashMap<>(starlightSources));
        Thread tr = new Thread(() -> threadedUpdateSourceProximity(copyTr));
        tr.setName("StarlightNetwork-UpdateThread");
        tr.start();

        checkIntegrity(pos);
        markDirty(data);
    }

    public void removeTransmission(BlockPos pos) {
        ChunkNetworkData data = getSection(pos);
        if (data == null) return; //Not that i'm sad, it's just... uhm..
        data.removeTransmissionTile(pos);

        checkIntegrity(pos);
        markDirty(data);
    }

    private void checkIntegrity(BlockPos actualPos) {
        ChunkNetworkData data = getSection(actualPos);
        if (data == null) return;

        data.checkIntegrity(); //Integrity of sections

        if (data.isEmpty()) {
            queueRemoval.add(actualPos);
        }
    }

    @Nullable
    private IIndependentStarlightSource addIndependentSource(BlockPos pos, IStarlightSource source) {
        this.cachedSourceTuples = null;

        IPrismTransmissionNode node = source.getNode();
        if (node instanceof ITransmissionSource) {
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

    public static class ChunkNetworkData extends WorldSection {

        private Map<Integer, ChunkSectionNetworkData> sections = new HashMap<>();

        ChunkNetworkData(int sX, int sZ) {
            super(sX, sZ);
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            for (String key : tag.keySet()) {
                int yLevel;
                try {
                    yLevel = Integer.parseInt(key);
                } catch (NumberFormatException exc) {
                    continue;
                }
                ListNBT yData = tag.getList(key, Constants.NBT.TAG_COMPOUND);
                ChunkSectionNetworkData sectionNetData = ChunkSectionNetworkData.loadFromNBT(yData);
                this.sections.put(yLevel, sectionNetData);
            }
        }

        @Override
        public void writeToNBT(CompoundNBT data) {
            for (Integer yLevel : sections.keySet()) {
                ChunkSectionNetworkData sectionData = sections.get(yLevel);
                ListNBT sectionTag = new ListNBT();
                sectionData.writeToNBT(sectionTag);
                data.put(String.valueOf(yLevel), sectionTag);
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
                if (data.isEmpty()) iterator.remove();
            }
        }

        public boolean isEmpty() {
            return sections.isEmpty(); //No section -> no data
        }

        private ChunkSectionNetworkData getOrCreateSection(int yLevel) {
            ChunkSectionNetworkData section = getSection(yLevel);
            if (section == null) {
                section = new ChunkSectionNetworkData();
                sections.put(yLevel, section);
            }
            return section;
        }

        private void removeSourceTile(BlockPos pos) {
            int yLevel = (pos.getY() & 255) >> 4;
            ChunkSectionNetworkData section = getSection(yLevel);
            if (section == null) return; //Uhm
            section.removeSourceTile(pos);
        }

        private void removeTransmissionTile(BlockPos pos) {
            int yLevel = (pos.getY() & 255) >> 4;
            ChunkSectionNetworkData section = getSection(yLevel);
            if (section == null) return; //Guess we don't remove anything then?
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

        private static ChunkSectionNetworkData loadFromNBT(ListNBT sectionData) {
            ChunkSectionNetworkData netData = new ChunkSectionNetworkData();
            for (int i = 0; i < sectionData.size(); i++) {
                CompoundNBT nodeComp = sectionData.getCompound(i);
                BlockPos pos = NBTHelper.readBlockPosFromNBT(nodeComp);

                CompoundNBT prismComp = nodeComp.getCompound("nodeTag");
                ResourceLocation nodeIdentifier = new ResourceLocation(prismComp.getString("trNodeId"));
                TransmissionProvider provider = TransmissionClassRegistry.getProvider(nodeIdentifier);
                if (provider == null) {
                    AstralSorcery.log.warn("Couldn't load node tile at " + pos + " - invalid identifier: " + nodeIdentifier);
                    continue;
                }
                IPrismTransmissionNode node = provider.get();
                node.readFromNBT(prismComp);
                netData.nodes.put(pos, node);
            }
            return netData;
        }

        private void writeToNBT(ListNBT sectionData) {
            for (Map.Entry<BlockPos, IPrismTransmissionNode> node : nodes.entrySet()) {
                try {
                    CompoundNBT nodeComp = new CompoundNBT();
                    NBTHelper.writeBlockPosToNBT(node.getKey(), nodeComp);

                    CompoundNBT prismComp = new CompoundNBT();
                    IPrismTransmissionNode prismNode = node.getValue();
                    prismNode.writeToNBT(prismComp);
                    prismComp.putString("trNodeId", prismNode.getProvider().getIdentifier().toString());

                    nodeComp.put("nodeTag", prismComp);
                    sectionData.add(nodeComp);
                } catch (Exception exc) {
                    try {
                        BlockPos at = node.getKey();
                        AstralSorcery.log.warn("Couldn't write node data for network node at " + at.toString() + "!");
                        AstralSorcery.log.warn("This is a major problem. To be perfectly save, consider making a backup, then break or mcedit the tileentity out and place a proper/new one...");
                    } catch (Exception exc2) {
                        try {
                            BlockPos at = node.getValue().getLocationPos();
                            AstralSorcery.log.warn("Couldn't write node data for network node at " + at.toString() + "!");
                            AstralSorcery.log.warn("This is a major problem. To be perfectly save, consider making a backup, then break or mcedit the tileentity out and place a proper/new one...");
                        } catch (Exception exc3) {
                            //Duh. we don't have much information if everything's inaccessible
                            AstralSorcery.log.warn("Couldn't write node data for a network node! Skipping...");
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
