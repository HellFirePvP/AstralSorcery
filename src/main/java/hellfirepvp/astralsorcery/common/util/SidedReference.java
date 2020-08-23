package hellfirepvp.astralsorcery.common.util;

import net.minecraftforge.fml.LogicalSide;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SidedReference
 * Created by HellFirePvP
 * Date: 23.08.2020 / 13:39
 */
public class SidedReference<T> {

    private T clientData = null;
    private T serverData = null;

    public Optional<T> getData(LogicalSide side) {
        if (side.isClient()) {
            return Optional.ofNullable(this.clientData);
        }
        return Optional.ofNullable(this.serverData);
    }

    public void setData(LogicalSide side, T data) {
        if (side.isClient()) {
            this.clientData = data;
        } else {
            this.serverData = data;
        }
    }
}
