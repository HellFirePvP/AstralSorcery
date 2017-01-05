/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

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

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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

    @Nullable
    public static File getServerDataFile() {
        File worldDir;
        try {
            worldDir = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getSaveHandler().getWorldDirectory();
        } catch (Exception exc) {
            return null;
        }
        File f = new File(worldDir, "AstralSorcery_ServerData.dat");
        if(!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                CompressedStreamTools.write(new NBTTagCompound(), f);
            } catch (IOException ignored) {}
        }
        return f;
    }

    public static void reloadData() {
        loadDataFromFile();

        updateData();
    }

    public static void writeData() {
        updateData();
        fileRequestedDimWhitelists.clear();
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
        if(dataFile == null) {
            FMLLog.bigWarning("[AstralSorcery] Couldn't find folder for AstralSorcery_ServerData.dat - Are you calling this too early or too late in the execution?");
            return;
        }
        try {
            dataFile.delete();
            dataFile.createNewFile();

            NBTTagCompound out = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            List<Integer> collect = new LinkedList<>();
            collect.addAll(fileRequestedDimWhitelists);
            for (Integer configDim : Config.constellationSkyDimWhitelist) {
                if(!collect.contains(configDim)) {
                    collect.add(configDim);
                }
            }
            for (Integer dimId : collect) {
                list.appendTag(new NBTTagInt(dimId));
            }
            out.setTag("dimWhitelist", list);
            CompressedStreamTools.write(out, dataFile);
        } catch (IOException e) {
            FMLLog.bigWarning("[AstralSorcery] Couldn't write ServerData File (AstralSorcery_ServerData.dat) - Expect issues.");
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
        if(dataFile == null) {
            FMLLog.bigWarning("[AstralSorcery] Couldn't find folder for AstralSorcery_ServerData.dat - Are you calling this too early or too late in the execution?");
            return;
        }
        try {
            NBTTagCompound cmp = CompressedStreamTools.read(dataFile);
            NBTTagList dimIds = cmp.getTagList("dimWhitelist", 3);
            for (int i = 0; i < dimIds.tagCount(); i++) {
                fileRequestedDimWhitelists.add(dimIds.getIntAt(i));
            }
            for (Integer configDim : Config.constellationSkyDimWhitelist) {
                if(!fileRequestedDimWhitelists.contains(configDim)) {
                    fileRequestedDimWhitelists.add(configDim);
                }
            }
        } catch (IOException e) {
            FMLLog.bigWarning("[AstralSorcery] Couldn't read ServerData File (AstralSorcery_ServerData.dat) - Expect issues.");
            defaultToConfig();
        }
    }

    private static void defaultToConfig() {
        fileRequestedDimWhitelists = Arrays.asList(Config.constellationSkyDimWhitelist);
    }

}
