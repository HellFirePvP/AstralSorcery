/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.entity.ItemEntityChiselAttackable;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemEntityStarmetal
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemEntityStarmetal extends ItemEntityChiselAttackable {

    public ItemEntityStarmetal(EntityType<? extends ItemEntityStarmetal> entityType, Level level) {
        super(entityType, level);
    }

    public ItemEntityStarmetal(EntityType<? extends ItemEntityStarmetal> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        super(entityType, level, posX, posY, posZ, itemStack);
    }

    public ItemEntityStarmetal(EntityType<? extends ItemEntityStarmetal> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        super(entityType, level, posX, posY, posZ, itemStack, deltaX, deltaY, deltaZ);
    }

    public static EntityType.EntityFactory<ItemEntityStarmetal> factory() {
        return ItemEntityStarmetal::new;
    }

    @Override
    public void onAttack(ServerPlayer sPlayer, ItemStack chisel) {
        if (random.nextFloat() < 0.4F) {
            ItemUtil.dropItemNaturally(this.level(), this.getX(), this.getY(), this.getZ(), ItemsAS.STARDUST.toStack());

            Holder<Enchantment> fortuneEnch = sPlayer.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FORTUNE);
            int fortune = chisel.getEnchantmentLevel(fortuneEnch);
            float ingotBreakChance = 0.9F;
            ingotBreakChance -= Mth.clamp(fortune, 0, 10) * 0.07F;
            if (random.nextFloat() < ingotBreakChance) {
                ItemStack thisStack = this.getItem();
                thisStack.shrink(1);
                this.setItem(thisStack);
            }

            chisel.hurtAndBreak(1, sPlayer.serverLevel(), sPlayer, (item) -> {
                sPlayer.onEquippedItemBroken(item, EquipmentSlot.MAINHAND);
                EventHooks.onPlayerDestroyItem(sPlayer, chisel, InteractionHand.MAIN_HAND);
            });
        } else if (random.nextFloat() < 0.6F) {
            chisel.hurtAndBreak(1, sPlayer.serverLevel(), sPlayer, (item) -> {
                sPlayer.onEquippedItemBroken(item, EquipmentSlot.MAINHAND);
                EventHooks.onPlayerDestroyItem(sPlayer, chisel, InteractionHand.MAIN_HAND);
            });
        }
    }
}
