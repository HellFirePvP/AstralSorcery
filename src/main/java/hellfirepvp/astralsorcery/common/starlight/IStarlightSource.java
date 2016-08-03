package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.constellation.Constellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightSource
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:23
 */
public interface IStarlightSource extends IStarlightTransmission {

    public Constellation getSourceType();

    public int tryDrain(Constellation type, int amount);

}
