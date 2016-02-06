package hellfire.astralSorcery.common.api;

import hellfire.astralSorcery.api.IAPIHandler;
import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.common.constellation.Constellation;
import hellfire.astralSorcery.common.constellation.ConstellationRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 02:00
 */
public class DefaultAPIHandler implements IAPIHandler {

    @Override
    public IConstellation createNewConstellation() {
        return new Constellation();
    }

    @Override
    public void registerConstellation(int tier, IConstellation constellation) {
        ConstellationRegistry.registerConstellation(tier, constellation);
    }

}
