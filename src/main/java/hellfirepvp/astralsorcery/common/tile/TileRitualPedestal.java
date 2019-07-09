/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.structure.PatternRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverRitualPedestal;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import hellfirepvp.astralsorcery.common.util.tile.TileInventoryFiltered;
import hellfirepvp.observerlib.api.ChangeSubscriber;
import hellfirepvp.observerlib.api.ObserverHelper;
import hellfirepvp.observerlib.common.change.ChangeObserverStructure;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualPedestal
 * Created by HellFirePvP
 * Date: 09.07.2019 / 19:25
 */
public class TileRitualPedestal extends TileReceiverBase<StarlightReceiverRitualPedestal> {

    private TileInventoryFiltered inventory;

    private ChangeSubscriber<ChangeObserverStructure> structureMatch;
    private boolean hasMultiblock = false;

    private boolean needsNetworkSync = false;

    public TileRitualPedestal() {
        super(TileEntityTypesAS.RITUAL_PEDESTAL);

        this.inventory = new TileInventoryFiltered(this, () -> 1, Direction.DOWN);
        this.inventory.canExtract((slot, amount, existing) -> !existing.isEmpty());
        this.inventory.canInsert(((slot, toAdd, existing) -> {
            return existing.isEmpty(); //TODO crystals && existing.getItem() instanceof Crystal
        }));
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            updateMultiblockState();
        }
    }

    private void updateMultiblockState() {
        if (this.structureMatch == null) {
            this.structureMatch = StructureTypesAS.PTYPE_RITUAL_PEDESTAL.observe(getWorld(), getPos());
        }
        boolean found = this.structureMatch.isValid(getWorld());
        if (found != this.hasMultiblock) {
            LogCategory.STRUCTURE_MATCH.info(() ->
                    "Structure match updated: " + this.getClass().getName() + " at " + this.getPos() +
                            " (" + this.hasMultiblock + " -> " + found + ")");
            this.hasMultiblock = found;
            markForUpdate();
            markForSync();
        }
    }

    private void markForSync() {
        this.needsNetworkSync = true;
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockritualpedestal.name";
    }

    @Nonnull
    @Override
    public StarlightReceiverRitualPedestal provideEndpoint(BlockPos at) {
        return new StarlightReceiverRitualPedestal(at);
    }

    @Override
    protected void onFirstTick() {}

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.put("inventory", this.inventory.serialize());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.inventory.hasCapability(cap, side)) {
            return this.inventory.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }

}
