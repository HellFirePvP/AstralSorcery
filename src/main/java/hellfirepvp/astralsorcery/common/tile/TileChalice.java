/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tile.FluidTankAccess;
import hellfirepvp.astralsorcery.common.util.tile.SimpleSingleFluidTank;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileChalice
 * Created by HellFirePvP
 * Date: 09.11.2019 / 15:03
 */
public class TileChalice extends TileEntityTick {

    private static final int TANK_SIZE = 64 * FluidAttributes.BUCKET_VOLUME;

    private SimpleSingleFluidTank tank;
    private FluidTankAccess access;

    private Vector3 rotation = new Vector3();
    private Vector3 prevRotation = new Vector3();
    private Vector3 rotationVec = null;

    public TileChalice() {
        super(TileEntityTypesAS.CHALICE);

        this.tank = new SimpleSingleFluidTank(TANK_SIZE);
        this.tank.setOnUpdate(this::markForUpdate);
        this.access = new FluidTankAccess();
        this.access.putTank(0, tank, Direction.DOWN);
    }

    @Override
    public void tick() {
        super.tick();

        if (getWorld().isRemote()) {
            if (rotationVec == null) {
                rotationVec = Vector3.random().normalize().multiply(1.5F);
            }

            this.prevRotation = this.rotation.clone();
            this.rotation.add(this.rotationVec);
        }
    }

    @Nonnull
    public SimpleSingleFluidTank getTank() {
        return this.tank;
    }

    @Nonnull
    public IFluidHandler getTankAccess() {
        return this.access.getCapability(Direction.DOWN).orElse(null);
    }

    @Nonnull
    public Vector3 getRotation() {
        return rotation;
    }

    @Nonnull
    public Vector3 getPrevRotation() {
        return prevRotation;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.tank.readNBT(compound.getCompound("tank"));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.put("tank", this.tank.writeNBT());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.access.hasCapability(cap, side)) {
            return this.access.getCapability(side).cast();
        }
        return super.getCapability(cap, side);
    }

}
