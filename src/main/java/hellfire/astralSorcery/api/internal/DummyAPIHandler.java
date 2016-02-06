package hellfire.astralSorcery.api.internal;

import hellfire.astralSorcery.api.IAPIHandler;
import hellfire.astralSorcery.api.constellation.IConstellation;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:56
 */
public class DummyAPIHandler implements IAPIHandler {

    @Override
    public IConstellation createNewConstellation() {
        return null;
    }

    @Override
    public void registerConstellation(int tier, IConstellation constellation) {}

}
