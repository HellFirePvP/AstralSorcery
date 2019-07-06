/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.marble;

import hellfirepvp.astralsorcery.common.block.base.template.BlockMarbleTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarblePillar
 * Created by HellFirePvP
 * Date: 01.06.2019 / 12:41
 */
public class BlockMarblePillar extends BlockMarbleTemplate {

    public static EnumProperty<PillarType> MARBLE_TYPE = EnumProperty.create("marbletype", PillarType.class);

    public BlockMarblePillar() {
        this.setDefaultState(this.getStateContainer().getBaseState().with(MARBLE_TYPE, PillarType.MIDDLE));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(MARBLE_TYPE);
    }

    @Override
    public BlockState updatePostPlacement(BlockState thisState, Direction otherBlockFacing, BlockState otherBlockState, IWorld world, BlockPos thisPos, BlockPos otherBlockPos) {
        return this.getThisState(world, thisPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.getThisState(ctx.getWorld(), ctx.getPos());
    }

    private BlockState getThisState(IBlockReader world, BlockPos pos) {
        boolean hasUp   = world.getBlockState(pos.up()).getBlock()   instanceof BlockMarblePillar;
        boolean hasDown = world.getBlockState(pos.down()).getBlock() instanceof BlockMarblePillar;
        if (hasUp) {
            if (hasDown) {
                return this.getDefaultState().with(MARBLE_TYPE, PillarType.MIDDLE);
            }
            return this.getDefaultState().with(MARBLE_TYPE, PillarType.BOTTOM);
        } else if (hasDown) {
            return this.getDefaultState().with(MARBLE_TYPE, PillarType.TOP);
        }
        return this.getDefaultState().with(MARBLE_TYPE, PillarType.MIDDLE);
    }

    public static enum PillarType implements IStringSerializable {

        TOP,
        MIDDLE,
        BOTTOM;

        public boolean obtainableInCreative() {
            return this != TOP && this != BOTTOM;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
