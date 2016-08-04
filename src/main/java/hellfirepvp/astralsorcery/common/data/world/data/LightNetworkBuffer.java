package hellfirepvp.astralsorcery.common.data.world.data;

import hellfirepvp.astralsorcery.common.data.world.CachedWorldData;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightNetworkBuffer
 * Created by HellFirePvP
 * Date: 03.08.2016 / 00:10
 */
public class LightNetworkBuffer extends CachedWorldData {

    private Map<ChunkPos, ChunkNetworkData> chunkSortedData = new HashMap<>();
    //private List<BlockPos, > TODO here.

    //specifically "highlighted" for removal.
    private List<ChunkPos> queueRemoval = new LinkedList<>();

    protected LightNetworkBuffer() {
        super(WorldCacheManager.SaveKey.LIGHT_NETWORK);
    }

    public LightNetworkBuffer(String key) {
        super(key);
    }

    public WorldNetworkHandler getNetworkHandler(World world) {
        return new WorldNetworkHandler(this, world);
    }

    @Override
    public LightNetworkBuffer constructNewData() {
        return new LightNetworkBuffer();
    }

    @Override
    public void updateTick() {
        cleanupQueuedChunks();
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

    public void createNewChunkData(@Nonnull ChunkPos pos) {
        chunkSortedData.put(pos, new ChunkNetworkData());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        chunkSortedData.clear();

        if(nbt.hasKey("chunkSortedData")) {
            NBTTagList list = nbt.getTagList("chunkSortedData", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound posTag = list.getCompoundTagAt(i);
                int chX = posTag.getInteger("chX");
                int chZ = posTag.getInteger("chZ");
                ChunkPos pos = new ChunkPos(chX, chZ);
                ChunkNetworkData data = ChunkNetworkData.loadFromNBT(posTag.getCompoundTag("netData"));
                chunkSortedData.put(pos, data);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        cleanupQueuedChunks();

        NBTTagList list = new NBTTagList();
        for (ChunkPos pos : chunkSortedData.keySet()) {
            ChunkNetworkData data = chunkSortedData.get(pos);
            NBTTagCompound posTag = new NBTTagCompound();
            posTag.setInteger("chX", pos.chunkXPos);
            posTag.setInteger("chZ", pos.chunkZPos);
            NBTTagCompound netData = new NBTTagCompound();
            data.writeToNBT(netData);
            posTag.setTag("netData", netData);
            list.appendTag(posTag);
        }
        nbt.setTag("chunkSortedData", list);
        return nbt;
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

        markDirty();
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

    public void removeSource(IStarlightSource source, BlockPos pos) {
        ChunkPos chPos = new ChunkPos(pos);
        ChunkNetworkData data = getChunkData(chPos);
        if(data == null) return; //Uuuuhm. what happened here.
        data.removeSourceTile(pos);

        checkIntegrity(chPos);

        markDirty();
    }

    public void removeTransmission(IStarlightTransmission transmission, BlockPos pos) {
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


    public static class ChunkNetworkData {

        private Map<Integer, ChunkSectionNetworkData> sections = new HashMap<>();

        //private Map<BlockPos, IStarlightTransmission> transmissionTiles = new HashMap<>();
        //private Map<BlockPos, IStarlightSource> sourceTiles = new HashMap<>();

        private static ChunkNetworkData loadFromNBT(NBTTagCompound tag) {
            ChunkNetworkData data = new ChunkNetworkData();
            if(tag.hasKey("sectionList")) {
                NBTTagList list = tag.getTagList("sectionList", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound section = list.getCompoundTagAt(i);
                    int yLevel = section.getInteger("yLevel");
                    NBTTagCompound sectionData = section.getCompoundTag("sectionData");
                    ChunkSectionNetworkData networkData = ChunkSectionNetworkData.loadFromNBT(sectionData);
                    data.sections.put(yLevel, networkData);
                }
            }
            return data;
        }

        private void writeToNBT(NBTTagCompound tag) {
            NBTTagList sectionList = new NBTTagList();
            for (Integer yLevel : sections.keySet()) {
                ChunkSectionNetworkData sectionData = sections.get(yLevel);
                NBTTagCompound sectionTag = new NBTTagCompound();
                sectionData.writeToNBT(sectionTag);

                NBTTagCompound section = new NBTTagCompound();
                section.setInteger("yLevel", yLevel);
                section.setTag("sectionData", sectionTag);
                sectionList.appendTag(section);
            }
            tag.setTag("sectionList", sectionList);
        }

        //Also allows for passing invalid yLevels outside of 0 to 15
        @Nullable
        public ChunkSectionNetworkData getSection(int yLevel) {
            return sections.get(yLevel);
        }

        /*@Nullable
        public IPrismTransmissionNode getTransmissionNode(BlockPos at) {
            int yLevel = (at.getY() & 255) >> 4;
            ChunkSectionNetworkData sectionData = sections.get(yLevel);
            if(sectionData == null) return null;
            return sectionData.getTransmissionNode(at);
        }

        public Collection<IPrismTransmissionNode> getAllTransmissionNodesNear(BlockPos at) {
            int yLevel = (at.getY() & 255) >> 4;
            List<IPrismTransmissionNode> nodes = new LinkedList<>();
            ChunkSectionNetworkData section = getSection(yLevel - 1);
            if(section != null) nodes.addAll(section.getAllTransmissionNodes());
            section = getSection(yLevel);
            if(section != null) nodes.addAll(section.getAllTransmissionNodes());
            section = getSection(yLevel + 1);
            if(section != null) nodes.addAll(section.getAllTransmissionNodes());
            return Collections.unmodifiableCollection(nodes);
        }*/

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
            ChunkSectionNetworkData section = getOrCreateSection(yLevel);
            section.removeSourceTile(pos);
        }

        private void removeTransmissionTile(BlockPos pos) {
            int yLevel = (pos.getY() & 255) >> 4;
            ChunkSectionNetworkData section = getOrCreateSection(yLevel);
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

        //TODO do read write
        private Map<BlockPos, IPrismTransmissionNode> nodes = new HashMap<>();

        private static ChunkSectionNetworkData loadFromNBT(NBTTagCompound tag) {
            ChunkSectionNetworkData netData = new ChunkSectionNetworkData();

            return netData;
        }

        private void writeToNBT(NBTTagCompound tag) {

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
