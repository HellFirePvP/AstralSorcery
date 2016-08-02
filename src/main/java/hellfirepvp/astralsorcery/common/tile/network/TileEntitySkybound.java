package hellfirepvp.astralsorcery.common.tile.network;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntitySkybound
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:35
 */
public abstract class TileEntitySkybound extends TileEntityCounting {

    protected boolean doesSeeSky = false;

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            doesSeeSky = worldObj.canSeeSky(getPos());
        }
    }
}
