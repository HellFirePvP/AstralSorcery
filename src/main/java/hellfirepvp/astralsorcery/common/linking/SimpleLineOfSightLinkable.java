/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking;

import hellfirepvp.astralsorcery.common.util.RayTraceUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SimpleLineOfSightLinkable
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface SimpleLineOfSightLinkable extends BlockLinkable {

    @Override
    default LinkResult canLinkTo(Level level, BlockPos to) {
        LinkResult canLink = BlockLinkable.super.canLinkTo(level, to);
        if (!canLink.isSuccess()) {
            return canLink;
        }
        if (this.ignoreBlockCollisionForLinks()) {
            return LinkResult.success();
        }
        if (!this.hasLineOfSight(level, to)) {
            return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.line_of_sight").withStyle(ChatFormatting.RED));
        }
        return LinkResult.success();
    }

    default boolean hasLineOfSight(Level level, BlockPos to) {
        ClipContext ctx = new ClipContext(this.getLinkablePos().getCenter(), to.getCenter(),
                ClipContext.Block.VISUAL, ClipContext.Fluid.WATER,
                CollisionContext.empty());
        return RayTraceUtil.clip(level, ctx, Set.of(this.getLinkablePos(), to)).getType() == HitResult.Type.MISS;
    }

    default boolean ignoreBlockCollisionForLinks() {
        return false;
    }
}
