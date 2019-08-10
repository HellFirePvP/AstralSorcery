/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
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
public abstract class ItemBlockCollectorCrystal extends ItemBlockCustom implements CrystalPropertyItem {

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
                setConstellation(stack, cst);
                applyCrystalProperties(stack, getMaxProperties(stack));
                stacks.add(stack);
            }
        }
    }

    public abstract CollectorCrystalType getCollectorType();

    public static void setConstellation(ItemStack stack, IWeakConstellation constellation) {
        constellation.writeToNBT(NBTHelper.getPersistentData(stack), "constellation");
    }

    public static IWeakConstellation getConstellation(ItemStack stack) {
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), "constellation");
    }

    public static void setTraitConstellation(ItemStack stack, @Nullable IMinorConstellation constellation) {
        if(constellation == null) return;
        constellation.writeToNBT(NBTHelper.getPersistentData(stack), "trait");
    }

    public static IMinorConstellation getTrait(ItemStack stack) {
        return (IMinorConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), "trait");
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
