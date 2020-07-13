/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.ServerLifecycleListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchIOThread
 * Created by HellFirePvP
 * Date: 15.01.2019 / 17:42
 */
public class ResearchIOThread extends TimerTask implements ServerLifecycleListener {

    private static ResearchIOThread saveTask;
    private static Timer ioThread;

    private Map<UUID, PlayerProgress> playerSaveQueue = Maps.newHashMap();
    private Map<UUID, PlayerProgress> awaitingSaveQueue = Maps.newHashMap();
    private boolean inSave = false, skipTick = false;

    private ResearchIOThread() {}

    public static ResearchIOThread startup() {
        if (saveTask == null) {
            saveTask = new ResearchIOThread();
        }
        return getTask();
    }

    public static ResearchIOThread getTask() {
        return saveTask;
    }

    @Override
    public void onServerStart() {
        if (ioThread != null) {
            return;
        }
        if (saveTask == null) {
            saveTask = new ResearchIOThread();
        }
        ioThread = new Timer("ResearchIOThread", true);
        ioThread.scheduleAtFixedRate(saveTask, 30_000, 30_000);
    }

    @Override
    public void onServerStop() {
        this.flushAndSaveAll();

        saveTask.cancel();
        saveTask = null;
        ioThread.cancel();
        ioThread = null;
    }

    @Override
    public void run() {
        if (skipTick) {
            return;
        }

        inSave = true;
        for (Map.Entry<UUID, PlayerProgress> entry : playerSaveQueue.entrySet()) {
            saveNow(entry.getKey(), entry.getValue());
        }
        playerSaveQueue.clear();
        inSave = false;

        playerSaveQueue.putAll(awaitingSaveQueue);
        awaitingSaveQueue.clear();
    }

    private void scheduleSave(UUID playerUUID, PlayerProgress copiedProgress) {
        if (inSave) {
            awaitingSaveQueue.put(playerUUID, copiedProgress);
        } else {
            playerSaveQueue.put(playerUUID, copiedProgress);
        }
    }

    private void cancelScheduledSave(UUID playerUUID) {
        awaitingSaveQueue.remove(playerUUID);
        playerSaveQueue.remove(playerUUID);
    }

    private void flushAndSaveAll() {
        skipTick = true;
        playerSaveQueue.putAll(awaitingSaveQueue);
        for (Map.Entry<UUID, PlayerProgress> entry : playerSaveQueue.entrySet()) {
            saveNow(entry.getKey(), entry.getValue());
        }
        playerSaveQueue.clear();
        awaitingSaveQueue.clear();
        skipTick = false;
        inSave = false;
    }

    public static void saveProgress(UUID playerUUID, PlayerProgress copiedProgress) {
        if (saveTask != null) {
            saveTask.scheduleSave(playerUUID, copiedProgress);
        }
    }

    public static void cancelSave(UUID playerUUID) {
        if (saveTask != null) {
            saveTask.cancelScheduledSave(playerUUID);
        }
    }

    static void saveNow(UUID playerUUID, PlayerProgress progress) {
        File playerFile = ResearchHelper.getPlayerFile(playerUUID);
        try {
            Files.copy(playerFile, ResearchHelper.getPlayerBackupFile(playerUUID));
        } catch (IOException exc) {
            AstralSorcery.log.warn("Failed copying progress file contents to backup file!");
            exc.printStackTrace();
        }
        try {
            CompoundNBT cmp = new CompoundNBT();
            progress.store(cmp);
            CompressedStreamTools.write(cmp, playerFile);
        } catch (IOException ignored) {}
    }
}
