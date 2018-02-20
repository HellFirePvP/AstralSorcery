/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.effect.CelestialStrike;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.getEntityWorld().isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMp = (EntityPlayerMP) player;
            if(!MiscUtils.isPlayerFakeMP(playerMp) && !playerMp.getCooldownTracker().hasCooldown(ItemsAS.chargedCrystalSword)) {
                CelestialStrike.play(player, player.getEntityWorld(), Vector3.atEntityCorner(entity), Vector3.atEntityCenter(entity));
                if(!ChargedCrystalToolBase.tryRevertMainHand(playerMp, stack)) {
                    playerMp.getCooldownTracker().setCooldown(ItemsAS.chargedCrystalSword, 80);
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalSword;
    }


}
