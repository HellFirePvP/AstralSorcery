/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import hellfirepvp.astralsorcery.common.integration.IntegrationCurios;
import hellfirepvp.astralsorcery.common.item.ItemEnchantmentAmulet;
import hellfirepvp.astralsorcery.common.util.item.ItemComparator;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletEnchantmentHelper
 * Created by HellFirePvP
 * Date: 11.08.2019 / 20:25
 */
public class AmuletEnchantmentHelper {

    public static final String KEY_AS_OWNER = "AS_Amulet_Holder";

    public static void removeAmuletTagsAndCleanup(PlayerEntity player, boolean keepEquipped) {
        PlayerInventory inv = player.inventory;
        for (int i = 0; i < inv.mainInventory.size(); i++) {
            if (i == inv.currentItem && keepEquipped) {
                continue;
            }
            removeAmuletOwner(inv.mainInventory.get(i));
        }
        removeAmuletOwner(inv.getItemStack());
        if (!keepEquipped) {
            for (int i = 0; i < inv.armorInventory.size(); i++) {
                removeAmuletOwner(inv.armorInventory.get(i));
            }
            for (int i = 0; i < inv.offHandInventory.size(); i++) {
                removeAmuletOwner(inv.offHandInventory.get(i));
            }
        }
    }

    @Nonnull
    private static UUID getWornPlayerUUID(ItemStack anyTool) {
        if (DynamicEnchantmentHelper.canHaveDynamicEnchantment(anyTool) && anyTool.hasTag()) {
            return anyTool.getTag().getUniqueId(KEY_AS_OWNER);
        }
        return new UUID(0, 0);
    }

    public static void applyAmuletOwner(ItemStack tool, PlayerEntity wearer) {
        if (DynamicEnchantmentHelper.canHaveDynamicEnchantment(tool)) {
            tool.getOrCreateTag().putUniqueId(KEY_AS_OWNER, wearer.getUniqueID());
        }
    }

    private static void removeAmuletOwner(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return;
        }
        NBTHelper.removeUUID(stack.getTag(), KEY_AS_OWNER);
        if (stack.getTag().isEmpty()) {
            stack.setTag(null);
        }
    }

    @Nullable
    public static PlayerEntity getPlayerHavingTool(ItemStack anyTool) {
        UUID plUUID = getWornPlayerUUID(anyTool);
        if (plUUID.getLeastSignificantBits() == 0 && plUUID.getMostSignificantBits() == 0) {
            return null;
        }
        PlayerEntity player;
        if (EffectiveSide.get() == LogicalSide.CLIENT) {
            player = resolvePlayerClient(plUUID);
        } else {
            MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
            if (server == null) {
                return null;
            }
            player = server.getPlayerList().getPlayerByUUID(plUUID);
        }
        if (player == null) {
            return null;
        }

        //Check if the player actually wears/carries the tool
        boolean foundTool = false;
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (ItemComparator.compare(stack, anyTool, ItemComparator.Clause.Sets.ITEMSTACK_STRICT)) {
                foundTool = true;
                break;
            }
        }
        if (!foundTool) return null;

        return player;
    }

    @Nullable
    static Tuple<ItemStack, PlayerEntity> getWornAmulet(ItemStack anyTool) {
        PlayerEntity player = getPlayerHavingTool(anyTool);
        if (player == null) return null;

        Optional<ImmutableTriple<String, Integer, ItemStack>> curios =
                IntegrationCurios.getCurio(player, (stack) -> stack.getItem() instanceof ItemEnchantmentAmulet);
        return curios.map(trpl -> new Tuple<>(trpl.right, player)).orElse(null);
    }

    @OnlyIn(Dist.CLIENT)
    private static PlayerEntity resolvePlayerClient(UUID plUUID) {
        Optional<World> w = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT);
        return w.map(world -> world.getPlayerByUuid(plUUID)).orElse(null);
    }

}
