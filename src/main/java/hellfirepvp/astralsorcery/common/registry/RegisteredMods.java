/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import net.minecraftforge.fml.common.Loader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegisteredMods
 * Created by HellFirePvP
 * Date: 27.02.2017 / 03:30
 */
public enum  RegisteredMods {

    MINETWEAKER("MineTweaker3"),
    JEI("JEI");

    private final String modid;
    private final boolean isLoaded;

    private RegisteredMods(String modid) {
        this.modid = modid;
        this.isLoaded = Loader.isModLoaded(modid);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public String getModid() {
        return modid;
    }

}
