/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InterceptInteractItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class InterceptInteractItem {

    private InterceptInteractItem() {}

    public interface Block {

        boolean shouldInterceptBlockInteract(LogicalSide side, Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult, Direction blockFace);

        //Return true to intercept further interactions
        boolean doBlockInteract(LogicalSide side, Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult, Direction blockFace);

    }

    public interface Entity<T extends net.minecraft.world.entity.Entity> {

        Class<T> getEntityFilterClass();

        boolean shouldInterceptEntityInteract(LogicalSide side, Player player, InteractionHand hand, T interacted);

        //Return true to intercept further interactions
        boolean doEntityInteract(LogicalSide side, Player player, InteractionHand hand, T interacted);

    }
}
