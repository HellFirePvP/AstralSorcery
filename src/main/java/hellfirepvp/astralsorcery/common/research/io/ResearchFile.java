/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.io;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchFile
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchFile {

    public static boolean doesPlayerFileExist(Player player) {
        return getPlayerFile(player).exists();
    }

    public static File getPlayerFile(Player player) {
        return getPlayerFile(player.getUUID());
    }

    public static File getPlayerFile(UUID playerUuid) {
        return getFile(playerUuid, null);
    }

    public static File getPlayerBackupFile(Player player) {
        return getPlayerBackupFile(player.getUUID());
    }

    public static File getPlayerBackupFile(UUID playerUuid) {
        return getFile(playerUuid, "_back");
    }

    private static File getFile(UUID playerUuid, @Nullable String suffix) {
        return new File(getPlayerDirectory(), playerUuid.toString() + ".astral" + (suffix == null ? "" : suffix));
    }

    private static File getPlayerDirectory() {
        File pDir = new File(AstralSorcery.getInstance().getProxy().getServerDataDirectory(), "playerdata");
        if (!pDir.exists()) {
            pDir.mkdirs();
        }
        return pDir;
    }
}
