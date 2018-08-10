/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import baubles.api.BaubleType;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.enchantment.amulet.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import hellfirepvp.astralsorcery.common.util.BaublesHelper;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

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

    public static int getNewEnchantmentLevel(int current, int currentEnchantmentId, ItemStack item) {
        if(isItemBlacklisted(item)) return current;
        return getNewEnchantmentLevel(current, currentEnchantmentId, item, null);
    }

    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item) {
        if(isItemBlacklisted(item)) return current;
        return getNewEnchantmentLevel(current, enchantment, item, null);
    }

    private static int getNewEnchantmentLevel(int current, int currentEnchantmentId, ItemStack item, @Nullable List<DynamicEnchantment> context) {
        if(isItemBlacklisted(item)) return current;
        Enchantment ench = Enchantment.getEnchantmentByID(currentEnchantmentId);
        if(ench != null) {
            return getNewEnchantmentLevel(current, ench, item, context);
        }
        return current;
    }

    private static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item, @Nullable List<DynamicEnchantment> context) {
        if(isItemBlacklisted(item)) return current;

        if(item.isEmpty() || !AmuletEnchantmentRegistry.canBeInfluenced(enchantment)) {
            return current;
        }

        List<DynamicEnchantment> modifiers = context != null ? context : fireEnchantmentGatheringEvent(item);
        for (DynamicEnchantment mod : modifiers) {
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
        if(isItemBlacklisted(stack)) return existingEnchantments;

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
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

        for (DynamicEnchantment mod : context) {
            if(mod.getType() == DynamicEnchantment.Type.ADD_TO_SPECIFIC) {
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
        if(isItemBlacklisted(stack)) return enchantmentLevelMap;

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        if(context.isEmpty()) return enchantmentLevelMap;

        Map<Enchantment, Integer> copyRet = Maps.newLinkedHashMap();
        for (Map.Entry<Enchantment, Integer> enchant : enchantmentLevelMap.entrySet()) {
            copyRet.put(enchant.getKey(), getNewEnchantmentLevel(enchant.getValue(), enchant.getKey(), stack, context));
        }

        for (DynamicEnchantment mod : context) {
            if(mod.getType() == DynamicEnchantment.Type.ADD_TO_SPECIFIC) {
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

    public static boolean isItemBlacklisted(ItemStack stack) {
        if(!stack.isEmpty()) {
            if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                return true; //We're not gonna apply enchantments to items used for querying matches
            }

            if (stack.getMaxStackSize() > 1) {
                return true; //Only swords & armor and stuff that isn't stackable
            }
            if (stack.getItem() instanceof ItemPotion) {
                return true; //We're not gonna apply enchantments to potions
            }

            ResourceLocation rl = stack.getItem().getRegistryName();
            if(rl == null) return true; //Yea... no questions asked i guess.

            if(rl.getResourceDomain().equalsIgnoreCase("draconicevolution")) {
                //Exploit with DE's item-GUI being able to draw item's enchantments while having it equipped
                //causes infinite feedback loop stacking enchantments higher and higher.
                return true;
            }
            return false;
        }
        return true;
    }

    //---------------------------------------------------
    // Data organization
    //---------------------------------------------------

    //This is more or less just a map to say whatever we add upon.
    private static List<DynamicEnchantment> fireEnchantmentGatheringEvent(ItemStack tool) {
        DynamicEnchantmentEvent.Add addEvent = new DynamicEnchantmentEvent.Add(tool);
        MinecraftForge.EVENT_BUS.post(addEvent);
        DynamicEnchantmentEvent.Modify modifyEvent = new DynamicEnchantmentEvent.Modify(tool, addEvent.getEnchantmentsToApply());
        MinecraftForge.EVENT_BUS.post(modifyEvent);
        return modifyEvent.getEnchantmentsToApply();
    }

    public static void removeAmuletTagsAndCleanup(EntityPlayer player, boolean keepEquipped) {
        InventoryPlayer inv = player.inventory;
        for (int i = 0; i < inv.mainInventory.size(); i++) {
            if (i == inv.currentItem && keepEquipped) continue;
            removeAmuletOwner(inv.mainInventory.get(i));
        }
        removeAmuletOwner(inv.getItemStack());
        if(!keepEquipped) {
            for (int i = 0; i < inv.armorInventory.size(); i++) {
                removeAmuletOwner(inv.armorInventory.get(i));
            }
            for (int i = 0; i < inv.offHandInventory.size(); i++) {
                removeAmuletOwner(inv.offHandInventory.get(i));
            }
        }
    }

    @Nullable
    private static UUID getWornPlayerUUID(ItemStack anyTool) {
        if(!anyTool.isEmpty() && anyTool.hasCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null)) {
            AmuletHolderCapability cap = anyTool.getCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null);
            if(cap != null) {
                return cap.getHolderUUID();
            }
        }
        return null;
    }

    public static void applyAmuletOwner(ItemStack tool, EntityPlayer wearer) {
        if(tool.isEmpty() || !tool.hasCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null)) return;
        AmuletHolderCapability cap = tool.getCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null);
        if(cap == null) return;
        cap.setHolderUUID(wearer.getUniqueID());
    }

    private static void removeAmuletOwner(ItemStack stack) {
        if(stack.isEmpty() || !stack.hasCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null)) {
            return;
        }
        AmuletHolderCapability cap = stack.getCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null);
        if(cap == null) return;
        cap.setHolderUUID(null);
    }

    static ItemStack getWornAmulet(ItemStack anyTool) {
        //Check if the player is online and exists & is set properly
        UUID plUUID = getWornPlayerUUID(anyTool);
        if(plUUID == null) return ItemStack.EMPTY;
        EntityPlayer player;
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            player = resolvePlayerClient(plUUID);
        } else {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if(server == null) return ItemStack.EMPTY;
            player = server.getPlayerList().getPlayerByUUID(plUUID);
        }
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

    @SideOnly(Side.CLIENT)
    private static EntityPlayer resolvePlayerClient(UUID plUUID) {
        World w = FMLClientHandler.instance().getWorldClient();
        if (w == null) return null;
        return w.getPlayerEntityByUUID(plUUID);
    }

}
