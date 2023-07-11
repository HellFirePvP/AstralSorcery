/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.LogicalSide;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentNightVision
 * Created by HellFirePvP
 * Date: 02.05.2020 / 12:38
 */
public class EnchantmentNightVision extends EnchantmentPlayerTick {

    public EnchantmentNightVision() {
        super(Rarity.VERY_RARE, EnchantmentType.ARMOR_HEAD, new EquipmentSlotType[] { EquipmentSlotType.HEAD });
    }

    @Override
    public void tick(PlayerEntity player, LogicalSide side, int level) {
        if (side.isServer()) {
            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
            if (prog.doPerkAbilities()) {
                EffectInstance effect = new EffectInstance(Effects.NIGHT_VISION, 300, level - 1, true, false);
                effect.addCurativeItem(getCurativeMarkerItem());
                player.addPotionEffect(effect);
            } else {
                player.curePotionEffects(getCurativeMarkerItem());
            }
        }
    }

    private static ItemStack getCurativeMarkerItem() {
        ItemStack curativeItem = new ItemStack(Items.AIR, 1);

        CompoundNBT nbtMarker = new CompoundNBT();
        nbtMarker.putBoolean("astralNightVision", true);

        curativeItem.setTag(nbtMarker);

        return curativeItem;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return this.type.canEnchantItem(stack.getItem());
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }
}
