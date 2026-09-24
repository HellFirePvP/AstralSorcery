/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.io;

import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.util.listener.ServerLifecycleListener;
import net.minecraft.server.MinecraftServer;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchIOThread
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchIOThread implements ServerLifecycleListener {

    private static final ResearchIOThread INSTANCE = new ResearchIOThread();

    private static Timer timer;
    private static TimerTask saveTask;

    private final Map<UUID, PlayerProgress> playerSaveQueue = new HashMap<>();
    private final Map<UUID, PlayerProgress> awaitingSaveQueue = new HashMap<>();
    private boolean inSave = false, skipTick = false;

    private ResearchIOThread() {}

    public static ResearchIOThread getInstance() {
        return INSTANCE;
    }

    @Override
    public void onServerStart(MinecraftServer server) {
        this.stop();

        saveTask = new TimerTask() {
            @Override
            public void run() {
                getInstance().doSaveTick();
            }
        };
        timer = new Timer("ResearchIOThread", true);
        timer.scheduleAtFixedRate(saveTask, 30_000, 30_000);
    }

    @Override
    public void onServerStop(MinecraftServer server) {
        this.saveAndFlushAll();

        this.stop();
    }

    private void stop() {
        if (saveTask != null) {
            saveTask.cancel();
            saveTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void doSaveTick() {
        if (skipTick) {
            return;
        }

        inSave = true;
        for (Map.Entry<UUID, PlayerProgress> entry : playerSaveQueue.entrySet()) {
            ResearchWriter.saveToFile(entry.getKey(), entry.getValue());
        }
        playerSaveQueue.clear();
        inSave = false;

        playerSaveQueue.putAll(awaitingSaveQueue);
        awaitingSaveQueue.clear();
    }

    private void saveAndFlushAll() {
        skipTick = true;
        playerSaveQueue.putAll(awaitingSaveQueue);
        for (Map.Entry<UUID, PlayerProgress> entry : playerSaveQueue.entrySet()) {
            ResearchWriter.saveToFile(entry.getKey(), entry.getValue());
        }
        playerSaveQueue.clear();
        awaitingSaveQueue.clear();
        skipTick = false;
        inSave = false;
    }

    public static void scheduleSave(UUID playerUUID, PlayerProgress progress) {
        ResearchIOThread thread = getInstance();
        if (thread.inSave) {
            thread.awaitingSaveQueue.put(playerUUID, progress);
        } else {
            thread.playerSaveQueue.put(playerUUID, progress);
        }
    }

    public static void cancelSave(UUID playerUUID) {
        ResearchIOThread thread = getInstance();
        thread.awaitingSaveQueue.remove(playerUUID);
        thread.playerSaveQueue.remove(playerUUID);
    }
}
