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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PillarBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PillarBlock extends Block implements SimpleWaterloggedBlock {

    public static final MapCodec<PillarBlock> CODEC = simpleCodec(PillarBlock::new);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<PillarType> PILLAR_TYPE = EnumProperty.create("pillartype", PillarType.class);

    private final VoxelShape middleShape, bottomShape, topShape;

    public PillarBlock(Properties prop) {
        super(prop);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PILLAR_TYPE, PillarType.MIDDLE).setValue(WATERLOGGED, false));
        this.middleShape = createPillarShape();
        this.topShape    = createPillarTopShape();
        this.bottomShape = createPillarBottomShape();
    }

    public static Supplier<PillarBlock> make(Properties prop) {
        return () -> new PillarBlock(prop);
    }

    @Override
    protected MapCodec<PillarBlock> codec() {
        return CODEC;
    }

    protected VoxelShape createPillarShape() {
        return Block.box(2, 0, 2, 14, 16, 14);
    }

    protected VoxelShape createPillarTopShape() {
        VoxelShape column = Block.box(2, 0, 2, 14, 12, 14);
        VoxelShape top = Block.box(0, 12, 0, 16, 16, 16);

        return Shapes.or(column, top);
    }

    protected VoxelShape createPillarBottomShape() {
        VoxelShape column = Block.box(2, 4, 2, 14, 16, 14);
        VoxelShape bottom = Block.box(0, 0, 0, 16, 4, 16);

        return Shapes.or(column, bottom);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PILLAR_TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(PILLAR_TYPE)) {
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
                return this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.MIDDLE);
            }
            return this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.BOTTOM);
        } else if (hasDown) {
            return this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.TOP);
        }
        return this.defaultBlockState().setValue(PILLAR_TYPE, PillarType.MIDDLE);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.BLOCKED;
    }

    public enum PillarType implements StringRepresentable {

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
