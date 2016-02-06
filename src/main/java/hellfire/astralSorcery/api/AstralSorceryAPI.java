package hellfire.astralSorcery.api;

import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.internal.DummyAPIHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:54
 */
public class AstralSorceryAPI {

    private static IAPIHandler handler = new DummyAPIHandler();

    public static void setAPIHandler(IAPIHandler handle) {
        handler = handle;
    }

    public static IConstellation createNewConstellation() {
        return handler.createNewConstellation();
    }

    public static void registerConstellation(int tier, IConstellation constellation) {
        handler.registerConstellation(tier, constellation);
    }

}
