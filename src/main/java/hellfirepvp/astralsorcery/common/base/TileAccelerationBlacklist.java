/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.tile.base.TileSourceBase;
import hellfirepvp.astralsorcery.common.tile.base.TileTransmissionBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAccelerationBlacklist
 * Created by HellFirePvP
 * Date: 01.11.2016 / 02:32
 */
public class TileAccelerationBlacklist {

    private static List<String> blacklistedPrefixes = new LinkedList<>();
    private static List<Class<?>> erroredTiles = new LinkedList<>();
    private static List<Class<?>> blacklistedClasses = new LinkedList<>();

    public static boolean canAccelerate(@Nullable TileEntity te) {
        if(te == null) return false; //Nothing there.
        if(!(te instanceof ITickable)) return false; //Uh nope.

        Class<?> tClass = te.getClass();
        String className = tClass.getName();
        String lCClassName = className.toLowerCase();
        if (lCClassName.startsWith("[L")) {
            lCClassName = lCClassName.substring(2); //Cull descriptor
        }
        for (String pref : blacklistedPrefixes) {
            if(lCClassName.startsWith(pref)) {
                return false;
            }
        }
        for (Class<?> tCl : blacklistedClasses) {
            if(tCl.isAssignableFrom(tClass)) {
                return false;
            }
        }
        return !erroredTiles.contains(tClass);
    }

    //Specifically used if the tile crashed once when trying to accel it.
    @Deprecated
    public static void errored(Class<?> teClass) {
        if(!erroredTiles.contains(teClass)) {
            erroredTiles.add(teClass);
        }
    }

    public static void blacklistTileClassAndSubclasses(Class<?> tileClass) {
        if(!blacklistedClasses.contains(tileClass))
            blacklistedClasses.add(tileClass);
    }

    public static void blacklistTileClassNamePrefix(String prefix) {
        if(!blacklistedPrefixes.contains(prefix.toLowerCase()))
            blacklistedPrefixes.add(prefix.toLowerCase());
    }

    public static void init() {
        blacklistTileClassAndSubclasses(TileTransmissionBase.class);
        blacklistTileClassAndSubclasses(TileSourceBase.class);
        blacklistTileClassAndSubclasses(TileRitualPedestal.class);
        blacklistTileClassAndSubclasses(TileAltar.class);
        blacklistTileClassAndSubclasses(TileAttunementAltar.class);
        blacklistTileClassAndSubclasses(TileCelestialCrystals.class);
        blacklistTileClassAndSubclasses(TileFakeTree.class);
    }

    public static class TileAccelBlacklistEntry extends ConfigEntry {

        private static String[] DEFAULT_ENTRIES = new String[] {
                "appeng", //I don't wanna run into issues that come from accelerating any network stuffs.
                "raoulvdberge.refinedstorage" //Same as AE stuff
        };

        public TileAccelBlacklistEntry() {
            super(Section.MACHINERY, "tileacceleration_blacklist");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            String[] blacklist = cfg.getStringList("ClassesOrSuperPackages", getConfigurationSection(), DEFAULT_ENTRIES,
                    "The classes for tileentities to be blacklisted from AstralSorcery's tile acceleration mechanics. Fully define a class or a package above it. Separated by '/'");

            for (String entry : blacklist) {
                blacklistTileClassNamePrefix(entry);
            }
        }
    }

}
