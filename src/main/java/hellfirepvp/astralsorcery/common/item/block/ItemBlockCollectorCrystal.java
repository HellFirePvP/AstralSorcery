/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyRegistry;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 20:58
 */
public abstract class ItemBlockCollectorCrystal extends ItemBlockCustom implements CrystalAttributeItem, ConstellationItem {

    public ItemBlockCollectorCrystal(Block block, Properties itemProperties) {
        super(block, itemProperties
                .group(CommonProxy.ITEM_GROUP_AS_CRYSTALS)
                .maxStackSize(1));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (isInGroup(group)) {
            for (IWeakConstellation cst : ConstellationRegistry.getWeakConstellations()) {
                ItemStack stack = new ItemStack(this);
                setAttunedConstellation(stack, cst);

                CrystalProperty prop = CrystalPropertyRegistry.INSTANCE.getConstellationProperty(cst);
                CrystalAttributes attr = this.getCreativeTemplateAttributes();
                if (prop != null) {
                    attr = attr.modifyLevel(prop, prop.getMaxTier());
                }
                attr.store(stack);

                stacks.add(stack);
            }
        }
    }
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        IWeakConstellation cst = this.getAttunedConstellation(stack);
        if (cst != null) {
            return new TranslationTextComponent(super.getTranslationKey(stack) + ".typed", cst.getConstellationName());
        }
        return super.getDisplayName(stack);
    }

    public abstract CollectorCrystalType getCollectorType();

    protected abstract CrystalAttributes getCreativeTemplateAttributes();

    @Override
    @Nullable
    public IWeakConstellation getAttunedConstellation(ItemStack stack) {
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), "constellation");
    }

    @Override
    public boolean setAttunedConstellation(ItemStack stack, @Nullable IWeakConstellation cst) {
        if (cst != null) {
            cst.writeToNBT(NBTHelper.getPersistentData(stack), "constellation");
        } else {
            NBTHelper.getPersistentData(stack).remove("constellation");
        }
        return true;
    }

    @Override
    @Nullable
    public IMinorConstellation getTraitConstellation(ItemStack stack) {
        return (IMinorConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), "trait");
    }

    @Override
    public boolean setTraitConstellation(ItemStack stack, @Nullable IMinorConstellation cst) {
        if (cst != null) {
            cst.writeToNBT(NBTHelper.getPersistentData(stack), "trait");
        } else {
            NBTHelper.getPersistentData(stack).remove("trait");
        }
        return true;
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes(ItemStack stack) {
        return CrystalAttributes.getCrystalAttributes(stack);
    }

    @Override
    public void setAttributes(ItemStack stack, @Nullable CrystalAttributes attributes) {
        if (attributes != null) {
            attributes.store(stack);
        } else {
            CrystalAttributes.storeNull(stack);
        }
    }
}
