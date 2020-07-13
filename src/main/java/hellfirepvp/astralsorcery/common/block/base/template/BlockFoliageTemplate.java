/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base.template;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.IPlantable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFoliageTemplate
 * Created by HellFirePvP
 * Date: 21.07.2019 / 09:21
 */
public abstract class BlockFoliageTemplate extends Block implements CustomItemBlock, IPlantable {

    public BlockFoliageTemplate(Block.Properties properties) {
        super(properties);
    }

    protected abstract boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos);

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        if (!state.isValidPosition(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.updatePostPlacement(state, dir, facingState, world, pos, facingPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        BlockPos blockpos = pos.down();
        if (state.getBlock() == this) {
            return world.getBlockState(blockpos).canSustainPlant(world, blockpos, Direction.UP, this);
        }
        return this.isValidGround(world.getBlockState(blockpos), world, blockpos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }

    @Override
    public BlockState getPlant(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return this.getDefaultState();
        }
        return state;
    }
}
