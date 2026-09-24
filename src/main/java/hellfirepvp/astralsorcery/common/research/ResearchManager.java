/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import hellfirepvp.astralsorcery.client.helper.RenderPerkExperienceOverlay;
import hellfirepvp.astralsorcery.client.screen.tome.TomeResearchScreen;
import hellfirepvp.astralsorcery.common.research.io.ResearchIOThread;
import hellfirepvp.astralsorcery.common.research.io.ResearchWriter;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchManager {

    private static PlayerProgress clientProgress = PlayerProgressTestAccess.get();

    private static final Map<UUID, PlayerProgress> serverProgress = new HashMap<>();

    @Nonnull
    public static PlayerProgress getProgress(@Nullable Player player, LogicalSide side) {
        if (side.isClient()) {
            return getClientProgress();
        } else if (player instanceof ServerPlayer) {
            return getProgressServer((ServerPlayer) player);
        } else {
            return PlayerProgressTestAccess.get();
        }
    }

    @Nonnull
    public static PlayerProgress getClientProgress() {
        return clientProgress;
    }

    @Nonnull
    private static PlayerProgress getProgressServer(ServerPlayer player) {
        if (MiscUtil.isPlayerFake(player)) {
            return PlayerProgressTestAccess.get();
        }
        return getProgress(player.getUUID());
    }

    @Nonnull
    private static PlayerProgress getProgress(UUID uuid) {
        PlayerProgress progress = serverProgress.get(uuid);
        if (progress == null) {
            progress = ResearchWriter.loadFromFile(uuid);
            serverProgress.put(uuid, progress);
        }
        return progress;
    }

    @OnlyIn(Dist.CLIENT)
    public static void setClientProgress(@Nonnull PlayerProgress progress) {
        setClientProgress(progress, Minecraft.getInstance().player);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setClientProgress(@Nonnull PlayerProgress progress, @Nullable Player player) {
        int prevClientLevel = ResearchManager.getClientProgress().getPerkData().getPerkLevel(player, LogicalSide.CLIENT);
        clientProgress = progress;
        TomeResearchScreen.resetOpenTome();
        int newLevel = progress.getPerkData().getPerkLevel(player, LogicalSide.CLIENT);
        if (newLevel > prevClientLevel) {
            RenderPerkExperienceOverlay.RENDERER.revealExperienceBar(160);
        }
    }

    static void removeProgress(UUID playerUuid) {
        serverProgress.remove(playerUuid);
    }

    public static void clearServerCache() {
        serverProgress.clear();
    }

    public static void scheduleSave(Player player, boolean force) {
        if (player instanceof ServerPlayer sPlayer && !MiscUtil.isPlayerFake(sPlayer)) {
            scheduleSave(sPlayer.getUUID(), force);
        }
    }

    public static void scheduleSave(UUID playerUuid, boolean force) {
        PlayerProgress progress = getProgress(playerUuid);
        if (force) {
            ResearchWriter.saveToFile(playerUuid, progress);
        } else {
            ResearchIOThread.scheduleSave(playerUuid, progress);
        }
    }
}
