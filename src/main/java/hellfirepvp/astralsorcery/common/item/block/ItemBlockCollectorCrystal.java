/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 20:58
 */
public abstract class ItemBlockCollectorCrystal extends ItemBlockCustom implements CrystalPropertyItem, ConstellationItem {

    public ItemBlockCollectorCrystal(Block block, Properties itemProperties) {
        super(block, itemProperties
                .group(RegistryItems.ITEM_GROUP_AS_CRYSTALS)
                .maxStackSize(1));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (isInGroup(group)) {
            for (IWeakConstellation cst : ConstellationRegistry.getWeakConstellations()) {
                ItemStack stack = new ItemStack(this);
                setAttunedConstellation(stack, cst);
                applyCrystalProperties(stack, getMaxProperties(stack));
                stacks.add(stack);
            }
        }
    }

    public abstract CollectorCrystalType getCollectorType();

    @Override
    public IWeakConstellation getAttunedConstellation(ItemStack stack) {
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), "constellation");
    }

    @Override
    public boolean setAttunedConstellation(ItemStack stack, IWeakConstellation cst) {
        if (cst != null) {
            cst.writeToNBT(NBTHelper.getPersistentData(stack), "constellation");
        } else {
            NBTHelper.getPersistentData(stack).remove("constellation");
        }
        return true;
    }

    @Override
    public IMinorConstellation getTraitConstellation(ItemStack stack) {
        return (IMinorConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), "trait");
    }

    @Override
    public boolean setTraitConstellation(ItemStack stack, IMinorConstellation cst) {
        if (cst != null) {
            cst.writeToNBT(NBTHelper.getPersistentData(stack), "trait");
        } else {
            NBTHelper.getPersistentData(stack).remove("trait");
        }
        return true;
    }

    @Nullable
    @Override
    public CrystalProperties getProperties(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    @Override
    public void applyCrystalProperties(ItemStack stack, @Nonnull CrystalProperties prop) {
        CrystalProperties.applyCrystalProperties(stack, prop);
    }
}
