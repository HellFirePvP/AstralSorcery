/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world;

import com.google.common.io.Files;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.world.data.LightNetworkBuffer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldCacheManager
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:15
 */
public class WorldCacheManager implements ITickHandler {

    private static WorldCacheManager instance = new WorldCacheManager();
    private static Map<Integer, Map<SaveKey, CachedWorldData>> cachedData = new HashMap<>();
    private static File saveDir;

    private WorldCacheManager() {}

    public static WorldCacheManager getInstance() {
        return instance;
    }

    public static void wipeCache() {
        cachedData.clear();
        saveDir = null;
    }

    public static <T extends CachedWorldData> T getOrLoadData(World world, SaveKey key) {
        CachedWorldData data = getFromCache(world, key);
        if(data != null) return (T) data;
        return (T) loadAndCache(world, key);
    }

    private synchronized static DataFileSet getDataFile(World world, String key) {
        if(world.isRemote)
            throw new IllegalArgumentException("Tried to access data structure on clientside. This is a severe implementation error!");
        if(saveDir == null) {
            saveDir = new File(world.getSaveHandler().getWorldDirectory(), "AstralSorceryData");
            if(!saveDir.exists()) {
                saveDir.mkdirs();
            } else {
                ensureFolder(saveDir);
            }
        }
        File worldDir = new File(saveDir, "DIM_" + world.getDimension().getType().getId());
        if(!worldDir.exists()) {
            worldDir.mkdirs();
        } else {
            ensureFolder(worldDir);
        }
        return new DataFileSet(new File(worldDir, key + ".dat"));
    }

    private static void ensureFolder(File f) {
        if(!f.isDirectory()) {
            AstralSorcery.log.warn("AstralSorcery dataFile exists, but is a file instead of a folder! Please ensure that this is a folder/delete the file!");
            AstralSorcery.log.warn("Encountered illegal state. Crashing to prevent further, harder to resolve errors!");
            throw new IllegalStateException("Affected file: " + f.getAbsolutePath());
        }
    }

    @Nullable
    private static CachedWorldData getFromCache(World world, SaveKey key) {
        int dimId = world.getDimension().getType().getId();
        if(!cachedData.containsKey(dimId)) return null;
        Map<SaveKey, CachedWorldData> dataMap = cachedData.get(dimId);
        return dataMap.get(key);
    }

    private static CachedWorldData loadAndCache(World world, SaveKey key) {
        CachedWorldData data = getFromCache(world, key);
        if(data != null) return data;

        int dimId = world.getDimension().getType().getId();
        CachedWorldData loaded = loadDataFromFile(world, key);
        if(!cachedData.containsKey(dimId)) {
            cachedData.put(dimId, new HashMap<>());
        }
        Map<SaveKey, CachedWorldData> dataMap = cachedData.get(dimId);
        if(dataMap.containsKey(key)) {
            AstralSorcery.log.warn("Duplicate loading of the same WorldData! Discarding old data.");
            AstralSorcery.log.warn("Affected data: Dim=" + dimId + " key=" + key.identifier);
            dataMap.remove(key);
        }
        dataMap.put(key, loaded);
        loaded.onLoad(world);
        return loaded;
    }

    private static CachedWorldData loadDataFromFile(World world, SaveKey key) {
        DataFileSet f = getDataFile(world, key.identifier);
        if (!f.actualFile.exists() && !f.backupFile.exists()) {
            return key.getNewInstance();
        }
        AstralSorcery.log.info("Load CachedWorldData '" + key.identifier + "' for world " + world.getDimension().getType().getId());
        boolean errored = false;
        CachedWorldData data = null;
        try {
            if(f.actualFile.exists()) {
                data = attemptLoad(key, f.actualFile);
            }
        } catch (Exception exc) {
            AstralSorcery.log.info("Loading worlddata '" + key.identifier + "' failed for its actual save file. Attempting load from backup file.");
            errored = true;
        }
        if(data == null) {
            try {
                if(f.backupFile.exists()) {
                    data = attemptLoad(key, f.backupFile);
                }
            } catch (Exception exc) {
                AstralSorcery.log.info("Loading worlddata '" + key.identifier + "' failed for its backup save file. Creating empty one for current runtime and copying erroneous files to error files.");
                errored = true;
            }
        }
        if(data == null && errored) {
            DataFileSet errorSet = f.getErrorFileSet();
            try {
                if(f.actualFile.exists()) {
                    Files.copy(f.actualFile, errorSet.actualFile);
                    f.actualFile.delete();
                }
                if(f.backupFile.exists()) {
                    Files.copy(f.backupFile, errorSet.backupFile);
                    f.backupFile.delete();
                }
            } catch (Exception e) {
                AstralSorcery.log.info("Attempting to copy erroneous worlddata '" + key.identifier + "' to its error files failed.");
                e.printStackTrace();
            }
        }
        if(data == null) {
            if(errored) {
                DataFileSet errorSet = f.getErrorFileSet();
                try {
                    if(f.actualFile.exists()) {
                        Files.copy(f.actualFile, errorSet.actualFile);
                        f.actualFile.delete();
                    }
                    if(f.backupFile.exists()) {
                        Files.copy(f.backupFile, errorSet.backupFile);
                        f.backupFile.delete();
                    }
                } catch (Exception e) {
                    AstralSorcery.log.info("Attempting to copy erroneous worlddata '" + key.identifier + "' to its error files failed.");
                    e.printStackTrace();
                }
            }
            data = key.getNewInstance();
        }
        AstralSorcery.log.info("Loading of '" + key.identifier + "' for world " + world.getDimension().getType().getId() + " finished.");
        return data;
    }

