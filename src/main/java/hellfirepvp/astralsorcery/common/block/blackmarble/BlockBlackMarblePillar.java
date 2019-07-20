/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.blackmarble;

import hellfirepvp.astralsorcery.common.block.base.template.BlockBlackMarbleTemplate;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBlackMarblePillar
 * Created by HellFirePvP
 * Date: 20.07.2019 / 19:49
 */
public class BlockBlackMarblePillar extends BlockBlackMarbleTemplate {

    public static final EnumProperty<PillarType> PILLAR_TYPE = EnumProperty.create("pillartype", PillarType.class);

    private final VoxelShape middleShape, bottomShape, topShape;

    public BlockBlackMarblePillar() {
        this.setDefaultState(this.getStateContainer().getBaseState().with(PILLAR_TYPE, PillarType.MIDDLE));
        this.middleShape = createPillarShape();
        this.topShape    = createPillarTopShape();
        this.bottomShape = createPillarBottomShape();
    }

    protected VoxelShape createPillarShape() {
        return Block.makeCuboidShape(2, 0, 2, 14, 16, 14);
    }

    protected VoxelShape createPillarTopShape() {
        VoxelShape column = Block.makeCuboidShape(2, 0, 2, 14, 12, 14);
        VoxelShape top = Block.makeCuboidShape(0, 12, 0, 16, 16, 16);

        return VoxelUtils.combineAll(IBooleanFunction.OR,
                column, top);
    }

    protected VoxelShape createPillarBottomShape() {
        VoxelShape column = Block.makeCuboidShape(2, 4, 2, 14, 16, 14);
        VoxelShape bottom = Block.makeCuboidShape(0, 0, 0, 16, 4, 16);

        return VoxelUtils.combineAll(IBooleanFunction.OR,
                column, bottom);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(PILLAR_TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        switch (state.get(PILLAR_TYPE)) {
            case TOP:
                return this.topShape;
            case BOTTOM:
                return this.bottomShape;
            default:
            case MIDDLE:
                return this.middleShape;
        }
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
        boolean hasUp   = world.getBlockState(pos.up()).getBlock()   instanceof BlockBlackMarblePillar;
        boolean hasDown = world.getBlockState(pos.down()).getBlock() instanceof BlockBlackMarblePillar;
        if (hasUp) {
            if (hasDown) {
                return this.getDefaultState().with(PILLAR_TYPE, PillarType.MIDDLE);
            }
            return this.getDefaultState().with(PILLAR_TYPE, PillarType.BOTTOM);
        } else if (hasDown) {
            return this.getDefaultState().with(PILLAR_TYPE, PillarType.TOP);
        }
        return this.getDefaultState().with(PILLAR_TYPE, PillarType.MIDDLE);
    }

    public static enum PillarType implements IStringSerializable {

        TOP,
        MIDDLE,
        BOTTOM;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }
}
