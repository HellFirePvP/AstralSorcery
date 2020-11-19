/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeacon;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
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
 * Class: BlockTreeBeacon
 * Created by HellFirePvP
 * Date: 04.09.2020 / 21:13
 */
public class BlockTreeBeacon extends BlockStarlightNetwork implements CustomItemBlock {

    private static final VoxelShape SHAPE = VoxelShapes.create(3D / 16D, 0D / 16D, 3D / 16D, 13D / 16D, 16D / 16D, 13D / 16D);

    public BlockTreeBeacon() {
        super(PropertiesMisc.defaultPlant()
                .hardnessAndResistance(1.5F, 6.0F)
                .harvestLevel(1)
                .harvestTool(ToolType.AXE)
                .setLightLevel(state -> 6)
                .sound(SoundType.PLANT));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileTreeBeacon ttb = MiscUtils.getTileAt(world, pos, TileTreeBeacon.class, true);
        if (ttb != null && !world.isRemote() && placer instanceof ServerPlayerEntity && !MiscUtils.isPlayerFakeMP((ServerPlayerEntity) placer)) {
            ttb.setPlayerUUID(placer.getUniqueID());
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction placedAgainst, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        if (!this.isValidPosition(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return hasSolidSideOnTop(world, pos.down());
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileTreeBeacon();
    }
}
