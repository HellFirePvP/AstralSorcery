package hellfire.astralSorcery.common.integration;

import net.minecraftforge.fml.common.Loader;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 21:24
 */
public abstract class Integration {

    public boolean present = false;

    public abstract String getModID();

    public abstract void init();

    public final void tryInit() {
        if(Loader.isModLoaded(getModID())) {
            present = true;
            init();
        }
    }

}
