/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import net.minecraftforge.fml.common.Loader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Mods
 * Created by HellFirePvP
 * Date: 31.10.2016 / 11:30
 */
public enum Mods {

    TICONSTRUCT("tconstruct"),
    CRAFTTWEAKER("crafttweaker"),
    JEI("jei"),
    BOTANIA("botania");

    public final String modid;

    private Mods(String modName) {
        this.modid = modName;
    }

    public boolean isPresent() {
        return Loader.isModLoaded(modid);
    }

}
