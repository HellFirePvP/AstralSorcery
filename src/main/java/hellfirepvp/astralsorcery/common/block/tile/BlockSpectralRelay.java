/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.tile.TileSpectralRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockSpectralRelay
 * Created by HellFirePvP
 * Date: 14.08.2019 / 06:53
 */
public class BlockSpectralRelay extends BlockStarlightNetwork implements CustomItemBlock {

    private static final VoxelShape RELAY = Block.makeCuboidShape(2, 0, 2, 14, 2, 14);

    public BlockSpectralRelay() {
        super(PropertiesGlass.coatedGlass()
                .setLightLevel(state -> 4));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return RELAY;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote()) {
            ItemStack held = player.getHeldItem(hand);
            TileSpectralRelay tar = MiscUtils.getTileAt(world, pos, TileSpectralRelay.class, true);
            if (tar != null) {
                TileInventory inv = tar.getInventory();
                if (!held.isEmpty()) {
                    if (!inv.getStackInSlot(0).isEmpty()) {
                        ItemStack stack = inv.getStackInSlot(0);
                        player.inventory.placeItemBackInInventory(world, stack);
                        inv.setStackInSlot(0, ItemStack.EMPTY);
                        tar.markForUpdate();
                        TileSpectralRelay.cascadeRelayProximityUpdates(world, pos);
                    }

                    if (!world.isAirBlock(pos.up())) {
                        return ActionResultType.PASS;
                    }

                    inv.setStackInSlot(0, ItemUtils.copyStackWithSize(held, 1));
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    if (!player.isCreative()) {
                        held.shrink(1);
                    }
                    tar.updateAltarLinkState();
                    TileSpectralRelay.cascadeRelayProximityUpdates(world, pos);
                    tar.markForUpdate();
                } else {
                    if (!inv.getStackInSlot(0).isEmpty()) {
                        ItemStack stack = inv.getStackInSlot(0);
                        player.inventory.placeItemBackInInventory(world, stack);
                        inv.setStackInSlot(0, ItemStack.EMPTY);
                        TileSpectralRelay.cascadeRelayProximityUpdates(world, pos);
                        tar.markForUpdate();
                    }
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        if (!worldIn.isRemote()) {
            TileSpectralRelay.cascadeRelayProximityUpdates(worldIn, pos);
        }
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
    public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        TileSpectralRelay tsr = MiscUtils.getTileAt(world, pos, TileSpectralRelay.class, false);
        if (tsr != null) {
            return tsr.getInventory().getStackInSlot(0).isEmpty() ? 0 : 15;
        }
        return 0;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileSpectralRelay();
    }
}
