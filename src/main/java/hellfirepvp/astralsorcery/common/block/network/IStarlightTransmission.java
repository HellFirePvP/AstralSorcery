package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.constellation.Constellation;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightTransmission
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:25
 */
public interface IStarlightTransmission {

    @Nullable
    public Constellation getTransmittingType();

    default public boolean canDrain(Constellation type) {
        return type != null && type.equals(getTransmittingType());
    }

    public int drain(Constellation type, int tryAmount);

}
