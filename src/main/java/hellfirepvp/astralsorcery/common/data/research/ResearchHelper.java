/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.io.Files;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 18:00
 */
public class ResearchHelper {

    public static PlayerProgress clientProgress = new PlayerProgress();
    public static boolean clientInitialized = false;

    private static Map<UUID, PlayerProgress> playerProgressServer = new HashMap<>();

    @Nonnull
    public static PlayerProgress getProgress(EntityPlayer player, Dist dist) {
        if (dist.isClient()) {
            return clientProgress;
        } else if (player instanceof EntityPlayerMP) {
            return getProgressServer((EntityPlayerMP) player);
        } else {
            return new PlayerProgressTestAccess();
        }
    }

    @Nonnull
    private static PlayerProgress getProgressServer(EntityPlayerMP player) {
        if(MiscUtils.isPlayerFakeMP(player)) {
            return new PlayerProgressTestAccess();
        }
        return getProgress(player.getUniqueID());
    }

    @Nonnull
    private static PlayerProgress getProgress(UUID uuid) {
        PlayerProgress progress = playerProgressServer.get(uuid);
        if (progress == null) {
            loadPlayerKnowledge(uuid);
            progress = playerProgressServer.get(uuid);
        }
        if (progress == null) {
            progress = new PlayerProgress(); //WELL we already try recovering.. so wtf.
        }
        return progress;
    }

    public static void loadPlayerKnowledge(EntityPlayerMP p) {
        if(!MiscUtils.isPlayerFakeMP(p)) {
            loadPlayerKnowledge(p.getUniqueID());
        }
    }

    private static void loadPlayerKnowledge(UUID pUUID) {
        File playerFile = getPlayerFile(pUUID);
        try {
            load_unsafe(pUUID, playerFile);
        } catch (Exception e) {
            AstralSorcery.log.warn("Unable to load progress from default progress file. Attempting loading backup.");
            AstralSorcery.log.warn("Erroneous file: " + playerFile.getName());
            e.printStackTrace();

            playerFile = getPlayerBackupFile(pUUID);
            try {
                load_unsafe(pUUID, playerFile);
                Files.copy(playerFile, getPlayerFile(pUUID)); //Copying back.
            } catch (Exception e1) {
                AstralSorcery.log.warn("Unable to load progress from backup progress file. Copying relevant files to error files.");
                AstralSorcery.log.warn("Erroneous file: " + playerFile.getName());
                e1.printStackTrace();

                File plOriginal = getPlayerFile(pUUID);
                File plBackup = getPlayerBackupFile(pUUID);
                try {
                    Files.copy(plOriginal, new File(plOriginal.getParent(), plOriginal.getName() + ".lerror"));
                    Files.copy(plBackup,   new File(plBackup.getParent(),     plBackup.getName() + ".lerror"));
                    AstralSorcery.log.warn("Copied progression files to error files. In case you would like to try me (HellFirePvP) to maybe see what i can do about maybe recovering the files,");
                    AstralSorcery.log.warn("send them over to me at the issue tracker https://github.com/HellFirePvP/AstralSorcery/issues - 90% that i won't be able to do anything, but reporting it would still be great.");
                } catch (IOException e2) {
                    AstralSorcery.log.warn("Unable to copy files to error-files.");
                    AstralSorcery.log.warn("I've had enough. I can't even access or open the files apparently. I'm giving up.");
                    e2.printStackTrace();
                }
                plOriginal.delete();
                plBackup.delete();

                informPlayersAboutProgressionLoss(pUUID);

                load_unsafeFromNBT(pUUID, null);
                savePlayerKnowledge(pUUID, true);
            }
        }
    }

    private static void load_unsafe(UUID pUUID, File playerFile) throws Exception {
        NBTTagCompound compound = CompressedStreamTools.read(playerFile); //IO-Exc thrown only here.
        load_unsafeFromNBT(pUUID, compound);
    }

    private static void load_unsafeFromNBT(UUID pUUID, @Nullable NBTTagCompound compound) {
        PlayerProgress progress = new PlayerProgress();
        if (compound != null && !compound.isEmpty()) {
            progress.load(compound);
        }
        progress.forceGainResearch(ResearchProgression.DISCOVERY);

        playerProgressServer.put(pUUID, progress);
    }

