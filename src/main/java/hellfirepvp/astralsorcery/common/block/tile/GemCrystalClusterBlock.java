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
import hellfirepvp.astralsorcery.common.item.block.GemCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileGemCrystalCluster;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GemCrystalClusterBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GemCrystalClusterBlock extends BaseTickTileBlock<TileGemCrystalCluster> {

    public static MapCodec<GemCrystalClusterBlock> CODEC = simpleCodec(GemCrystalClusterBlock::new);
    public static final EnumProperty<GrowthStageType> STAGE = EnumProperty.create("stage", GrowthStageType.class);
    private static final VoxelShape STAGE_0       = Block.box(4, 0, 4, 12,  6, 12);
    private static final VoxelShape STAGE_1       = Block.box(4, 0, 4, 12,  8, 12);
    private static final VoxelShape STAGE_2_SKY   = Block.box(5, 0, 5, 11, 10, 11);
    private static final VoxelShape STAGE_2_DAY   = Block.box(4, 0, 4, 12, 10, 12);
    private static final VoxelShape STAGE_2_NIGHT = Block.box(5, 0, 5, 11,  8, 11);

    public GemCrystalClusterBlock(Properties properties) {
        super(properties, TileEntitiesAS.GEM_CRYSTAL_CLUSTER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE);
    }

    @Override
    protected float getMaxHorizontalOffset() {
        return 0.1F;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = switch (state.getValue(STAGE)) {
            case STAGE_0 -> STAGE_0;
            case STAGE_1 -> STAGE_1;
            case STAGE_2_SKY -> STAGE_2_SKY;
            case STAGE_2_DAY -> STAGE_2_DAY;
            case STAGE_2_NIGHT -> STAGE_2_NIGHT;
        };
        Vec3 offset = state.getOffset(level, pos);
        return shape.move(offset.x, offset.y, offset.z);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState toPlace = this.defaultBlockState();
        if (!context.getItemInHand().isEmpty()) {
            toPlace = toPlace.setValue(GemCrystalClusterBlock.STAGE, GemCrystalClusterBlockItem.getStage(context.getItemInHand()));
        }
        return toPlace;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        GemCrystalClusterBlockItem.setStage(stack, state.getValue(STAGE));
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
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected BlockEntityTicker<TileGemCrystalCluster> createTicker() {
        return this.ticker();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public enum GrowthStageType implements StringRepresentable {

        STAGE_0      (0, ColorWrapper.WHITE),
        STAGE_1      (1, ColorWrapper.WHITE),
        STAGE_2_SKY  (2, ColorsAS.GEM_CLUSTER_SKY),
        STAGE_2_DAY  (2, ColorsAS.GEM_CLUSTER_DAY),
        STAGE_2_NIGHT(2, ColorsAS.GEM_CLUSTER_NIGHT);

        private final int growthStage;
        private final ColorWrapper displayColor;

        GrowthStageType(int growthStage, ColorWrapper displayColor) {
            this.growthStage = growthStage;
            this.displayColor = displayColor;
        }

        public ColorWrapper getDisplayColor() {
            return this.displayColor;
        }

        public int getGrowthStage() {
            return this.growthStage;
        }

        public GrowthStageType grow(Level world) {
            if (this == STAGE_0) {
                return STAGE_1;
            }
            if (this == STAGE_1) {
                if (DayTimeHelper.isDay(world)) {
                    return STAGE_2_DAY;
                }
                if (DayTimeHelper.isNight(world)) {
                    return STAGE_2_NIGHT;
                }
                return STAGE_2_SKY;
            }
            return this;
        }

        public GrowthStageType shrink() {
            if (this.getGrowthStage() == 2) {
                return STAGE_1;
            }
            return STAGE_0;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return this.getSerializedName();
        }
    }
}
