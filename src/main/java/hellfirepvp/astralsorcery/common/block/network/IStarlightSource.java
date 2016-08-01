package hellfirepvp.astralsorcery.common.block.network;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightSource
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:23
 */
public interface IStarlightSource extends IStarlightTransmission {

    default public int getAvailableCharge() {
        return 0;
    }

}
