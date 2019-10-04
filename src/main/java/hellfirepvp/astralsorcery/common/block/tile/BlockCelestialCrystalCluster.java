package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockCrystalContainer;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialCrystalCluster
 * Created by HellFirePvP
 * Date: 30.09.2019 / 18:00
 */
public class BlockCelestialCrystalCluster extends BlockCrystalContainer {

    private static final VoxelShape GROWTH_STAGE_0 = VoxelShapes.create(0.1, 0.0, 0.1, 0.9, 0.3, 0.9);
    private static final VoxelShape GROWTH_STAGE_1 = VoxelShapes.create(0.1, 0.0, 0.1, 0.9, 0.4, 0.9);
    private static final VoxelShape GROWTH_STAGE_2 = VoxelShapes.create(0.1, 0.0, 0.1, 0.9, 0.5, 0.9);
    private static final VoxelShape GROWTH_STAGE_3 = VoxelShapes.create(0.1, 0.0, 0.1, 0.9, 0.6, 0.9);
    private static final VoxelShape GROWTH_STAGE_4 = VoxelShapes.create(0.1, 0.0, 0.1, 0.9, 0.7, 0.9);

    public static IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);

    public BlockCelestialCrystalCluster() {
        super(Properties.create(Material.GLASS, CollectorCrystalType.CELESTIAL_CRYSTAL.getMaterialColor())
                .hardnessAndResistance(3F, 3F)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1)
                .sound(SoundType.GLASS)
                .lightValue(8));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(STAGE)) {
            case 0:
                return GROWTH_STAGE_0;
            case 1:
                return GROWTH_STAGE_1;
            case 2:
                return GROWTH_STAGE_2;
            case 3:
                return GROWTH_STAGE_3;
            case 4:
                return GROWTH_STAGE_4;
        }
        return GROWTH_STAGE_0;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return func_220064_c(worldIn, blockpos) || func_220055_a(worldIn, blockpos, Direction.UP);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !state.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() :
                super.updatePostPlacement(state, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, world, pos, newState, isMoving);

            PktPlayEffect effect = new PktPlayEffect(PktPlayEffect.Type.SMALL_CRYSTAL_BREAK)
                    .addData(buf -> ByteBufUtils.writeVector(buf, new Vector3(pos).add(0.5, 0.4, 0.5)));
            PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(world, pos, 32));
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileCelestialCrystals();
    }

}
