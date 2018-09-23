/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.data;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PersistentDataManager
 * Created by HellFirePvP
 * Date: 22.09.2018 / 14:14
 */
@SideOnly(Side.CLIENT)
public class PersistentDataManager {

    public static final PersistentDataManager INSTANCE = new PersistentDataManager();

    private static final String[] infoFileContents  = new String[] {
            "Astral Sorcery's persistent data directory",
            "Astral Sorcery stores various informations here to ensure they are persistent across several playthroughs",
            "of different modpacks. Thus, no matter what pack you play, these files end up here.",
            "",
            "Files saved here are saved in an NBT format and can't be opened directly on many systems.",
            "See 'NBTExplorer' or related software to open and modify the files if needed.",
            "",
            "Should you wish to disable some or all features accessing the permanent storage, check",
            "Astral Sorcery's configuration file and disable all or individual features."
    };

    private File selectedPersistentDataFolder, localModPackFolder;
    private boolean useLocal = false;
    private boolean initialized = false;

    private boolean active = true;
    private Map<PersistentKey, Boolean> activeMap = Maps.newHashMap();
    private Map<PersistentKey, CachedPersistentData> cachedPersistentData = Maps.newHashMap();

    //Trying to be in line with PSI's naming and location of folders
    private static final String dirWindows = "\\AppData\\Roaming\\.minecraft\\astralsorcery_persistent";
    private static final String dirMac = "/Library/Application Support/minecraft/astralsorcery_persistent";
    private static final String dirOther = "/.minecraft/astralsorcery_persistent";

    private PersistentDataManager() {}

    public void init(File localDataDirectory) {
        if (initialized) {
            return;
        }
        initialized = true;

        File dataDir;
        if (active) {
            dataDir = resolvePersistentDataFolder();
            if (dataDir == null) {
                dataDir = localDataDirectory;
                useLocal = true;
            } else {
                useLocal = false;
            }
        } else {
            dataDir = localDataDirectory;
            useLocal = true;
        }

        if (!dataDir.exists()) {
            if(!dataDir.mkdirs()) {
                AstralSorcery.log.info("Unable to create folder for persistent data. Are you sure the mod has the permissions to create files there?");
                return;
            }
        }
        if (dataDir != localDataDirectory && !localDataDirectory.exists()) {
            if(!localDataDirectory.mkdirs()) {
                AstralSorcery.log.info("Unable to create folder for persistent data. Are you sure the mod has the permissions to create files there?");
                return;
            }
        }
        this.selectedPersistentDataFolder = dataDir;
        this.localModPackFolder = localDataDirectory;

        checkInfoFile();

        for (PersistentKey key : PersistentKey.values()) {
            if (loadPersistentData(key)) {
                savePersistentData(getData(key));
            }
        }
    }

    @Nonnull
    public <T extends CachedPersistentData> T getData(PersistentKey key) {
        return (T) this.cachedPersistentData.getOrDefault(key, key.provideEmptyInstance());
    }

    private File resolvePersistentDataFolder() {
        String userHome = System.getProperty("user.home");
        String userOS = System.getProperty("os.name");
        if (userOS == null) {
            AstralSorcery.log.info("Unable to determine User OS. Unable to select appropriate directory. Using local/pack-dependent directory!");
            return null;
        }
        userOS = userOS.toLowerCase();

        String dir;
        if (userOS.startsWith("windows")) {
            dir = dirWindows;
        } else if (userOS.startsWith("mac")) {
            dir = dirMac;
        } else {
            dir = dirOther;
        }

        String folderPath = userHome + dir;
        return new File(folderPath);
    }

    boolean savePersistentData(CachedPersistentData data) {
        PersistentKey key = data.getKey();
        File dataFile = getKeyFile(key);
        if (dataFile == null) {
            return false;
        }
        NBTTagCompound tag = new NBTTagCompound();
        data.writeToNBT(tag);
        try {
            CompressedStreamTools.write(tag, dataFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean loadPersistentData(PersistentKey key) {
        File dataFile = getKeyFile(key);
        if (dataFile == null) {
            return false;
        }
        CachedPersistentData data;
        try {
            NBTTagCompound tag = CompressedStreamTools.read(dataFile);
            data = key.provideEmptyInstance();
            data.readFromNBT(tag);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        boolean mergedData = false;
        if (!useLocal || !this.activeMap.getOrDefault(key, false)) {
            try {
                File localFile = getLocalModPackKeyFile(key);
                if (localFile.exists()) {
                    NBTTagCompound prevTag = CompressedStreamTools.read(localFile);
                    CachedPersistentData prevData = key.provideEmptyInstance();
                    prevData.readFromNBT(prevTag);

                    mergedData = data.mergeFrom(prevData);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
        this.cachedPersistentData.put(key, data);
        return mergedData;
    }

    private File getKeyFile(PersistentKey key) {
        return resolveFile(key.getFileName(), this.activeMap.getOrDefault(key, false) ? this.selectedPersistentDataFolder : this.localModPackFolder, true);
    }

    private File getLocalModPackKeyFile(PersistentKey key) {
        return resolveFile(key.getFileName(), this.localModPackFolder, false);
    }

    private File resolveFile(String name, File folder, boolean createFile) {
        File dataFile = new File(folder, name);
        if (createFile && !dataFile.exists()) {
            try {
                if (!dataFile.createNewFile()) {
                    AstralSorcery.log.info("Unable to create file for persistent data. Are you sure the mod has the permissions to create files?");
                }
            } catch (IOException exc) {
                exc.printStackTrace();
                return null;
            }
        }
        return dataFile;
    }

    private void checkInfoFile() {
        if (useLocal) return; //No need to do this when saving pack-dependent anyway.

        File infoFile = new File(this.selectedPersistentDataFolder, "info.txt");
        if (!infoFile.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(infoFile))) {
                if (infoFile.createNewFile()) {
                    for (String line : infoFileContents) {
                        bw.write(line);
                        bw.newLine();
                    }
                    bw.flush();
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }

    public static class ConfigPersistency extends ConfigEntry {

        public ConfigPersistency() {
            super(Section.DATA_PERSISTENCE, "files");
        }

        @Override
        public final void loadFromConfig(Configuration cfg) {
            PersistentDataManager mgr = INSTANCE;

            mgr.active = cfg.getBoolean("active", getConfigurationSection(), true, "Defines if the persistent client-data usage/storage should be used. If set to false, it'll store the data in a pack-related directory (subfolder 'astralsorcery' in the same directory as /mods, /config, ...)");

            for (PersistentKey key : PersistentKey.values()) {
                mgr.activeMap.put(key, cfg.getBoolean("active_" + key.getName(), getConfigurationSection(), true, "Defines if the persistent data loading & saving should be enabled for " + key.getName() + " (saved in file " + key.getFileName() + ")"));
            }
        }
    }

    public static enum PersistentKey {

        KNOWLEDGE_FRAGMENTS("knowledge_fragments.dat", (v) -> new KnowledgeFragmentData());

        private final String fileName;
        private final Function<Void, CachedPersistentData> dataProvider;

        PersistentKey(String fileName, Function<Void, CachedPersistentData> dataProvider) {
            this.fileName = fileName;
            this.dataProvider = dataProvider;
        }

        public CachedPersistentData provideEmptyInstance() {
            return this.dataProvider.apply(null);
        }

        public String getName() {
            return name().toLowerCase();
        }

        public String getFileName() {
            return fileName;
        }
    }

}
