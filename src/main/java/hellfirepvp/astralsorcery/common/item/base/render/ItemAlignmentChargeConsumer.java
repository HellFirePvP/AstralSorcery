/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base.render;

import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAlignmentChargeConsumer
 * Created by HellFirePvP
 * Date: 27.12.2016 / 21:52
 */
public interface ItemAlignmentChargeConsumer extends ItemAlignmentChargeRevealer {

    default public boolean drainTempCharge(EntityPlayer player, float charge, boolean simulate) {
        if(player.isCreative()) return true;

        if(!PlayerChargeHandler.INSTANCE.hasAtLeast(player, charge)) {
            return false;
        }
        if(!simulate) {
            PlayerChargeHandler.INSTANCE.drainCharge(player, charge);
        }
        return true;
    }

}
