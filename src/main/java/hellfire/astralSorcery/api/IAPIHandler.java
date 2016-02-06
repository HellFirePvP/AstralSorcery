package hellfire.astralSorcery.api;

import hellfire.astralSorcery.api.constellation.IConstellation;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:54
 */
public interface IAPIHandler {

    public IConstellation createNewConstellation();

    public void registerConstellation(int tier, IConstellation constellation);
}
