/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.entity.ItemEntityChiselAttackable;
import hellfirepvp.astralsorcery.common.item.crystal.RockCrystalItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemEntityCrystal
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemEntityCrystal extends ItemEntityChiselAttackable {

    public ItemEntityCrystal(EntityType<? extends ItemEntityCrystal> entityType, Level level) {
        super(entityType, level);
    }

    public ItemEntityCrystal(EntityType<? extends ItemEntityCrystal> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        super(entityType, level, posX, posY, posZ, itemStack);
    }

    public ItemEntityCrystal(EntityType<? extends ItemEntityCrystal> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        super(entityType, level, posX, posY, posZ, itemStack, deltaX, deltaY, deltaZ);
    }

    public static EntityType.EntityFactory<ItemEntityCrystal> factory() {
        return ItemEntityCrystal::new;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypeTags.IS_EXPLOSION) && super.hurt(source, amount);
    }

    @Override
    public void onAttack(ServerPlayer sPlayer, ItemStack chisel) {
        ItemStack crystalStack = this.getItem();
        if (!(crystalStack.getItem() instanceof RockCrystalItem thisItem)) {
            return;
        }
        CrystalAttributesComponent thisAttributes = crystalStack.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
        if (thisAttributes.isEmpty()) {
            return;
        }

        boolean didSplit = false;
        if (random.nextFloat() < 0.5F) {
            Holder<Enchantment> fortune = sPlayer.serverLevel().holderOrThrow(Enchantments.FORTUNE);
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(fortune, sPlayer);
            didSplit = this.splitCrystal(thisItem, thisAttributes, fortuneLevel);
        }
        if (didSplit || random.nextFloat() < 0.5F) {
            chisel.hurtAndBreak(1, sPlayer.serverLevel(), sPlayer, (item) -> {
                sPlayer.onEquippedItemBroken(item, EquipmentSlot.MAINHAND);
                EventHooks.onPlayerDestroyItem(sPlayer, chisel, InteractionHand.MAIN_HAND);
            });
        }
    }

    protected boolean splitCrystal(RockCrystalItem thisItem, CrystalAttributesComponent thisAttributes, int fortuneLevel) {
        RockCrystalItem splitCrystalItem = thisItem.getCrystalSplitItem();
        ItemStack splitCrystal = new ItemStack(splitCrystalItem);
        if (splitCrystal.isEmpty()) {
            return false;
        }
        int maxSplit = Mth.ceil(thisAttributes.getTotalTierCount() / 2F);
        if (maxSplit >= thisAttributes.getTotalTierCount()) {
            return false;
        }
        int lostModifiers = 0;
        if (maxSplit > 1 && this.getRandom().nextFloat() < (0.9F / (fortuneLevel + 1))) {
            lostModifiers++;
            if (maxSplit > 2 && this.getRandom().nextFloat() < (0.6F / (fortuneLevel + 1))) {
                lostModifiers++;
            }
        }

        CrystalAttributesComponent.GenerationProperties props = thisAttributes.getProperties();
        CrystalAttributesComponent newAttributes = CrystalAttributesComponent.empty(props.generateCount(), props.maxTierCount());

        for (int i = 0; i < maxSplit; i++) {
            CrystalAttributesComponent.TieredAttribute attr = MiscUtil.getRandomEntry(thisAttributes.getAttributes(), this.getRandom()).orElse(null);
            if (attr == null) {
                break;
            }

            thisAttributes = thisAttributes.setAttributeTier(attr, attr.getTier() - 1);
            if (lostModifiers > 0) {
                lostModifiers--;
            } else {
                newAttributes = newAttributes.setAttributeTier(attr, newAttributes.getAttributeTier(attr) + 1);
            }
        }

        ItemStack thisStack = this.getItem();
        thisStack.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, thisAttributes);

        splitCrystal.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, newAttributes);
        ItemUtil.dropItemNaturally(this.level(), this.getX(), this.getY() + 0.25F, this.getZ(), splitCrystal);
        return true;
    }
}
