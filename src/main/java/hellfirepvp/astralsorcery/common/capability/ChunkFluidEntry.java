/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.capability;

import hellfirepvp.astralsorcery.common.data.config.registry.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.FluidRarityEntry;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChunkFluidEntry
 * Created by HellFirePvP
 * Date: 25.04.2020 / 10:26
 */
public class ChunkFluidEntry implements INBTSerializable<CompoundNBT> {

    private FluidStack chunkFluid = FluidStack.EMPTY;
    private int mbAmount = 0;

    private boolean initialized = false;

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean isEmpty() {
        return chunkFluid.isEmpty() || mbAmount <= 0;
    }

    public void setEmpty() {
        this.chunkFluid = FluidStack.EMPTY;
        this.mbAmount = 0;
    }

    public void generate(long seed) {
        if (isInitialized()) {
            return;
        }

        Random r = new Random(seed);
        FluidRarityEntry fluidEntry = FluidRarityRegistry.INSTANCE.getRandomValue(r);
        if (fluidEntry != null) {
            this.mbAmount = fluidEntry.getRandomAmount(r);
            this.chunkFluid = new FluidStack(fluidEntry.getFluid(), FluidAttributes.BUCKET_VOLUME);
        } else {
            this.setEmpty();
        }
        this.initialized = true;
    }

    @Nonnull
    public FluidStack drain(int amount, boolean simulate) {
        if (!isInitialized() || isEmpty()) {
            return new FluidStack(Fluids.WATER, amount);
        }

        int drainableAmount = Math.min(amount, this.mbAmount);
        FluidStack drained = this.chunkFluid.copy();
        drained.setAmount(drainableAmount);
        if (!simulate) {
            this.mbAmount -= drainableAmount;
            if (isEmpty()) {
                setEmpty();
            }
        }
        return drained;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        NBTHelper.setFluid(nbt, "chunkFluid", this.chunkFluid);
        nbt.putInt("mbAmount", this.mbAmount);
        nbt.putBoolean("initialized", this.initialized);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.chunkFluid = NBTHelper.getFluid(nbt, "chunkFluid");
        this.mbAmount = nbt.getInt("mbAmount");
        this.initialized = nbt.getBoolean("initialized");
    }
}
