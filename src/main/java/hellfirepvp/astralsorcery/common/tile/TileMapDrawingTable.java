package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileMapDrawingTable
 * Created by HellFirePvP
 * Date: 14.03.2017 / 22:18
 */
public class TileMapDrawingTable extends TileSkybound {

    private boolean hasParchment = false;

    public TileMapDrawingTable() {}

    @Override
    protected void onFirstTick() {}

    public void addParchment() {
        this.hasParchment = true;
        markForUpdate();
    }

    public boolean hasParchment() {
        return hasParchment;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.hasParchment = compound.getBoolean("parchment");
    }

    @Override
    public void writeNetNBT(NBTTagCompound compound) {
        super.writeNetNBT(compound);

        compound.setBoolean("parchment", this.hasParchment);
    }

}
