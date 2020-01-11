/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OverrideInteractItem
 * Created by HellFirePvP
 * Date: 24.08.2019 / 16:35
 */
public interface OverrideInteractItem {

    boolean shouldInterceptInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face);

    boolean doInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face);

}
