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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalSword
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:43
 */
public class ItemChargedCrystalSword extends ItemCrystalSword implements ChargedCrystalToolBase {

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.getEntityWorld().isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMp = (EntityPlayerMP) player;
            if(!MiscUtils.isPlayerFakeMP(playerMp) && !playerMp.getCooldownTracker().hasCooldown(ItemsAS.chargedCrystalSword)) {
                CelestialStrike.play(player, player.getEntityWorld(), new Vector3(entity), new Vector3(entity, true));
                if(!ChargedCrystalToolBase.tryRevertMainHand(playerMp, stack)) {
                    playerMp.getCooldownTracker().setCooldown(ItemsAS.chargedCrystalSword, 80);
                }
            }
        }
        return false;
    }

    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalSword;
    }

}
