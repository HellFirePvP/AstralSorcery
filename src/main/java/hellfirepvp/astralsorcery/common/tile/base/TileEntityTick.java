/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ITickable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityTick
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:34
 */
public abstract class TileEntityTick extends TileEntitySynchronized implements ITickable {

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

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        
        ticksExisted = compound.getInt("ticksExisted");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInt("ticksExisted", ticksExisted);
    }

}
