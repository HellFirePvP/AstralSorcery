/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import baubles.api.BaubleType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.enchantment.amulet.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import hellfirepvp.astralsorcery.common.util.BaublesHelper;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentUpgradeHelper
 * Created by HellFirePvP
 * Date: 27.01.2018 / 11:27
 */
public class EnchantmentUpgradeHelper {

    public static String NBT_KEY_WEARER = "amulet-wearer";

    public static int getNewEnchantmentLevel(int current, int currentEnchantmentId, ItemStack item) {
        return getNewEnchantmentLevel(current, currentEnchantmentId, item, null);
    }

    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item) {
        return getNewEnchantmentLevel(current, enchantment, item, null);
    }

    private static int getNewEnchantmentLevel(int current, int currentEnchantmentId, ItemStack item, @Nullable List<AmuletEnchantment> context) {
        Enchantment ench = Enchantment.getEnchantmentByID(currentEnchantmentId);
        if(ench != null) {
            return getNewEnchantmentLevel(current, ench, item, context);
        }
        return current;
    }

    private static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item, @Nullable List<AmuletEnchantment> context) {
        if(item.isEmpty() || !item.hasTagCompound() || !AmuletEnchantmentRegistry.canBeInfluenced(enchantment)) {
            return current;
        }

        List<AmuletEnchantment> modifiers = context != null ? context : getEnchantmentAdditions(item);
        for (AmuletEnchantment mod : modifiers) {
            Enchantment target = mod.getEnchantment();
            switch (mod.getType()) {
                case ADD_TO_SPECIFIC:
                    if(enchantment == target) {
                        current += mod.getLevelAddition();
                    }
                    break;
                case ADD_TO_EXISTING_SPECIFIC:
                    if(enchantment == target && current > 0) {
                        current += mod.getLevelAddition();
                    }
                    break;
                case ADD_TO_EXISTING_ALL:
                    if(current > 0) {
                        current += mod.getLevelAddition();
                    }
                    break;
            }
        }
        return current;
    }

    public static NBTTagList modifyEnchantmentTags(@Nonnull NBTTagList existingEnchantments, ItemStack stack) {
        List<AmuletEnchantment> context = getEnchantmentAdditions(stack);
        if(context.isEmpty()) return existingEnchantments;

        NBTTagList returnNew = new NBTTagList();
        List<Enchantment> enchantments = new ArrayList<>(existingEnchantments.tagCount());
        for (int i = 0; i < existingEnchantments.tagCount(); i++) {
            NBTTagCompound cmp = existingEnchantments.getCompoundTagAt(i);
            int enchId = cmp.getShort("id");
            int lvl = cmp.getShort("lvl");
            int newLvl = getNewEnchantmentLevel(lvl, enchId, stack, context);

            NBTTagCompound newEnchTag = new NBTTagCompound();
            newEnchTag.setShort("id", (short) enchId);
            newEnchTag.setShort("lvl", (short) newLvl);
            returnNew.appendTag(newEnchTag);
            Enchantment e = Enchantment.getEnchantmentByID(enchId);
            if(e != null) { //If that is actually null, something went terribly wrong.
                enchantments.add(e);
            }
        }

        for (AmuletEnchantment mod : context) {
            if(mod.getType() == AmuletEnchantment.Type.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if(!AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }
                EnumEnchantmentType type = ench.type;
                if(type != null && !type.canEnchantItem(stack.getItem())) {
                    continue;
                }
                if(!enchantments.contains(ench)) { //Means we didn't add the levels on the other iteration
                    NBTTagCompound newEnchTag = new NBTTagCompound();
                    newEnchTag.setShort("id", (short) Enchantment.getEnchantmentID(ench));
                    newEnchTag.setShort("lvl", (short) getNewEnchantmentLevel(0, ench, stack, context));
                    returnNew.appendTag(newEnchTag);
                }
            }
        }
        return returnNew;
    }

    public static Map<Enchantment, Integer> applyNewEnchantmentLevels(Map<Enchantment, Integer> enchantmentLevelMap, ItemStack stack) {
        List<AmuletEnchantment> context = getEnchantmentAdditions(stack);
        if(context.isEmpty()) return enchantmentLevelMap;

        Map<Enchantment, Integer> copyRet = Maps.newLinkedHashMap();
        for (Map.Entry<Enchantment, Integer> enchant : enchantmentLevelMap.entrySet()) {
            copyRet.put(enchant.getKey(), getNewEnchantmentLevel(enchant.getValue(), enchant.getKey(), stack, context));
        }

        for (AmuletEnchantment mod : context) {
            if(mod.getType() == AmuletEnchantment.Type.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if(!AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }
                EnumEnchantmentType type = ench.type;
                if(type != null && !type.canEnchantItem(stack.getItem())) {
                    continue;
                }
                if(!enchantmentLevelMap.containsKey(ench)) { //Means we didn't add the levels on the other iteration
                    copyRet.put(ench, getNewEnchantmentLevel(0, ench, stack, context));
                }
            }
        }
        return copyRet;
    }

    //---------------------------------------------------
    // Data organization
    //---------------------------------------------------

    //This is more or less just a map to say whatever we add upon.
    private static List<AmuletEnchantment> getEnchantmentAdditions(ItemStack tool) {
        ItemStack linkedAmulet = getWornAmulet(tool);
        if(linkedAmulet.isEmpty()) return Lists.newArrayList();

        return ItemEnchantmentAmulet.getAmuletEnchantments(linkedAmulet);
    }

    public static boolean hasAmuletTag(ItemStack anyTool) {
        if(anyTool.isEmpty() || !anyTool.hasTagCompound()) {
            return false;
        }
        NBTTagCompound tag = NBTHelper.getPersistentData(anyTool);
        return tag.hasUniqueId(NBT_KEY_WEARER);
    }

    @Nullable
    public static UUID getWornPlayerUUID(ItemStack anyTool) {
        if(!hasAmuletTag(anyTool)) return null;
        return NBTHelper.getPersistentData(anyTool).getUniqueId(NBT_KEY_WEARER);
    }

    public static void applyAmuletTag(ItemStack tool, EntityPlayer wearer) {
        if(tool.isEmpty()) return;
        UUID playerUUID = wearer.getUniqueID();
        NBTTagCompound tag = NBTHelper.getPersistentData(tool);
        tag.setUniqueId(NBT_KEY_WEARER, playerUUID);
    }

    private static ItemStack getWornAmulet(ItemStack anyTool) {
        if(!hasAmuletTag(anyTool)) return ItemStack.EMPTY;

        //Check if the player is online and exists & is set properly
        UUID plUUID = getWornPlayerUUID(anyTool);
        if(plUUID == null) return ItemStack.EMPTY;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(server == null) return ItemStack.EMPTY;
        EntityPlayer player = server.getPlayerList().getPlayerByUUID(plUUID);
        if(player == null) return ItemStack.EMPTY;

        //Check if the player actually wears/carries the tool
        boolean foundTool = false;
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            if(ItemUtils.matchStacksStrict(stack, anyTool)) {
                foundTool = true;
                break;
            }
        }
        if(!foundTool) return ItemStack.EMPTY;

        //Check if the player wears an amulet and return that one then..
        if(BaublesHelper.doesPlayerWearBauble(player, BaubleType.AMULET, (stack) -> !stack.isEmpty() && stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return BaublesHelper.getFirstWornBaublesForType(player, BaubleType.AMULET);
        }
        return ItemStack.EMPTY;
    }

}
