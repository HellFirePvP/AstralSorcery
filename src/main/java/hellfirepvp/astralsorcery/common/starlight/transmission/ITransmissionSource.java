package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionNode;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ITransmissionSource
 * Created by HellFirePvP
 * Date: 03.08.2016 / 11:06
 */
public interface ITransmissionSource extends IPrismTransmissionNode {

    @Nullable
    public Constellation getSourceType();

    public int getAvailableCharge();

}
