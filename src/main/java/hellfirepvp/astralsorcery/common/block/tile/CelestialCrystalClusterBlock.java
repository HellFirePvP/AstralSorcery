/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.block.tile.base.BaseTickTileBlock;
import hellfirepvp.astralsorcery.common.item.block.CelestialCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialCrystalClusterBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialCrystalClusterBlock extends BaseTickTileBlock<TileCelestialCrystalCluster> {

    public static MapCodec<CelestialCrystalClusterBlock> CODEC = simpleCodec(CelestialCrystalClusterBlock::new);
    private static final VoxelShape GROWTH_STAGE_0 = Block.box(4, 0, 5, 12, 8, 11);
    private static final VoxelShape GROWTH_STAGE_1 = Block.box(4, 0, 5, 12, 10, 11);
    private static final VoxelShape GROWTH_STAGE_2 = Block.box(2, 0, 4, 12, 12, 14);
    private static final VoxelShape GROWTH_STAGE_3 = Block.box(2, 0, 2, 14, 14, 14);
    private static final VoxelShape GROWTH_STAGE_4 = Block.box(2, 0, 2, 14, 16, 14);

    public static IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);

    public CelestialCrystalClusterBlock(Properties properties) {
        super(properties, TileEntitiesAS.CELESTIAL_CRYSTAL_CLUSTER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected float getMaxHorizontalOffset() {
        return 0.1F;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = switch (state.getValue(STAGE)) {
            case 0 -> GROWTH_STAGE_0;
            case 1 -> GROWTH_STAGE_1;
            case 2 -> GROWTH_STAGE_2;
            case 3 -> GROWTH_STAGE_3;
            case 4 -> GROWTH_STAGE_4;
            default -> GROWTH_STAGE_4;
        };
        Vec3 offset = state.getOffset(level, pos);
        return shape.move(offset.x, offset.y, offset.z);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState toPlace = this.defaultBlockState();
        if (!context.getItemInHand().isEmpty()) {
            toPlace = toPlace.setValue(CelestialCrystalClusterBlock.STAGE, CelestialCrystalClusterBlockItem.getStage(context.getItemInHand()));
        }
        return toPlace;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        CelestialCrystalClusterBlockItem.setStage(stack, state.getValue(STAGE));
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
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @org.jetbrains.annotations.Nullable Entity entity) {
        return super.getSoundType(state, level, pos, entity);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below());
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected BlockEntityTicker<TileCelestialCrystalCluster> createTicker() {
        return this.ticker();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
