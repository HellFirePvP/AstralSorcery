/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LargeBlock
 * Created by HellFirePvP
 * Date: 16.02.2020 / 08:30
 */
public interface LargeBlock {

    public AxisAlignedBB getBlockSpace();

    default public boolean canPlaceAt(BlockItemUseContext ctx) {
        BlockPos pos = ctx.getPos();
        World world = ctx.getWorld();
        AxisAlignedBB box = this.getBlockSpace();
        try (BlockPos.PooledMutable mut = BlockPos.PooledMutable.retain()) {
            for (int xx = (int) box.minX; xx <= box.maxX; xx++) {
                for (int yy = (int) box.minY; yy <= box.maxY; yy++) {
                    for (int zz = (int) box.minZ; zz <= box.maxZ; zz++) {
                        mut.setPos(pos.getX() + xx, pos.getY() + yy, pos.getZ() + zz);
                        if (!world.isAirBlock(mut) && !world.getBlockState(mut).isReplaceable(BlockItemUseContext.func_221536_a(ctx, mut, Direction.DOWN))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

}
