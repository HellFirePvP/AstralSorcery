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
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LensBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LensBlock extends BaseTickTileBlock<TileLens> {

    private static final VoxelShape DOWN =  Shapes.box(2.5D / 16D, 0,          2.5D / 16D, 13.5D / 16D, 14.5D / 16D, 13.5D / 16D);
    private static final VoxelShape UP =    Shapes.box(2.5D / 16D, 1.5D / 16D, 2.5D / 16D, 13.5D / 16D, 1,           13.5D / 16D);
    private static final VoxelShape NORTH = Shapes.box(2.5D / 16D, 2.5D / 16D, 0,          13.5D / 16D, 13.5D / 16D, 14.5D / 16D);
    private static final VoxelShape SOUTH = Shapes.box(2.5D / 16D, 2.5D / 16D, 1.5D / 16D, 13.5D / 16D, 13.5D / 16D, 1);
    private static final VoxelShape EAST =  Shapes.box(1.5D / 16D, 2.5D / 16D, 2.5D / 16D, 1,           13.5D / 16D, 13.5D / 16D);
    private static final VoxelShape WEST =  Shapes.box(0,          2.5D / 16D, 2.5D / 16D, 14.5D / 16D, 13.5D / 16D, 13.5D / 16D);

    public static MapCodec<LensBlock> CODEC = simpleCodec(LensBlock::new);
    public static EnumProperty<Direction> PLACED_AGAINST = EnumProperty.create("against", Direction.class);

    public LensBlock(Properties properties) {
        super(properties, TileEntitiesAS.LENS);
        this.registerDefaultState(this.stateDefinition.any().setValue(PLACED_AGAINST, Direction.DOWN));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PLACED_AGAINST)) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PLACED_AGAINST);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(PLACED_AGAINST, context.getClickedFace().getOpposite());
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected BlockEntityTicker<TileLens> createTicker() {
        return this.ticker();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
