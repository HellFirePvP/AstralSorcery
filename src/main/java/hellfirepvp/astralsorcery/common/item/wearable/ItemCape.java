/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectFactory;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectRegistry;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCape
 * Created by HellFirePvP
 * Date: 09.10.2017 / 23:08
 */
public class ItemCape extends ItemArmor {

    public ItemCape() {
        super(RegistryItems.imbuedLeatherMaterial, -1, EntityEquipmentSlot.CHEST);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(this.isInCreativeTab(tab)) {
            ItemStack stack;
            for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
                stack = new ItemStack(this);
                setAttunedConstellation(stack, c);
                items.add(stack);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IConstellation cst = getAttunedConstellation(stack);
        if(cst != null) {
            tooltip.add(cst.getUnlocalizedName());
        }
    }

    @Nullable
    public static CapeArmorEffect getCapeEffect(@Nullable EntityPlayer entity) {
        if(entity == null) return null;
        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        IConstellation cst = getAttunedConstellation(stack);
        if(cst == null) {
            return null;
        }
        return getCapeEffect(stack);
    }

    @Nullable
    public static <V extends CapeArmorEffect> V getCapeEffect(@Nullable EntityPlayer entity, @Nonnull IConstellation expectedConstellation) {
        if(entity == null) return null;
        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        IConstellation cst = getAttunedConstellation(stack);
        if(cst == null || cst != expectedConstellation) {
            return null;
        }
        return getCapeEffect(stack);
    }

    @Nullable
    public static <V extends CapeArmorEffect> V getCapeEffect(@Nonnull ItemStack stack) {
        IConstellation cst = getAttunedConstellation(stack);
        if(cst == null) {
            return null;
        }
        CapeEffectFactory<? extends CapeArmorEffect> call = CapeEffectRegistry.getArmorEffect(cst);
        if(call == null) {
            return null;
        }
        try {
            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            return (V) call.deserializeCapeEffect(cmp);
        } catch (Exception exc) {
            return null;
        }
    }

    @Nullable
    public static IConstellation getAttunedConstellation(@Nonnull ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemCape)) {
            return null;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        return IConstellation.readFromNBT(cmp);
    }

    public static void setAttunedConstellation(@Nonnull ItemStack stack, @Nonnull IConstellation cst) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemCape)) {
            return;
        }
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cst.writeToNBT(cmp);
    }

}
