/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.observerlib.api.ChangeSubscriber;
import hellfirepvp.observerlib.api.ObserverHelper;
import hellfirepvp.observerlib.common.change.ChangeObserverStructure;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityTick
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:34
 */
public abstract class TileEntityTick extends TileEntitySynchronized implements ITickableTileEntity, TileRequiresMultiblock {

    private boolean doesSeeSky = false;
    private int lastUpdateTick = -1;

    private ChangeSubscriber<ChangeObserverStructure> structureMatch;
    private boolean hasMultiblock = false;

    protected int ticksExisted = 0;

    protected TileEntityTick(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if (ticksExisted == 0) {
            onFirstTick();
        }

        ticksExisted++;
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return null;
    }

    protected void onFirstTick() {}

    public int getTicksExisted() {
        return ticksExisted;
    }

    public boolean doesSeeSky() {
        if (getWorld().isRemote()) {
            return this.doesSeeSky;
        }

        if (lastUpdateTick == -1 || (ticksExisted - lastUpdateTick) >= 20) {
            lastUpdateTick = ticksExisted;

            boolean prevSky = doesSeeSky;
            boolean newSky = MiscUtils.canSeeSky(this.getWorld(), this.getPos().up(), true, this.doesSeeSky);
            if (prevSky != newSky) {
                this.notifySkyStateUpdate(prevSky, newSky);
                this.doesSeeSky = newSky;
                this.markForUpdate();
            }
        }
        return doesSeeSky;
    }

    public boolean hasMultiblock() {
        if (getWorld().isRemote()) {
            return this.hasMultiblock;
        }

        if (this.getRequiredStructureType() == null) {
            refreshMatcher();
            resetMultiblockState();
            return false;
        }

        refreshMatcher();
        if (this.structureMatch == null) {
            this.structureMatch = this.getRequiredStructureType().observe(getWorld(), getPos());
        }
        boolean prevFound = this.hasMultiblock;
        boolean found = this.structureMatch.isValid(getWorld());
        if (prevFound != found) {
            LogCategory.STRUCTURE_MATCH.info(() ->
                    "Structure match updated: " + this.getClass().getName() + " at " + this.getPos() +
                            " (" + this.hasMultiblock + " -> " + found + ")");
            this.notifyMultiblockStateUpdate(prevFound, found);
            this.hasMultiblock = found;
            this.markForUpdate();
        }
        return this.hasMultiblock;
    }

    private void refreshMatcher() {
        StructureType struct = this.getRequiredStructureType();
        if (this.structureMatch != null) {
            //Same registry name as the structure type.
            ResourceLocation key = this.structureMatch.getObserver().getProviderRegistryName();
            if (struct == null || !key.equals(struct.getRegistryName())) {
                ObserverHelper.getHelper().removeObserver(getWorld(), getPos());
                this.structureMatch = null;
            }
        }
        if (struct == null && ObserverHelper.getHelper().getSubscriber(getWorld(), getPos()) != null) {
            ObserverHelper.getHelper().removeObserver(getWorld(), getPos());
        }
    }

    private void resetMultiblockState() {
        if (this.hasMultiblock) {
            this.notifyMultiblockStateUpdate(true, false);
            this.hasMultiblock = false;
            this.markForUpdate();
        }
    }


    protected void notifySkyStateUpdate(boolean doesSeeSkyPrev, boolean doesSeeSkyNow) {}

    protected void notifyMultiblockStateUpdate(boolean hadMultiblockPrev, boolean hasMultiblockNow) {}

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);
        
        this.ticksExisted = compound.getInt("ticksExisted");
        this.doesSeeSky = compound.getBoolean("doesSeeSky");
        this.hasMultiblock = compound.getBoolean("hasMultiblock");
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.putInt("ticksExisted", this.ticksExisted);
        compound.putBoolean("doesSeeSky", this.doesSeeSky);
        compound.putBoolean("hasMultiblock", this.hasMultiblock);
    }

}
