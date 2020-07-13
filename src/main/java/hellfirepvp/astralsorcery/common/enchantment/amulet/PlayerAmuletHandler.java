/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import hellfirepvp.astralsorcery.common.event.DynamicEnchantmentEvent;
import hellfirepvp.astralsorcery.common.item.ItemEnchantmentAmulet;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.TickEvent;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerAmuletHandler
 * Created by HellFirePvP
 * Date: 11.08.2019 / 20:24
 */
public class PlayerAmuletHandler implements ITickHandler {

    public static final PlayerAmuletHandler INSTANCE = new PlayerAmuletHandler();

    private PlayerAmuletHandler() {}

    public static void onEnchantmentAdd(DynamicEnchantmentEvent.Add event) {
        if (!DynamicEnchantmentHelper.canHaveDynamicEnchantment(event.getEnchantedItemStack())) {
            return;
        }

        Tuple<ItemStack, PlayerEntity> linkedAmulet = AmuletEnchantmentHelper.getWornAmulet(event.getEnchantedItemStack());
        if (linkedAmulet == null ||
                linkedAmulet.getA().isEmpty() ||
                linkedAmulet.getB() == null) {
            return;
        }

        event.getEnchantmentsToApply().addAll(ItemEnchantmentAmulet.getAmuletEnchantments(linkedAmulet.getA()));
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        PlayerEntity player = (PlayerEntity) context[0];
        applyAmuletTags(player);
        clearAmuletTags(player);
    }

    private void applyAmuletTags(PlayerEntity player) {
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            AmuletEnchantmentHelper.applyAmuletOwner(player.getItemStackFromSlot(slot), player);
        }
    }

    private void clearAmuletTags(PlayerEntity player) {
        AmuletEnchantmentHelper.removeAmuletTagsAndCleanup(player, true);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "PlayerAmuletHandler";
    }
}
