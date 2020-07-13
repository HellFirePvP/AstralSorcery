/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletEnchantment;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletRandomizeHelper;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemEnchantmentAmulet
 * Created by HellFirePvP
 * Date: 11.08.2019 / 20:07
 */
public class ItemEnchantmentAmulet extends Item implements ItemDynamicColor {

    private static Random rand = new Random();

    public ItemEnchantmentAmulet() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        List<AmuletEnchantment> enchantments = getAmuletEnchantments(stack);
        for (AmuletEnchantment ench : enchantments) {
            tooltip.add(new StringTextComponent(ench.getDisplay()).setStyle(new Style().setColor(TextFormatting.BLUE)));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote() && !getAmuletColor(stack).isPresent()) {
            freezeAmuletColor(stack);
        }
        if (!worldIn.isRemote() && getAmuletEnchantments(stack).isEmpty()) {
            AmuletRandomizeHelper.rollAmulet(stack);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) {
            return 0xFFFFFFFF;
        }
        Optional<Integer> color = getAmuletColor(stack);
        if (color.isPresent()) {
            return color.get();
        }
        int tick = (int) (ClientScheduler.getClientTick() % 500000L);
        int c = Color.getHSBColor((tick / 500000F) * 360F, 0.7F, 1F).getRGB();
        return c | 0xFF000000;
    }

    public static Optional<Integer> getAmuletColor(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return Optional.empty();
        }
        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (!tag.contains("amuletColor")) {
            return Optional.empty();
        }
        return Optional.of(tag.getInt("amuletColor"));
    }

    public static void freezeAmuletColor(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }
        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (tag.contains("amuletColor")) {
            return;
        }
        if (rand.nextInt(400) == 0) {
            tag.putInt("amuletColor", 0xFFFFFFFF);
        } else {
            float hue = rand.nextFloat() * 360F;
            tag.putInt("amuletColor", Color.getHSBColor(hue, 0.7F, 1.0F).getRGB() | 0xFF000000);
        }
    }

    public static List<AmuletEnchantment> getAmuletEnchantments(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return Lists.newArrayList();
        }

        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (!tag.contains("amuletEnchantments")) {
            return Lists.newArrayList();
        }
        ListNBT enchants = tag.getList("amuletEnchantments", Constants.NBT.TAG_COMPOUND);
        List<AmuletEnchantment> enchantments = new ArrayList<>(enchants.size());
        for (int i = 0; i < enchants.size(); i++) {
            AmuletEnchantment ench = AmuletEnchantment.deserialize(enchants.getCompound(i));
            if (ench != null) {
                enchantments.add(ench);
            }
        }
        enchantments.sort(Comparator.comparing(AmuletEnchantment::getType));
        return enchantments;
    }

    public static void setAmuletEnchantments(ItemStack stack, List<AmuletEnchantment> enchantments) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }
        enchantments.sort(Comparator.comparing(AmuletEnchantment::getType));

        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        ListNBT enchants = new ListNBT();
        for (AmuletEnchantment enchant : enchantments) {
            enchants.add(enchant.serialize());
        }
        tag.put("amuletEnchantments", enchants);
    }
}
