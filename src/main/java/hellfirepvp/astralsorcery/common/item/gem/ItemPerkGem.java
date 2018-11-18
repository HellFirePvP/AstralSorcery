/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.gem;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.GemAttributeModifier;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemPerkGem
 * Created by HellFirePvP
 * Date: 18.11.2018 / 09:30
 */
public class ItemPerkGem extends Item {

    public ItemPerkGem() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        for (GemAttributeModifier mod : getModifiers(stack)) {
            tooltip.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + mod.getLocalizedDisplayString());
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote) {
            return;
        }

        if (getModifiers(stack).isEmpty()) {
            GemAttributeHelper.rollGem(stack);
        }
    }

    @Nonnull
    public static List<GemAttributeModifier> getModifiers(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemPerkGem)) {
            return Lists.newArrayList();
        }
        List<GemAttributeModifier> modifiers = Lists.newArrayList();
        NBTTagList mods = NBTHelper.getPersistentData(stack).getTagList("modifiers", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < mods.tagCount(); i++) {
            NBTTagCompound tag = mods.getCompoundTagAt(i);
            modifiers.add(GemAttributeModifier.deserialize(tag));
        }
        return modifiers;
    }

    public static boolean setModifiers(ItemStack stack, List<GemAttributeModifier> modifiers) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemPerkGem)) {
            return false;
        }
        NBTTagList mods = new NBTTagList();
        for (GemAttributeModifier modifier : modifiers) {
            mods.appendTag(modifier.serialize());
        }
        NBTHelper.getPersistentData(stack).setTag("modifiers", mods);
        return true;
    }

}