    private static void informPlayersAboutProgressionLoss(UUID pUUID) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if(server != null) {
            EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(pUUID);
            if(player != null) {
                player.sendMessage(new TextComponentString("AstralSorcery: Your progression could not be loaded and can't be recovered from backup. Please contact an administrator to lookup what went wrong and/or potentially recover your data from a backup.").setStyle(new Style().setColor(TextFormatting.RED)));
            }
            String resolvedName = player != null ? player.getGameProfile().getName() : pUUID.toString() + " (Not online)";
            for (String opName : server.getPlayerList().getOppedPlayerNames()) {
                EntityPlayer pl = server.getPlayerList().getPlayerByUsername(opName);
                if(pl != null) {
                    pl.sendMessage(new TextComponentString("AstralSorcery: The progression of " + resolvedName + " could not be loaded and can't be recovered from backup. Error files might be created from the unloadable progression files, check the console for additional information!").setStyle(new Style().setColor(TextFormatting.RED)));
                }
            }
        }
    }

    public static boolean mergeApplyPlayerprogress(PlayerProgress toMergeFrom, EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.acceptMergeFrom(toMergeFrom);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static void wipeKnowledge(EntityPlayerMP p) {
        resetPerks(p);
        wipeFile(p);
        playerProgressServer.remove(p.getUniqueID());
        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, p);
        PktSyncKnowledge pk = new PktSyncKnowledge(PktSyncKnowledge.STATE_WIPE);
        PacketChannel.CHANNEL.sendTo(pk, p);
        loadPlayerKnowledge(p);
        ResearchSyncHelper.pushProgressToClientUnsafe(p);
    }

    private static void wipeFile(EntityPlayerMP player) {
        getPlayerFile(player).delete();
        ResearchIOThread.cancelSave(player.getUniqueID());
    }

    public static void savePlayerKnowledge(EntityPlayerMP p) {
        if(!MiscUtils.isPlayerFakeMP(p)) {
            savePlayerKnowledge(p.getUniqueID(), false);
        }
    }

    private static void savePlayerKnowledge(UUID pUUID, boolean force) {
        if (playerProgressServer.get(pUUID) == null) return;
        PlayerProgress progress = playerProgressServer.get(pUUID);
        if (force) {
            ResearchIOThread.saveNow(pUUID, progress);
        } else {
            ResearchIOThread.saveProgress(pUUID, progress.copy());
        }
    }

    public static void saveAndClearServerCache() {
        ResearchIOThread.saveAllPending();
        playerProgressServer.clear();
    }

    public static File getPlayerFile(EntityPlayer player) {
        return getPlayerFile(player.getUniqueID());
    }

    public static File getPlayerFile(UUID pUUID) {
        File f = new File(getPlayerDirectory(), pUUID.toString() + ".astral");
        if(!f.exists()) {
            try {
                CompressedStreamTools.write(new NBTTagCompound(), f);
            } catch (IOException ignored) {} //Will be created later anyway... just as fail-safe.
        }
        return f;
    }

    public static boolean doesPlayerFileExist(EntityPlayer player) {
        return new File(getPlayerDirectory(), player.getUniqueID().toString() + ".astral").exists();
    }

    public static File getPlayerBackupFile(EntityPlayer player) {
        return getPlayerBackupFile(player.getUniqueID());
    }

    public static File getPlayerBackupFile(UUID pUUID) {
        File f = new File(getPlayerDirectory(), pUUID.toString() + ".astralback");
        if(!f.exists()) {
            try {
                CompressedStreamTools.write(new NBTTagCompound(), f);
            } catch (IOException ignored) {} //Will be created later anyway... just as fail-safe.
        }
        return f;
    }

    private static File getPlayerDirectory() {
        File pDir = new File(AstralSorcery.getProxy().getASServerDataDirectory(), "playerdata");
        if (!pDir.exists()) {
            pDir.mkdirs();
        }
        return pDir;
    }

}
