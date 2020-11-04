package hellfirepvp.astralsorcery.common.block.tile.fountain;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.block.tile.BlockFountain;
import hellfirepvp.astralsorcery.common.crafting.nojson.fountain.FountainEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFountainPrime
 * Created by HellFirePvP
 * Date: 31.10.2020 / 16:32
 */
public abstract class BlockFountainPrime extends Block implements CustomItemBlock {

    public BlockFountainPrime() {
        super(PropertiesMarble.defaultMarble()
                .notSolid());
    }

    @Nonnull
    public abstract FountainEffect provideEffect();

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction placedAgainst, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        if (!this.isValidPosition(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return world.getBlockState(pos.up()).getBlock() instanceof BlockFountain;
    }
}
