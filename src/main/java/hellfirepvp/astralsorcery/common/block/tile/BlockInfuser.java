/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockInventory;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
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
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockInfuser
 * Created by HellFirePvP
 * Date: 09.11.2019 / 19:22
 */
public class BlockInfuser extends BlockInventory implements CustomItemBlock {

    private static final VoxelShape INFUSER = VoxelShapes.create(0D / 16D, 0D / 16D, 0D / 16D, 16D / 16D, 12D / 16D, 16D / 16D);

    public BlockInfuser() {
        super(PropertiesMarble.defaultMarble()
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return INFUSER;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ItemStack held = player.getHeldItem(hand);
            TileInfuser ti = MiscUtils.getTileAt(world, pos, TileInfuser.class, true);
            if (ti != null) {
                ItemStack stored = ti.getItemInput();
                if (!held.isEmpty()) {
                    if (!stored.isEmpty()) {
                        player.inventory.placeItemBackInInventory(world, stored);
                        ti.setItemInput(ItemStack.EMPTY);
                        ti.markForUpdate();
                    }

                    if (!world.isAirBlock(pos.up())) {
                        return ActionResultType.PASS;
                    }

                    ti.setItemInput(ItemUtils.copyStackWithSize(held, 1));
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    if (!player.isCreative()) {
                        held.shrink(1);
                    }
                    ti.markForUpdate();
                } else {
                    if (!stored.isEmpty()) {
                        player.inventory.placeItemBackInInventory(world, stored);
                        ti.setItemInput(ItemStack.EMPTY);
                        ti.markForUpdate();
                    }
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        TileInfuser ti = MiscUtils.getTileAt(world, pos, TileInfuser.class, false);
        if (ti != null) {
            return ti.getItemInput().isEmpty() ? 0 : 15;
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileInfuser();
    }
}
