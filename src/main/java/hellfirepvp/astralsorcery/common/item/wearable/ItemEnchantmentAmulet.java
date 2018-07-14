/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletEnchantHelper;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletEnchantment;
import hellfirepvp.astralsorcery.common.enchantment.amulet.EnchantmentUpgradeHelper;
import hellfirepvp.astralsorcery.common.enchantment.amulet.PlayerAmuletHandler;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemEnchantmentAmulet
 * Created by HellFirePvP
 * Date: 25.01.2018 / 19:05
 */
public class ItemEnchantmentAmulet extends Item implements ItemDynamicColor, IBauble {

    private static Random rand = new Random();

    public ItemEnchantmentAmulet() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);

            items.add(stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        List<AmuletEnchantment> enchantments = getAmuletEnchantments(stack);
        for (AmuletEnchantment ench : enchantments) {
            tooltip.add(TextFormatting.BLUE + ench.getDescription());
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && !getAmuletColor(stack).isPresent()) {
            freezeAmuletColor(stack);
        }
        if(!worldIn.isRemote && getAmuletEnchantments(stack).isEmpty()) {
            AmuletEnchantHelper.rollAmulet(stack, 0);
        }
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if(tintIndex != 1) return 0xFFFFFFFF;
        Optional<Integer> color = getAmuletColor(stack);
        if(color.isPresent()) {
            return color.get();
        }
        int tick = (int) (ClientScheduler.getClientTick() % 500000L);
        int c = Color.getHSBColor((tick / 500000F) * 360F, 0.7F, 1F).getRGB();
        return c | 0xFF000000;
    }

    public static Optional<Integer> getAmuletColor(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return Optional.empty();
        }
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if(!tag.hasKey("amuletColor")) {
            return Optional.empty();
        }
        return Optional.of(tag.getInteger("amuletColor"));
    }

    public static void freezeAmuletColor(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if(tag.hasKey("amuletColor")) {
            return;
        }
        if(rand.nextInt(400) == 0) {
            tag.setInteger("amuletColor", 0xFFFFFFFF);
        } else {
            float hue = rand.nextFloat() * 360F;
            tag.setInteger("amuletColor", Color.getHSBColor(hue, 0.7F, 1.0F).getRGB() | 0xFF000000);
        }
    }

    public static List<AmuletEnchantment> getAmuletEnchantments(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return Lists.newArrayList();
        }

        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if(!tag.hasKey("amuletEnchantments")) {
            return Lists.newArrayList();
        }
        NBTTagList enchants = tag.getTagList("amuletEnchantments", Constants.NBT.TAG_COMPOUND);
        List<AmuletEnchantment> enchantments = new ArrayList<>(enchants.tagCount());
        for (int i = 0; i < enchants.tagCount(); i++) {
            AmuletEnchantment ench = AmuletEnchantment.deserialize(enchants.getCompoundTagAt(i));
            if(ench != null) {
                enchantments.add(ench);
            }
        }
        enchantments.sort(Comparator.comparing(AmuletEnchantment::getType));
        return enchantments;
    }

    public static void setAmuletEnchantments(ItemStack stack, List<AmuletEnchantment> enchantments) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }
        enchantments.sort(Comparator.comparing(AmuletEnchantment::getType));

        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        NBTTagList enchants = tag.hasKey("amuletEnchantments", Constants.NBT.TAG_COMPOUND) ?
                tag.getTagList("amuletEnchantments", Constants.NBT.TAG_COMPOUND) : new NBTTagList();
        for (AmuletEnchantment enchant : enchantments) {
            enchants.appendTag(enchant.serialize());
        }
        tag.setTag("amuletEnchantments", enchants);
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if(itemstack != null && itemstack.getItem() instanceof ItemEnchantmentAmulet && player instanceof EntityPlayer) {
            List<AmuletEnchantment> enchList = getAmuletEnchantments(itemstack);
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                ItemStack stack = player.getItemStackFromSlot(slot);
                if (canBeAffected(stack, enchList)) {
                    EnchantmentUpgradeHelper.applyAmuletOwner(player.getItemStackFromSlot(slot), (EntityPlayer) player);
                }
            }
        }
    }

    public static boolean canBeAffected(ItemStack stack, Collection<AmuletEnchantment> enchantments) {
        if (EnchantmentUpgradeHelper.isItemBlacklisted(stack) || stack.isEmpty()) return false;

        for (AmuletEnchantment ench : enchantments) {
            if (canInfluenceType(ench.getType(), stack, enchantments)) {
                return true;
            }
        }
        return false;
    }

    private static boolean canInfluenceType(AmuletEnchantment.Type type, ItemStack stack, Collection<AmuletEnchantment> enchantments) {
        for (AmuletEnchantment ench : enchantments) {
            Enchantment e = ench.getEnchantment();
            if (e == null || e.type == null) continue;
            if(ench.getType() != type) continue;

            switch (type) {
                case ADD_TO_SPECIFIC:
                    if (e.type.canEnchantItem(stack.getItem())) {
                        return true;
                    }
                    break;
                case ADD_TO_EXISTING_SPECIFIC:
                    if (EnchantmentHelper.getEnchantmentLevel(e, stack) > 0 || hasAddedEnchant(enchantments, stack, e)) {
                        return true;
                    }
                    break;
                case ADD_TO_EXISTING_ALL:
                    if (!EnchantmentHelper.getEnchantments(stack).isEmpty() || hasAddedEnchant(enchantments, stack, null)) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    private static boolean hasAddedEnchant(Collection<AmuletEnchantment> enchantments, ItemStack stack, @Nullable Enchantment e) {
        for (AmuletEnchantment enchantment : enchantments) {
            if(enchantment.getType() != AmuletEnchantment.Type.ADD_TO_SPECIFIC) continue;
            if(e == null || (enchantment.getEnchantment().equals(e) && e.type.canEnchantItem(stack.getItem()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        player.playSound(SoundEvents.BLOCK_GLASS_PLACE, .65F, 6.4f);
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        player.playSound(SoundEvents.BLOCK_GLASS_PLACE, .65F, 6.4f);
        if (player instanceof EntityPlayerMP) {
            PlayerAmuletHandler.INSTANCE.clearAmuletTags(((EntityPlayerMP) player));
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

}
