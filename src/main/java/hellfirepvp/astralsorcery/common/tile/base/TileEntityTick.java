/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityTick
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:34
 */
public abstract class TileEntityTick extends TileEntitySynchronized implements ITickableTileEntity {

    private boolean doesSeeSky = false;
    private int lastUpdateTick = -1;

    protected int ticksExisted = 0;

    protected TileEntityTick(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if(ticksExisted == 0) {
            onFirstTick();
        }

        ticksExisted++;
    }

    protected abstract void onFirstTick();

    public int getTicksExisted() {
        return ticksExisted;
    }

    public boolean doesSeeSky() {
        if (lastUpdateTick == -1 || (ticksExisted - lastUpdateTick) >= 20) {
            lastUpdateTick = ticksExisted;

            boolean prevSky = doesSeeSky;
            boolean newSky = MiscUtils.canSeeSky(this.getWorld(), this.getPos().up(), true, this.doesSeeSky);
            if (prevSky != newSky) {
                notifySkyStateUpdate(prevSky, newSky);
                doesSeeSky = newSky;
            }
        }
        return doesSeeSky;
    }

    protected void notifySkyStateUpdate(boolean doesSeeSkyPrev, boolean doesSeeSkyNow) {}

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);
        
        ticksExisted = compound.getInt("ticksExisted");
        doesSeeSky = compound.getBoolean("doesSeeSky");
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.putInt("ticksExisted", ticksExisted);
        compound.putBoolean("doesSeeSky", doesSeeSky);
    }

}
