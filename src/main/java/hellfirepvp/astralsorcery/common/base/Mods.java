/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Mods
 * Created by HellFirePvP
 * Date: 31.10.2016 / 11:30
 */
public enum Mods {

    TICONSTRUCT("tconstruct"),
    MINETWEAKER("MineTweaker3"),
    JEI("JEI"),
    BOTANIA("Botania"),
    GALACTICRAFT_CORE("galacticraftcore");

    public final String modId;

    private static Class<?> gcPlayerClass;

    private Mods(String modName) {
        this.modId = modName;
    }

    public boolean isPresent() {
        return Loader.isModLoaded(modId);
    }

    @Nullable
    public static Class<?> getGCPlayerClass() {
        if(GALACTICRAFT_CORE.isPresent()) {
            if(gcPlayerClass == null) {
                try {
                    gcPlayerClass = Class.forName("micdoodle8.mods.galacticraft.core.entities.player.GCEntityPlayerMP");
                } catch (Exception ignored) {}
            }
            return gcPlayerClass;
        }
        return null;
    }

}
