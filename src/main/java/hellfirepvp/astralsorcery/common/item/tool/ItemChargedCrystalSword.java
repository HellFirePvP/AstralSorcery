/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.entities.EntityStarburst;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalSword
 * Created by HellFirePvP
 * Date: 12.03.2017 / 10:45
 */
public class ItemChargedCrystalSword extends ItemCrystalSword implements ChargedCrystalToolBase {

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (isSelected && !worldIn.isRemote && entityIn instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entityIn;
            if(!MiscUtils.isPlayerFakeMP(player) && player.swingProgressInt > 0 && !player.getCooldownTracker().hasCooldown(ItemsAS.chargedCrystalSword)) {
                int swingEndTick = player.isPotionActive(MobEffects.HASTE) ? 6 - (1 + player.getActivePotionEffect(MobEffects.HASTE).getAmplifier()) : (player.isPotionActive(MobEffects.MINING_FATIGUE) ? 6 + (1 + player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6);
                swingEndTick -= 1;
                if(player.swingProgressInt == swingEndTick) {
                    worldIn.spawnEntity(new EntityStarburst(worldIn, player));
                    if(!ChargedCrystalToolBase.tryRevertMainHand(player, stack)) {
                        player.getCooldownTracker().setCooldown(ItemsAS.chargedCrystalSword, 70);
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalSword;
    }


}
