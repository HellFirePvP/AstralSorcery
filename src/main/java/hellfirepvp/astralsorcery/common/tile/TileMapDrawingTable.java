/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileMapDrawingTable
 * Created by HellFirePvP
 * Date: 18.03.2017 / 20:02
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
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeNetNBT(compound);

        compound.setBoolean("parchment", this.hasParchment);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(0.5, 0.5, 0.5);
    }
}
