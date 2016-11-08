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

    TICONSTRUCT("tconstruct");

    public final String modName;

    private Mods(String modName) {
        this.modName = modName;
    }

    public boolean isPresent() {
        return Loader.isModLoaded(modName);
    }

}
