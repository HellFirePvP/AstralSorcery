/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.block.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.block.tile.base.BaseTickTileBlock;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.item.block.LumenCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.tile.TileLumenCrystalCluster;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystalClusterBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenCrystalClusterBlock extends BaseTickTileBlock<TileLumenCrystalCluster> implements BlockDynamicColor {

    public static MapCodec<LumenCrystalClusterBlock> CODEC = simpleCodec(LumenCrystalClusterBlock::new);
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape GROWTH_STAGE_0    = Block.box(5, 0, 5, 11, 8.5,  11);
    private static final VoxelShape GROWTH_STAGE_1    = Block.box(4, 0, 4, 12, 10,   12);
    private static final VoxelShape GROWTH_STAGE_2_NS = Block.box(5, 0, 1, 11, 10,   15);
    private static final VoxelShape GROWTH_STAGE_2_WE = Block.box(1, 0, 5, 15, 10,   11);
    private static final VoxelShape GROWTH_STAGE_3    = Block.box(2, 0, 2, 14, 12.5, 14);
    private static final VoxelShape GROWTH_STAGE_4    = Block.box(1, 0, 1, 15, 14.5,   15);

    public LumenCrystalClusterBlock(Properties properties) {
        super(properties, TileEntitiesAS.LUMEN_CRYSTAL_CLUSTER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE, FACING);
    }

    @Override
    protected float getMaxHorizontalOffset() {
        return 0.05F;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = switch (state.getValue(STAGE)) {
            case 0 -> GROWTH_STAGE_0;
            case 1 -> GROWTH_STAGE_1;
            case 2 -> state.getValue(FACING).getAxis() == Direction.Axis.Z ? GROWTH_STAGE_2_NS : GROWTH_STAGE_2_WE;
            case 3 -> GROWTH_STAGE_3;
            case 4 -> GROWTH_STAGE_4;
            default -> GROWTH_STAGE_4;
        };
        Vec3 offset = state.getOffset(level, pos);
        return shape.move(offset.x, offset.y, offset.z);
    }

    public BlockState getPlaceableState(BlockPos pos, int stage) {
        long seed = Mth.getSeed(pos.getX(), pos.getY(), pos.getZ());
        return this.defaultBlockState()
                .setValue(LumenCrystalClusterBlock.STAGE, stage)
                .setValue(LumenCrystalClusterBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(RandomSource.create(seed)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState toPlace = this.defaultBlockState();
        if (!context.getItemInHand().isEmpty()) {
            toPlace = this.getPlaceableState(context.getClickedPos(), LumenCrystalClusterBlockItem.getStage(context.getItemInHand()));
        }
        return toPlace;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (stack.has(DataComponentsAS.LUMEN)) {
            MiscUtil.getTileAt(level, pos, TileLumenCrystalCluster.class, true).ifPresent(cluster -> {
                Lumen lumen = stack.getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen().value();
                cluster.getTileData().setLumen(lumen);
                cluster.getTileData().markForUpdate();
            });
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        LumenCrystalClusterBlockItem.setStage(stack, state.getValue(STAGE));
        MiscUtil.getTileAt(level, pos, TileLumenCrystalCluster.class, true).ifPresent(cluster -> {
            LumenComponent.of(cluster.getTileData().getLumen()).ifPresent(lumenCmp -> {
                stack.set(DataComponentsAS.LUMEN, lumenCmp);
            });
        });
        return stack;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below());
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        int stage = state.getValue(STAGE);
        if (stage > 0 && !player.isCreative()) {
            BlockState newState = state.setValue(STAGE, stage - 1);
            return level.setBlock(pos, newState, Block.UPDATE_ALL);
        } else {
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected BlockEntityTicker<TileLumenCrystalCluster> createTicker() {
        return this.clientTicker();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public int getColor(BlockState state, long tick, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        //if (tintIndex != 1) return 0xFFFFFF;
        return MiscUtil.getTileAt(level, pos, TileLumenCrystalCluster.class, true).map(cluster -> {
            return cluster.getTileData().getLumen().getColor(tick).getColor();
        }).orElse(0xFFFFFF);
    }
}
