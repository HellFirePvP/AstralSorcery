/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ColumnBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ColumnBlock extends Block implements SimpleWaterloggedBlock {

    public static final MapCodec<ColumnBlock> CODEC = simpleCodec(ColumnBlock::new);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<ColumnType> COLUMN_TYPE = EnumProperty.create("columntype", ColumnType.class);

    private final VoxelShape middleShape, bottomShape, topShape;

    public ColumnBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(COLUMN_TYPE, ColumnType.MIDDLE).setValue(WATERLOGGED, false));
        this.middleShape = createColumnShape();
        this.topShape    = createColumnTopShape();
        this.bottomShape = createColumnBottomShape();
    }

    public static Supplier<ColumnBlock> make(Properties prop) {
        return () -> new ColumnBlock(prop);
    }

    @Override
    protected MapCodec<ColumnBlock> codec() {
        return CODEC;
    }

    protected VoxelShape createColumnShape() {
        return Block.box(4, 0, 4, 12, 16, 12);
    }

    protected VoxelShape createColumnTopShape() {
        VoxelShape column = Block.box(4, 0, 4, 12, 14, 12);
        VoxelShape top = Block.box(2, 14, 2, 14, 16, 14);

        return Shapes.or(column, top);
    }

    protected VoxelShape createColumnBottomShape() {
        VoxelShape column = Block.box(4, 2, 4, 12, 16, 12);
        VoxelShape bottom = Block.box(2, 0, 2, 14, 2, 14);

        return Shapes.or(column, bottom);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLUMN_TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(COLUMN_TYPE)) {
            case TOP -> this.topShape;
            case BOTTOM -> this.bottomShape;
            default -> this.middleShape;
        };
    }

    @Override
    public BlockState updateShape(BlockState thisState, Direction otherBlockFacing, BlockState otherBlockState, LevelAccessor world, BlockPos thisPos, BlockPos otherBlockPos) {
        if (thisState.getValue(WATERLOGGED)) {
            world.scheduleTick(thisPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return this.getThisState(world, thisPos).setValue(WATERLOGGED, thisState.getValue(WATERLOGGED));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockpos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        FluidState ifluidstate = world.getFluidState(blockpos);
        return this.getThisState(world, blockpos).setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER);
    }

    private BlockState getThisState(BlockGetter world, BlockPos pos) {
        boolean hasUp   = world.getBlockState(pos.above()).getBlock() == this;
        boolean hasDown = world.getBlockState(pos.below()).getBlock() == this;
        if (hasUp) {
            if (hasDown) {
                return this.defaultBlockState().setValue(COLUMN_TYPE, ColumnType.MIDDLE);
            }
            return this.defaultBlockState().setValue(COLUMN_TYPE, ColumnType.BOTTOM);
        } else if (hasDown) {
            return this.defaultBlockState().setValue(COLUMN_TYPE, ColumnType.TOP);
        }
        return this.defaultBlockState().setValue(COLUMN_TYPE, ColumnType.MIDDLE);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.BLOCKED;
    }

    public enum ColumnType implements StringRepresentable {

        TOP,
        MIDDLE,
        BOTTOM;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return this.getSerializedName();
        }
    }
}
