package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.tile.TileEntitySynchronized;
import net.minecraft.util.ITickable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityCounting
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:34
 */
public abstract class TileEntityCounting extends TileEntitySynchronized implements ITickable {

    protected int ticksExisted = 0;

    @Override
    public void update() {
        ticksExisted++;
    }

}
