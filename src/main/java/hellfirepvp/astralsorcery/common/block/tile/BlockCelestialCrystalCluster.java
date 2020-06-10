/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockCrystalContainer;
import hellfirepvp.astralsorcery.common.block.base.BlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialCrystalCluster
 * Created by HellFirePvP
 * Date: 30.09.2019 / 18:00
 */
public class BlockCelestialCrystalCluster extends BlockCrystalContainer implements BlockStarlightRecipient, CustomItemBlock {

    private static final VoxelShape GROWTH_STAGE_0 = Block.makeCuboidShape(4, 0, 5, 12, 8, 11);
    private static final VoxelShape GROWTH_STAGE_1 = Block.makeCuboidShape(4, 0, 5, 12, 10, 11);
    private static final VoxelShape GROWTH_STAGE_2 = Block.makeCuboidShape(2, 0, 4, 12, 12, 14);
    private static final VoxelShape GROWTH_STAGE_3 = Block.makeCuboidShape(2, 0, 2, 14, 14, 14);
    private static final VoxelShape GROWTH_STAGE_4 = Block.makeCuboidShape(2, 0, 2, 14, 16, 14);

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
    public Class<? extends BlockItem> getItemBlockClass() {
        return ItemBlockCelestialCrystalCluster.class;
    }

    @Override
    public void receiveStarlight(World world, Random rand, BlockPos pos, IWeakConstellation starlightType, double amount) {
        TileCelestialCrystals crystals = MiscUtils.getTileAt(world, pos, TileCelestialCrystals.class, false);
        if (crystals != null) {
            crystals.grow((int) (TileCelestialCrystals.TICK_GROWTH_CHANCE / amount));
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        Vec3d offset = state.getOffset(world, pos);
        VoxelShape shape;
        switch (state.get(STAGE)) {
            case 4:
                shape = GROWTH_STAGE_4;
                break;
            case 3:
                shape = GROWTH_STAGE_3;
                break;
            case 2:
                shape = GROWTH_STAGE_2;
                break;
            case 1:
                shape = GROWTH_STAGE_1;
                break;
            case 0:
            default:
                shape = GROWTH_STAGE_0;
        }
        return shape.withOffset(offset.x, offset.y, offset.z);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @Override
    public Vec3d getOffset(BlockState state, IBlockReader world, BlockPos pos) {
        return super.getOffset(state, world, pos).mul(0.7, 0.7, 0.7);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return hasSolidSideOnTop(worldIn, blockpos) || hasEnoughSolidSide(worldIn, blockpos, Direction.UP);
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
                    .addData(buf -> ByteBufUtils.writeVector(buf,
                            new Vector3(pos).add(state.getOffset(world, pos)).add(0.5, 0.4, 0.5)));
            PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(world, pos, 32));
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileCelestialCrystals();
    }
}
