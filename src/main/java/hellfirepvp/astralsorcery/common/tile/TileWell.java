/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefactionContext;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverWell;
import hellfirepvp.astralsorcery.common.util.tile.PrecisionSingleFluidTank;
import hellfirepvp.astralsorcery.common.util.tile.TileInventoryFiltered;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileWell
 * Created by HellFirePvP
 * Date: 30.06.2019 / 21:53
 */
public class TileWell extends TileReceiverBase<StarlightReceiverWell> {

    private static final int MAX_CAPACITY = 2000;

    private WellLiquefaction runningRecipe = null;

    private PrecisionSingleFluidTank tank;
    private TileInventoryFiltered inventory;
    private double starlightBuffer = 0;
    private float posDistribution = -1;

    public TileWell() {
        super(TileEntityTypesAS.WELL);

        this.tank = new PrecisionSingleFluidTank(MAX_CAPACITY, Direction.DOWN);
        this.tank.setAllowInput(false);
        this.tank.setOnUpdate(this::markForUpdate);

        this.inventory = new TileInventoryFiltered(this, () -> 1, Direction.DOWN);
        this.inventory.canExtract((slot, amount, existing) -> false);
        this.inventory.canInsert((slot, toAdd, existing) -> {
            if (toAdd.isEmpty()) {
                return true;
            }
            return existing.isEmpty() && RecipeTypesAS.TYPE_WELL
                    .findRecipe(Dist.DEDICATED_SERVER, new WellLiquefactionContext(this)) != null;
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isRemote()) {
            if (this.doesSeeSky()) {
                this.collectStarlight();
            }
        }
    }

    private void collectStarlight() {
        double sbDayDistribution = DayTimeHelper.getCurrentDaytimeDistribution(world);
        sbDayDistribution = 0.3 + (0.7 * sbDayDistribution);
        int yLevel = getPos().getY();
        float dstr;
        if(yLevel > 120) {
            dstr = 1F;
        } else {
            dstr = yLevel / 120F;
        }
        if(posDistribution == -1) {
            posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, getPos());
        }

        sbDayDistribution *= dstr;
        sbDayDistribution *= 1 + (1.2 * posDistribution);
        starlightBuffer += Math.max(0.0001, sbDayDistribution);
    }

    public void receiveStarlight(double amount) {
        this.starlightBuffer += amount;
        this.markForUpdate();
    }

    @Nonnull
    public PrecisionSingleFluidTank getTank() {
        return tank;
    }

    @Nonnull
    public TileInventoryFiltered getInventory() {
        return inventory;
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockwell.name";
    }

    @Nonnull
    @Override
    public StarlightReceiverWell provideEndpoint(BlockPos at) {
        return new StarlightReceiverWell(at);
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.tank.readNBT(compound.getCompound("tank"));
        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.put("tank", this.tank.writeNBT());
        compound.put("inventory", this.inventory.serialize());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.tank.hasCapability(cap, side)) {
            return this.tank.getCapability().cast();
        }
        if (this.inventory.hasCapability(cap, side)) {
            return this.inventory.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void onFirstTick() {}
}
