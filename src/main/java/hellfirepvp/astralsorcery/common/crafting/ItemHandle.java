/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandle
 * Created by HellFirePvP
 * Date: 26.12.2016 / 15:13
 */
public class ItemHandle {

    private ItemStack applicableItem = null;
    private String oreDictName = null;
    private FluidStack fluidTypeAndAmount = null;

    public ItemHandle(String oreDictName) {
        this.oreDictName = oreDictName;
    }

    public ItemHandle(Fluid fluid) {
        this.fluidTypeAndAmount = new FluidStack(fluid, 1000);
    }

    public ItemHandle(Fluid fluid, int mbAmount) {
        this.fluidTypeAndAmount = new FluidStack(fluid, mbAmount);
    }

    public ItemHandle(FluidStack compareStack) {
        this.fluidTypeAndAmount = compareStack.copy();
    }

    public ItemHandle(ItemStack matchStack) {
        this.applicableItem = ItemUtils.copyStackWithSize(matchStack, matchStack.stackSize);
    }

    public List<ItemStack> getApplicableItems() {
        if(oreDictName != null) {
            List<ItemStack> stacks = OreDictionary.getOres(oreDictName);

            List<ItemStack> out = new LinkedList<>();
            for (ItemStack oreDictIn : stacks) {
                if (oreDictIn.getItemDamage() == OreDictionary.WILDCARD_VALUE && !oreDictIn.isItemStackDamageable()) {
                    oreDictIn.getItem().getSubItems(oreDictIn.getItem(), CreativeTabs.BUILDING_BLOCKS, out);
                } else {
                    out.add(oreDictIn);
                }
            }
            return out;
        } else if(fluidTypeAndAmount != null) {
            return Lists.newArrayList(UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluidTypeAndAmount.getFluid()));
        } else {
            return Lists.newArrayList(applicableItem);
        }
    }

    public Object getObjectForRecipe() {
        if(oreDictName != null) {
            return oreDictName;
        }
        if(fluidTypeAndAmount != null) {
            return UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluidTypeAndAmount.getFluid());
        }
        return applicableItem;
    }

    @Nullable
    public String getOreDictName() {
        return oreDictName;
    }

    @Nullable
    public FluidStack getFluidTypeAndAmount() {
        return fluidTypeAndAmount;
    }

    public boolean matchCrafting(ItemStack stack) {
        if(stack == null) return applicableItem == null;

        if(oreDictName != null) {
            for (int id : OreDictionary.getOreIDs(stack)) {
                String name = OreDictionary.getOreName(id);
                if (name != null && name.equals(oreDictName)) {
                    return true;
                }
            }
            return false;
        } else if(fluidTypeAndAmount != null) {
            return ItemUtils.drainFluidFromItem(stack, fluidTypeAndAmount, false);
        } else {
            return ItemUtils.stackEqualsNonNBT(applicableItem, stack);
        }
    }

}
