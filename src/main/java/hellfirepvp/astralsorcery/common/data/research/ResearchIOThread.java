/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
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
public class ResearchIOThread implements ServerLifecycleListener {

    private static final ResearchIOThread instance = new ResearchIOThread();

    private static Timer timer;
    private static TimerTask saveTask;

    private final Map<UUID, PlayerProgress> playerSaveQueue = Maps.newHashMap();
    private final Map<UUID, PlayerProgress> awaitingSaveQueue = Maps.newHashMap();
    private boolean inSave = false, skipTick = false;

    private ResearchIOThread() {}

    public static ResearchIOThread getInstance() {
        return instance;
    }

    @Override
    public void onServerStart() {
        reset();

        saveTask = new TimerTask() {
            @Override
            public void run() {
                instance.doSave();
            }
        };
        timer = new Timer("ResearchIOThread", true);
        timer.scheduleAtFixedRate(saveTask, 30_000, 30_000);
    }

    @Override
    public void onServerStop() {
        this.flushAndSaveAll();

        reset();
    }

    private void reset() {
        if (saveTask != null) {
            saveTask.cancel();
            saveTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void doSave() {
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
        if (instance != null) {
            instance.scheduleSave(playerUUID, copiedProgress);
        }
    }

    public static void cancelSave(UUID playerUUID) {
        if (instance != null) {
            instance.cancelScheduledSave(playerUUID);
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
