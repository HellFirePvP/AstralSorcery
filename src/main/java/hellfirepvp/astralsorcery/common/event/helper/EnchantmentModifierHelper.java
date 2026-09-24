/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import hellfirepvp.astralsorcery.common.Mods;
import hellfirepvp.astralsorcery.common.component.EnchantmentModifierComponent;
import hellfirepvp.astralsorcery.common.component.WeakPlayerReferenceComponent;
import hellfirepvp.astralsorcery.common.enchantment.CombinedEnchantmentModifiers;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.integration.IntegrationCurios;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnchantmentModifierHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EnchantmentModifierHelper {

    private static final ResourceLocation AIR_ID = ResourceLocation.withDefaultNamespace("air");

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EnchantmentModifierHelper::onPlayerTick);
        bus.addListener(EnchantmentModifierHelper::onEnchantmentAdd);
    }

    private static void onPlayerTick(PlayerTickEvent.Pre event) {
        UUID playerId = event.getEntity().getUUID();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = event.getEntity().getItemBySlot(slot);
            if (!stack.isEmpty() && canHaveModifiedEnchantments(stack)) {
                stack.set(DataComponentsAS.WEAK_PLAYER_REFERENCE, new WeakPlayerReferenceComponent(playerId));
            }
        }
    }

    private static void onEnchantmentAdd(GetEnchantmentLevelEvent event) {
        ItemEnchantments.Mutable enchants = event.getEnchantments();
        addEnchantments(enchants, event.getStack(), event::isTargetting);
    }

    public static ItemEnchantments addEnchantments(ItemStack hostStack, ItemEnchantments enchants) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchants);
        addEnchantments(mutable, hostStack);
        return mutable.toImmutable();
    }

    public static void addEnchantments(ItemEnchantments.Mutable enchants, ItemStack hostStack) {
        addEnchantments(enchants, hostStack, ench -> true);
    }

    public static void addEnchantments(ItemEnchantments.Mutable enchants, ItemStack hostStack, Predicate<Holder<Enchantment>> filter) {
        CombinedEnchantmentModifiers toAdd = getModifiersToApply(hostStack);

        toAdd.getAddedToSpecific().forEach((ench, level) -> {
            if (filter.test(ench)) {
                enchants.set(ench, enchants.getLevel(ench) + level);
            }
        });
        toAdd.getAddedToExistingSpecific().forEach((ench, level) -> {
            if (filter.test(ench)) {
                int existing = enchants.getLevel(ench);
                if (existing > 0) {
                    enchants.set(ench, existing + level);
                }
            }
        });
        if (toAdd.getAddedToAll() != 0) {
            for (Holder<Enchantment> ench : enchants.keySet()) {
                int existing = enchants.getLevel(ench);
                if (existing > 0) {
                    enchants.set(ench, existing + toAdd.getAddedToAll());
                }
            }
        }
    }

    private static CombinedEnchantmentModifiers getModifiersToApply(ItemStack tool) {
        return getPlayer(tool).map(player -> {
            DynamicEnchantmentEvent.Add createEvent = new DynamicEnchantmentEvent.Add(player);
            NeoForge.EVENT_BUS.post(createEvent);
            CombinedEnchantmentModifiers modifiers = createEvent.getDynamicEnchantments().build();

            for (ItemStack modifierStack : findEnchantmentModifiers(player)) {
                EnchantmentModifierComponent mod = modifierStack.getOrDefault(DataComponentsAS.ENCHANTMENT_MODIFIERS, EnchantmentModifierComponent.EMPTY);
                modifiers = modifiers.combine(mod.combined());
            }

            DynamicEnchantmentEvent.Modify modifyEvent = new DynamicEnchantmentEvent.Modify(player, modifiers);
            NeoForge.EVENT_BUS.post(modifyEvent);
            return modifyEvent.getDynamicEnchantments().build();
        }).orElse(CombinedEnchantmentModifiers.of());
    }

    private static Optional<Player> getPlayer(ItemStack heldTool) {
        return getPlayerFromToolReference(heldTool).map(player -> {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack equipped = player.getItemBySlot(slot);
                if (ItemStack.isSameItemSameComponents(equipped, heldTool)) {
                    return player;
                }
            }
            return null;
        });
    }

    private static Optional<Player> getPlayerFromToolReference(ItemStack tool) {
        if (!tool.has(DataComponentsAS.WEAK_PLAYER_REFERENCE)) {
            return Optional.empty();
        }

        UUID playerId = tool.getOrDefault(DataComponentsAS.WEAK_PLAYER_REFERENCE, WeakPlayerReferenceComponent.NONE).playerUUID();

        //Try server first, then client world lookup
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        if (srv != null) {
            return Optional.ofNullable(srv.getPlayerList().getPlayer(playerId));
        } else {
            return LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).map(level -> {
                return level.getPlayerByUUID(playerId);
            });
        }
    }

    private static List<ItemStack> findEnchantmentModifiers(Player player) {
        List<ItemStack> foundEnchantmentModifierItems = new ArrayList<>();
        Predicate<ItemStack> filter = stack -> {
            //TODO specific slot filters
            return !stack.getOrDefault(DataComponentsAS.ENCHANTMENT_MODIFIERS, EnchantmentModifierComponent.EMPTY).isEmpty();
        };

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HAND) continue;

            ItemStack stack = player.getItemBySlot(slot);
            if (filter.test(stack)) {
                foundEnchantmentModifierItems.add(stack);
            }
        }

        foundEnchantmentModifierItems.addAll(IntegrationCurios.getCurio(player, filter));
        return foundEnchantmentModifierItems;
    }

    private static boolean canHaveModifiedEnchantments(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id.equals(AIR_ID)) return false;
        try {
            if (!stack.isEnchantable() || stack.getItem() instanceof BookItem) {
                return false;
            }
        } catch (Exception exc) {
            return false;
        }
        if (id.getNamespace().equals(Mods.DRACONIC_EVOLUTION.getModId())) {
            return false;
        }

        return true;
    }
}
