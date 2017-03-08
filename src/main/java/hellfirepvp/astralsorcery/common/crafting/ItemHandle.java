/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.item.ItemGatedVisibility;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandle
 * Created by HellFirePvP
 * Date: 26.12.2016 / 15:13
 */
public final class ItemHandle {

    public final Type handleType;

    private List<ItemStack> applicableItems = new LinkedList<>();
    private String oreDictName = null;
    private FluidStack fluidTypeAndAmount = null;

    /*
     * PLEASE do not use this without populating the respective fields...
     */
    private ItemHandle(Type type) {
        this.handleType = type;
    }

    public ItemHandle(String oreDictName) {
        this.oreDictName = oreDictName;
        this.handleType = Type.OREDICT;
    }

    public ItemHandle(Fluid fluid) {
        this.fluidTypeAndAmount = new FluidStack(fluid, 1000);
        this.handleType = Type.FLUID;
    }

    public ItemHandle(Fluid fluid, int mbAmount) {
        this.fluidTypeAndAmount = new FluidStack(fluid, mbAmount);
        this.handleType = Type.FLUID;
    }

    public ItemHandle(FluidStack compareStack) {
        this.fluidTypeAndAmount = compareStack.copy();
        this.handleType = Type.FLUID;
    }

    public ItemHandle(@Nonnull ItemStack matchStack) {
        this.applicableItems.add(ItemUtils.copyStackWithSize(matchStack, matchStack.getCount()));
        this.handleType = Type.STACK;
    }

    public static ItemHandle getCrystalVariant(boolean hasToBeTuned, boolean hasToBeCelestial) {
        if(hasToBeTuned) {
            if(hasToBeCelestial) {
                return new ItemHandle(new ItemStack(ItemsAS.tunedCelestialCrystal));
            }

            ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.tunedRockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
            return handle;
        } else {
            if(hasToBeCelestial) {
                ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.celestialCrystal));
                handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
                return handle;
            }

            ItemHandle handle = new ItemHandle(new ItemStack(ItemsAS.rockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.celestialCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedRockCrystal));
            handle.applicableItems.add(new ItemStack(ItemsAS.tunedCelestialCrystal));
            return handle;
        }
    }

    public NonNullList<ItemStack> getApplicableItems() {
        if(oreDictName != null) {
            NonNullList<ItemStack> stacks = OreDictionary.getOres(oreDictName);

            NonNullList<ItemStack> out = NonNullList.create();
            for (ItemStack oreDictIn : stacks) {
                if (oreDictIn.getItemDamage() == OreDictionary.WILDCARD_VALUE && !oreDictIn.isItemStackDamageable()) {
                    oreDictIn.getItem().getSubItems(oreDictIn.getItem(), CreativeTabs.BUILDING_BLOCKS, out);
                } else {
                    out.add(oreDictIn);
                }
            }
            return out;
        } else if(fluidTypeAndAmount != null) {
            return NonNullList.withSize(1, UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluidTypeAndAmount.getFluid()));
        } else {
            NonNullList<ItemStack> l = NonNullList.create();
            l.addAll(applicableItems);
            return l;
        }
    }

    public Object getObjectForRecipe() {
        if(oreDictName != null) {
            return oreDictName;
        }
        if(fluidTypeAndAmount != null) {
            return UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, fluidTypeAndAmount.getFluid());
        }
        return applicableItems;
    }

    @SideOnly(Side.CLIENT)
    public NonNullList<ItemStack> getApplicableItemsForRender() {
        NonNullList<ItemStack> applicable = getApplicableItems();
        Iterator<ItemStack> iterator = applicable.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if(stack.isEmpty()) continue;
            Item i = stack.getItem();
            if(i instanceof ItemGatedVisibility) {
                if(!((ItemGatedVisibility) i).isSupposedToSeeInRender(stack)) {
                    iterator.remove();
                }
            }
        }
        return applicable;
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
        if(stack.isEmpty()) return false;

        switch (handleType) {
            case OREDICT:
                for (int id : OreDictionary.getOreIDs(stack)) {
                    String name = OreDictionary.getOreName(id);
                    if (name != null && name.equals(oreDictName)) {
                        return true;
                    }
                }
                return false;
            case STACK:
                for (ItemStack applicable : applicableItems) {
                    if(ItemUtils.stackEqualsNonNBT(applicable, stack)) {
                        return true;
                    }
                }
                return false;
            case FLUID:
                return ItemUtils.drainFluidFromItem(stack, fluidTypeAndAmount, false);
        }
        return false;
    }

    public void serialize(ByteBuf byteBuf) {
        byteBuf.writeInt(handleType.ordinal());
        switch (handleType) {
            case OREDICT:
                ByteBufUtils.writeString(byteBuf, this.oreDictName);
                break;
            case STACK:
                byteBuf.writeInt(this.applicableItems.size());
                for (ItemStack applicableItem : this.applicableItems) {
                    ByteBufUtils.writeItemStack(byteBuf, applicableItem);
                }
                break;
            case FLUID:
                ByteBufUtils.writeFluidStack(byteBuf, this.fluidTypeAndAmount);
                break;
        }
    }

    public static ItemHandle deserialize(ByteBuf byteBuf) {
        Type type = Type.values()[byteBuf.readInt()];
        ItemHandle handle = new ItemHandle(type);
        switch (type) {
            case OREDICT:
                handle.oreDictName = ByteBufUtils.readString(byteBuf);
                break;
            case STACK:
                int amt = byteBuf.readInt();
                for (int i = 0; i < amt; i++) {
                    handle.applicableItems.add(ByteBufUtils.readItemStack(byteBuf));
                }
                break;
            case FLUID:
                handle.fluidTypeAndAmount = ByteBufUtils.readFluidStack(byteBuf);
                break;
        }
        return handle;
    }

    public static enum Type {

        OREDICT,
        STACK,
        FLUID

    }

}