    private static CachedWorldData attemptLoad(SaveKey key, File f) throws IOException {
        CachedWorldData data = key.getNewInstance();
        NBTTagCompound cmp = CompressedStreamTools.read(f);
        data.readFromNBT(cmp);
        return data;
    }

    private static void saveDataToFile(World world, CachedWorldData data) throws IOException {
        SaveKey key = data.getSaveKey();
        DataFileSet f = getDataFile(world, key.identifier);
        if(!f.actualFile.getParentFile().exists()) {
            f.actualFile.getParentFile().mkdirs();
        }
        if(f.actualFile.exists()) {
            try {
                Files.copy(f.actualFile, f.backupFile);
            } catch (Exception exc) {
                AstralSorcery.log.info("Copying '" + key.identifier + "' 's actual file to its backup file failed!");
                exc.printStackTrace();
            }
        }
        if (!f.actualFile.exists()) {
            f.actualFile.createNewFile();
        }
        NBTTagCompound tag = new NBTTagCompound();
        data.writeToNBT(tag);
        CompressedStreamTools.write(tag, f.actualFile);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World world = (World) context[0];
        if(world.isRemote) return;
        int dimId = world.getDimension().getType().getId();
        Map<SaveKey, CachedWorldData> dataMap = cachedData.get(dimId);
        if(dataMap == null) return;

        for (SaveKey key : SaveKey.values()) {
            if(dataMap.containsKey(key)) {
                dataMap.get(key).updateTick(world);
            }
        }
    }

    public void doSave(World world) {
        int dimId = world.getDimension().getType().getId();
        Map<SaveKey, CachedWorldData> worldCache = cachedData.get(dimId);
        if(worldCache == null) return;
        for (SaveKey key : SaveKey.values()) {
            if(worldCache.containsKey(key)) {
                CachedWorldData data = worldCache.get(key);
                if(data.needsSaving()) {
                    try {
                        saveDataToFile(world, data);
                    } catch (IOException e) {
                        AstralSorcery.log.warn("Unable to save WorldData!");
                        AstralSorcery.log.warn("Affected data: Dim=" + dimId + " key=" + key.identifier);
                        AstralSorcery.log.warn("Printing StackTrace details...");
                        e.printStackTrace();
                    }
                    data.clearDirtyFlag();
                }
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.WORLD);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "WorldCacheManager";
    }

    public static enum SaveKey {

        LIGHT_NETWORK("lightnetwork", LightNetworkBuffer::new);
        //CHUNK_VERSIONING("chunkversions", ChunkVersionBuffer::new),
        //GATEWAY_DATA("gateway", GatewayCache::new),
        //STRUCTURE_GEN("structures", StructureGenBuffer::new),
        //STORAGE_BUFFER("storagenetwork", StructureGenBuffer::new),
        //STRUCTURE_MATCH("structurematcher", StructureMatchingBuffer::new);

        private final String identifier;
        private final DataProvider<CachedWorldData> instanceProvider;

        private SaveKey(String identifier, DataProvider<CachedWorldData> provider) {
            this.identifier = identifier;
            this.instanceProvider = provider;
        }

        public CachedWorldData getNewInstance() {
            return instanceProvider.provideDataInstance();
        }

        public String getIdentifier() {
            return identifier;
        }

        private static interface DataProvider<T extends CachedWorldData> {

            public T provideDataInstance();

        }

    }

    private static class DataFileSet {

        private final File actualFile;
        private final File backupFile;

        private DataFileSet(File actualFile) {
            this.actualFile = actualFile;
            this.backupFile = new File(actualFile.getParent(), actualFile.getName() + ".back");
        }

        private DataFileSet getErrorFileSet() {
            return new DataFileSet(new File(actualFile.getParent(), actualFile.getName() + ".error"));
        }

    }

}
