package hellfirepvp.astralsorcery.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialCrystals
 * Created by HellFirePvP
 * Date: 15.09.2016 / 00:13
 */
public class TileCelestialCrystals extends TileEntitySynchronized {

    private int stage = 0;

    public TileCelestialCrystals() {}

    public TileCelestialCrystals(int stage) {
        this.stage = stage;
    }

    public int getGrowth() {
        return stage;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.stage = MathHelper.clamp_int(compound.getInteger("stage"), 0, 4);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("stage", stage);
    }

}
