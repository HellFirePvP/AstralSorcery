/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid;

import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatFluidStack
 * Created by HellFirePvP
 * Date: 19.07.2019 / 10:33
 */
public class CompatFluidStack {

    public static final int BUCKET_VOLUME = 1000;

    private Fluid fluid;
    private int amount;
    private CompoundNBT data = null;

    public CompatFluidStack(Fluid fluid, int amount) {
        if (fluid == null) {
            throw new IllegalArgumentException("Cannot create a fluidstack from a null fluid");
        }

        this.fluid = fluid;
        this.amount = amount;
    }

    public CompatFluidStack(Fluid fluid, int amount, @Nullable CompoundNBT data) {
        this(fluid, amount);
        if (data != null) {
            this.data = data.copy();
        }
    }

    public CompatFluidStack(CompatFluidStack stack, int amount) {
        this(stack.getFluid(), amount, stack.getData());
    }

    @Nonnull
    public Fluid getFluid() {
        return fluid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Nullable
    public CompoundNBT getData() {
        return data;
    }

    public CompatFluidStack copy() {
        return new CompatFluidStack(this.getFluid(), this.getAmount(), this.getData());
    }

    @OnlyIn(Dist.CLIENT)
    public String getLocalizedName() {
        return I18n.format(getUnlocalizedName());
    }

    @Nonnull
    public String getUnlocalizedName() {
        return this.getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey();
    }

    public boolean isFluidEqual(@Nullable CompatFluidStack other) {
        return other != null && getFluid() == other.getFluid() && isFluidStackTagEqual(other);
    }

    private boolean isFluidStackTagEqual(CompatFluidStack other) {
        return this.getData() == null ? other.getData() == null :
                other.getData() == null ? false :
                        this.getData().equals(other.getData());
    }

    public static boolean areFluidStackTagsEqual(@Nullable CompatFluidStack stack1, @Nullable CompatFluidStack stack2) {
        return stack1 == null && stack2 == null ? true :
                stack1 == null || stack2 == null ? false :
                        stack1.isFluidStackTagEqual(stack2);
    }

    public boolean containsFluid(@Nullable CompatFluidStack other) {
        return isFluidEqual(other) && this.getAmount() >= other.getAmount();
    }

    public boolean isFluidStackIdentical(CompatFluidStack other) {
        return isFluidEqual(other) && this.getAmount() == other.getAmount();
    }

    public boolean isFluidEqual(ItemStack other) {
        if (other == null) {
            return false;
        }

        //return CompatFluidUtil.getFluidContained(other).map(this::isFluidEqual).orElse(false);
        return false;
    }

    @Nullable
    public static CompatFluidStack deserialize(CompoundNBT nbt) {
        ResourceLocation key = new ResourceLocation(nbt.getString("fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(key);
        if (fluid == null) {
            return null;
        }
        int amount = nbt.getInt("amount");
        CompoundNBT data = null;
        if (nbt.contains("data", Constants.NBT.TAG_COMPOUND)) {
            data = nbt.getCompound("data");
        }
        return new CompatFluidStack(fluid, amount, data);
    }

    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();
        this.writeToNBT(nbt);
        return nbt;
    }

    public void writeToNBT(CompoundNBT nbt) {
        nbt.putString("fluid", this.fluid.getRegistryName().toString());
        nbt.putInt("amount", this.amount);
        if (this.data != null) {
            nbt.put("data", this.data);
        }
    }

    @Override
    public final int hashCode() {
        int code = 1;
        code = 31 * code + this.getFluid().hashCode();
        code = 31 * code + this.getAmount();
        if (this.getData() != null) {
            code = 31 * code + this.getData().hashCode();
        }
        return code;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof CompatFluidStack)) {
            return false;
        }
        return isFluidEqual((CompatFluidStack) o);
    }

}
