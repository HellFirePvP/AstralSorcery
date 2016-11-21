package hellfirepvp.astralsorcery.common.data.server;

import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import scala.actors.threadpool.Arrays;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ServerData
 * Created by HellFirePvP
 * Date: 21.11.2016 / 21:35
 */
public class ServerData {

    public static List<Integer> fileRequestedDimWhitelists = new ArrayList<>();

    public static File getServerDataFile() {
        File f = FMLCommonHandler.instance().getMinecraftServerInstance().getFile("AS_FixData.dat");
        if(!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException ignored) {}
        }
        return f;
    }

    public static void reloadData() {
        loadDataFromFile();

        updateData();
    }

    public static void addDimensionToHandle(int dimId) {
        if(!fileRequestedDimWhitelists.contains(dimId)) {
            fileRequestedDimWhitelists.add(dimId);
            updateData();
        }
    }

    public static boolean isDimensionWhitelisted(int dimId) {
        for (Integer i : fileRequestedDimWhitelists) {
            if(i == dimId) return true;
        }
        return false;
    }

    private static void updateData() {
        File dataFile = getServerDataFile();
        try {
            NBTTagCompound out = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            for (Integer dimId : fileRequestedDimWhitelists) {
                list.appendTag(new NBTTagInt(dimId));
            }
            CompressedStreamTools.write(out, dataFile);
        } catch (IOException e) {
            FMLLog.bigWarning("[AstralSorcery] Couldn't write ServerData File (AS_FixData.dat) - Expect issues.");
        } finally {
            pushData(fileRequestedDimWhitelists);
        }
    }

    private static void pushData(List<Integer> fileRequestedDimWhitelists) {
        DataWorldSkyHandlers skyHandlers = SyncDataHolder.getDataServer(SyncDataHolder.DATA_SKY_HANDLERS);
        skyHandlers.update(fileRequestedDimWhitelists);
    }

    private static void loadDataFromFile() {
        File dataFile = getServerDataFile();
        try {
            NBTTagCompound cmp = CompressedStreamTools.read(dataFile);
            NBTTagList dimIds = cmp.getTagList("dimWhitelist", 3);
            for (int i = 0; i < dimIds.tagCount(); i++) {
                fileRequestedDimWhitelists.add(dimIds.getIntAt(i));
            }
        } catch (IOException e) {
            FMLLog.bigWarning("[AstralSorcery] Couldn't read ServerData File (AS_FixData.dat) - Expect issues.");
            defaultToConfig();
        }
    }

    private static void defaultToConfig() {
        fileRequestedDimWhitelists = Arrays.asList(Config.constellationSkyDimWhitelist);
    }

}
