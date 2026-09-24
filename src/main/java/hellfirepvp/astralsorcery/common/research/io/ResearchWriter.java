/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.io;

import com.google.common.io.Files;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchWriter
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchWriter {

    @Nonnull
    public static PlayerProgress loadFromFile(UUID playerUUID) {
        File playerFile = ResearchFile.getPlayerFile(playerUUID);
        if (!playerFile.exists()) {
            return PlayerProgress.blankProgress();
        }
        AstralSorcery.LOG.info("Loading player progress for {}.", playerUUID);
        try {
            return attemptLoad(playerFile);
        } catch (IOException exc) {
            AstralSorcery.LOG.error("Failed loading player progress from file!", exc);
        }

        File backupFile = ResearchFile.getPlayerBackupFile(playerUUID);
        if (!backupFile.exists()) {
            return PlayerProgress.blankProgress();
        }
        try {
            PlayerProgress progress = attemptLoad(backupFile);
            Files.copy(backupFile, playerFile);
            return progress;
        } catch (IOException exc) {
            AstralSorcery.LOG.error("Failed loading player progress from backup file!", exc);
        }

        //TODO inform about progress wipe

        wipeFiles(playerUUID);
        return PlayerProgress.blankProgress();
    }

    @Nonnull
    private static PlayerProgress attemptLoad(File file) throws IOException {
        CompoundTag tag = NbtIo.read(file.toPath());
        return PlayerProgress.SAVE_CODEC.parse(NbtOps.INSTANCE, tag)
                .getOrThrow(str -> new IOException("Failed loading player progress from file! " + str));
    }

    public static void saveToFile(UUID playerUUID, PlayerProgress progress) {
        File playerFile = ResearchFile.getPlayerFile(playerUUID);
        try {
            if (playerFile.exists()) {
                Files.copy(playerFile, ResearchFile.getPlayerBackupFile(playerUUID));
            }
        } catch (IOException exc) {
            AstralSorcery.LOG.error("Failed copying progress file for %s to backup file!".formatted(playerUUID), exc);
        }

        PlayerProgress.SAVE_CODEC.encode(progress, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(tag -> {
            try {
                NbtIo.write((CompoundTag) tag, playerFile.toPath());
            } catch (IOException ignored) {}
        });
    }

    public static void wipeFiles(UUID playerUuid) {
        ResearchFile.getPlayerFile(playerUuid).delete();
        ResearchFile.getPlayerBackupFile(playerUuid).delete();
        ResearchIOThread.cancelSave(playerUuid);
    }
}
