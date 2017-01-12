/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.world.data.ChunkVersionBuffer;
import hellfirepvp.astralsorcery.common.data.world.data.LightNetworkBuffer;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
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
        try {
            return (T) loadAndCache(world, key);
        } catch (IOException e) {
            AstralSorcery.log.warn("Unable to load WorldData!");
            AstralSorcery.log.warn("Affected data: Dim=" + world.provider.getDimension() + " key=" + key.identifier);
            throw new RuntimeException(e);
        }
    }

    private synchronized static File getDataFile(World world, String key) {
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
        File worldDir = new File(saveDir, "DIM_" + world.provider.getDimension());
        if(!worldDir.exists()) {
            worldDir.mkdirs();
        } else {
            ensureFolder(worldDir);
        }
        return new File(worldDir, key + ".dat");
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
        if(!cachedData.containsKey(world.provider.getDimension())) return null;
        Map<SaveKey, CachedWorldData> dataMap = cachedData.get(world.provider.getDimension());
        return dataMap.get(key);
    }

    private static CachedWorldData loadAndCache(World world, SaveKey key) throws IOException {
        CachedWorldData data = getFromCache(world, key);
        if(data != null) return data;

        int dimId = world.provider.getDimension();
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

    private static CachedWorldData loadDataFromFile(World world, SaveKey key) throws IOException {
        File f = getDataFile(world, key.identifier);
        if (!f.exists()) {
            return key.getNewInstance();
        }
        AstralSorcery.log.info("Load CachedWorldData '" + key.identifier + "' for world " + world.provider.getDimension());
        //AstralSorcery.log.info("[AstralSorcery] Any error that appears in connection to some load issue may be related to this world.");
        CachedWorldData data = key.getNewInstance();
        NBTTagCompound cmp = CompressedStreamTools.read(f);
        data.readFromNBT(cmp);
        AstralSorcery.log.info("Loading of '" + key.identifier + "' for world " + world.provider.getDimension() + " finished.");
        return data;
    }

    private static void saveDataToFile(World world, CachedWorldData data) throws IOException {
        File f = getDataFile(world, data.getSaveKey().identifier);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        NBTTagCompound tag = new NBTTagCompound();
        data.writeToNBT(tag);
        CompressedStreamTools.write(tag, f);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World world = (World) context[0];
        if(world.isRemote) return;
        int dimId = world.provider.getDimension();
        Map<SaveKey, CachedWorldData> dataMap = cachedData.get(dimId);
        if(dataMap == null) return;

        for (SaveKey key : SaveKey.values()) {
            if(dataMap.containsKey(key)) {
                dataMap.get(key).updateTick(world);
            }
        }
    }

    public void doSave(World world) {
        int dimId = world.provider.getDimension();
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

        ROCK_CRYSTAL("rcrystals", RockCrystalBuffer::new),
        LIGHT_NETWORK("lightnetwork", LightNetworkBuffer::new),
        CHUNK_VERSIONING("chunkversions", ChunkVersionBuffer::new);

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

}
