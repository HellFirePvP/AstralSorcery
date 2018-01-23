/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.AstralSorcery;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FileStorageUtil
 * Created by HellFirePvP
 * Date: 23.01.2018 / 23:09
 */
public class FileStorageUtil {

    private FileStorageUtil() {}

    @Nonnull
    public static File getAstralSorceryDirectory() {
        File f = new File(System.getProperty("user.dir"), AstralSorcery.MODID);
        if(!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    @Nonnull
    public static File getGeneralSubDirectory(String directoryName) {
        File f = new File(FileStorageUtil.getAstralSorceryDirectory(), directoryName);
        if(!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

}
