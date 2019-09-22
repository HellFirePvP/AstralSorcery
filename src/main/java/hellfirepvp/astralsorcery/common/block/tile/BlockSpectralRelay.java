/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
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

    private static final VoxelShape RELAY = VoxelShapes.create(2D / 16D, 0, 2D / 16D, 14D / 16D, 2D / 16D, 14D / 16D);

    public BlockSpectralRelay() {
        super(PropertiesGlass.coatedGlass()
                .lightValue(4));
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return RELAY;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
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
                    }

                    if (!world.isAirBlock(pos.up())) {
                        return false;
                    }

                    inv.setStackInSlot(0, ItemUtils.copyStackWithSize(held, 1));
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    if (!player.isCreative()) {
                        held.shrink(1);
                    }
                    tar.markForUpdate();
                } else {
                    if (!inv.getStackInSlot(0).isEmpty()) {
                        ItemStack stack = inv.getStackInSlot(0);
                        player.inventory.placeItemBackInInventory(world, stack);
                        inv.setStackInSlot(0, ItemStack.EMPTY);
                        tar.markForUpdate();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileSpectralRelay();
    }
}
