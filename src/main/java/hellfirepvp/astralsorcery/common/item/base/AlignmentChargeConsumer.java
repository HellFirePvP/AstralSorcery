/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AlignmentChargeConsumer
 * Created by HellFirePvP
 * Date: 08.03.2020 / 20:13
 */
public interface AlignmentChargeConsumer extends AlignmentChargeRevealer {

    float getAlignmentChargeCost(PlayerEntity player, ItemStack stack);

}
