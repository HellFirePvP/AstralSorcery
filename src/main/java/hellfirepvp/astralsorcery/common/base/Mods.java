/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Mods
 * Created by HellFirePvP
 * Date: 31.10.2016 / 11:30
 */
public enum Mods {

    ;

    public final String modid;
    private final boolean loaded;

    //private static Class<?> gcPlayerClass, urPlayerClass;

    private Mods(String modName) {
        this.modid = modName;
        this.loaded = ModList.get().isLoaded(this.modid);
    }

    public boolean isPresent() {
        return loaded;
    }

    @Nullable
    public Class<?> getExtendedPlayerClass() {
        if(!isPresent()) return null;

        //switch (this) {
        //    case GALACTICRAFT_CORE:
        //        if(gcPlayerClass == null) {
        //            try {
        //                gcPlayerClass = Class.forName("micdoodle8.mods.galacticraft.core.entities.player.GCEntityPlayerMP");
        //            } catch (Exception ignored) {}
        //        }
        //        return gcPlayerClass;
        //    case UNIVERSALREMOTE:
        //        if(urPlayerClass == null) {
        //            try {
        //                urPlayerClass = Class.forName("clayborn.universalremote.hooks.entity.HookedEntityPlayerMP");
        //            } catch (Exception ignored) {}
        //        }
        //        return urPlayerClass;
        //    default:
        //        break;
        //}
        return null;
    }

}
