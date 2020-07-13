/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.dynamic;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletEnchantmentHelper;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicEnchantmentHelper
 * Created by HellFirePvP
 * Date: 11.08.2019 / 19:49
 */
public class DynamicEnchantmentHelper {

    private static int getNewEnchantmentLevel(int current, String enchStr, ItemStack item, @Nullable List<DynamicEnchantment> context) {
        Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchStr));
        if (ench != null) {
            return getNewEnchantmentLevel(current, ench, item, context);
        }
        return current;
    }

    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item, @Nullable List<DynamicEnchantment> context) {
        if (!canHaveDynamicEnchantment(item)) {
            return current;
        }
        if (enchantment == null || !AmuletEnchantmentRegistry.canBeInfluenced(enchantment)) {
            return current;
        }

        List<DynamicEnchantment> modifiers = context != null ? context : fireEnchantmentGatheringEvent(item);
        for (DynamicEnchantment mod : modifiers) {
            Enchantment target = mod.getEnchantment();
            switch (mod.getType()) {
                case ADD_TO_SPECIFIC:
                    if (enchantment.equals(target)) {
                        current += mod.getLevelAddition();
                    }
                    break;
                case ADD_TO_EXISTING_SPECIFIC:
                    if (enchantment.equals(target) && current > 0) {
                        current += mod.getLevelAddition();
                    }
                    break;
                case ADD_TO_EXISTING_ALL:
                    if (current > 0) {
                        current += mod.getLevelAddition();
                    }
                    break;
                default:
                    break;
            }
        }
        return current;
    }

    public static ListNBT modifyEnchantmentTags(ListNBT existingEnchantments, ItemStack stack) {
        if (!canHaveDynamicEnchantment(stack)) {
            return existingEnchantments;
        }

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        if (context.isEmpty()) {
            return existingEnchantments;
        }

        ListNBT returnNew = new ListNBT();
        Set<String> enchantments = new HashSet<>(existingEnchantments.size());
        for (int i = 0; i < existingEnchantments.size(); i++) {
            CompoundNBT cmp = existingEnchantments.getCompound(i);
            String enchKey = cmp.getString("id");
            int lvl = cmp.getInt("lvl");
            int newLvl = getNewEnchantmentLevel(lvl, enchKey, stack, context);

            CompoundNBT newEnchTag = new CompoundNBT();
            newEnchTag.putString("id", enchKey);
            newEnchTag.putInt("lvl", newLvl);
            returnNew.add(newEnchTag);

            enchantments.add(enchKey);
        }

        for (DynamicEnchantment mod : context) {
            if (mod.getType() == DynamicEnchantmentType.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if (ench == null || !AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }

                if (!stack.canApplyAtEnchantingTable(ench)) {
                    continue;
                }
                String enchName = ench.getRegistryName().toString();
                if (!enchantments.contains(enchName)) { //Means we didn't add the levels on the other iteration
                    CompoundNBT newEnchTag = new CompoundNBT();
                    newEnchTag.putString("id", enchName);
                    newEnchTag.putInt("lvl", getNewEnchantmentLevel(0, ench, stack, context));
                    returnNew.add(newEnchTag);
                }
            }
        }
        return returnNew;
    }

    public static Map<Enchantment, Integer> addNewLevels(Map<Enchantment, Integer> enchantmentLevelMap, ItemStack stack) {
        if (!canHaveDynamicEnchantment(stack)) {
            return enchantmentLevelMap;
        }

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        if (context.isEmpty()) {
            return enchantmentLevelMap;
        }

        Map<Enchantment, Integer> copyRet = Maps.newLinkedHashMap();
        for (Map.Entry<Enchantment, Integer> enchant : enchantmentLevelMap.entrySet()) {
            copyRet.put(enchant.getKey(), getNewEnchantmentLevel(enchant.getValue(), enchant.getKey(), stack, context));
        }

        for (DynamicEnchantment mod : context) {
            if (mod.getType() == DynamicEnchantmentType.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if (ench == null || !AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }
                EnchantmentType type = ench.type;
                if (type != null && !type.canEnchantItem(stack.getItem())) {
                    continue;
                }
                if (!enchantmentLevelMap.containsKey(ench)) { //Means we didn't add the levels on the other iteration
                    copyRet.put(ench, getNewEnchantmentLevel(0, ench, stack, context));
                }
            }
        }
        return copyRet;
    }

    public static boolean canHaveDynamicEnchantment(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item i = stack.getItem();
        if (i.getRegistryName() == null) {
            return false;
        }
        if (!i.isEnchantable(stack) || !stack.isDamageable()) {
            return false;
        }
        if (Mods.DRACONIC_EVOLUTION.owns(stack.getItem())) {
            return false;
        }
        return true;
    }

    //This is more or less just a map to say whatever we add upon.
    private static List<DynamicEnchantment> fireEnchantmentGatheringEvent(ItemStack tool) {
        PlayerEntity foundEntity = AmuletEnchantmentHelper.getPlayerHavingTool(tool);
        if (foundEntity == null) {
            return new ArrayList<>();
        }
        DynamicEnchantmentEvent.Add addEvent = new DynamicEnchantmentEvent.Add(tool, foundEntity);
        if (MinecraftForge.EVENT_BUS.post(addEvent)) {
            return new ArrayList<>();
        }
        DynamicEnchantmentEvent.Modify modifyEvent = new DynamicEnchantmentEvent.Modify(tool, addEvent.getEnchantmentsToApply(), foundEntity);
        if (MinecraftForge.EVENT_BUS.post(modifyEvent)) {
            return new ArrayList<>();
        }
        return modifyEvent.getEnchantmentsToApply();
    }

}
