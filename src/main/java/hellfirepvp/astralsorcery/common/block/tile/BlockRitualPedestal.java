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
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.MaterialsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.observerlib.api.block.BlockStructureObserver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRitualPedestal
 * Created by HellFirePvP
 * Date: 09.07.2019 / 20:03
 */
public class BlockRitualPedestal extends BlockStarlightNetwork implements CustomItemBlock, BlockStructureObserver {

    public BlockRitualPedestal() {
        super(PropertiesMarble.defaultMarble()
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rtr) {
        if (world.isRemote()) {
            return true;
        }
        TileRitualPedestal pedestal = MiscUtils.getTileAt(world, pos, TileRitualPedestal.class, true);
        if (pedestal == null) {
            return false;
        }

        ItemStack heldItem = player.getHeldItem(hand);

        ItemStack in = pedestal.getCurrentCrystal();
        if (player.isSneaking()) {
            pedestal.tryPlaceCrystalInPedestal(ItemStack.EMPTY);
            if (player.getHeldItem(hand).isEmpty()) {
                player.setHeldItem(hand, in);
            } else {
                player.inventory.placeItemBackInInventory(world, in);
            }
        } else {
            player.setHeldItem(hand, pedestal.tryPlaceCrystalInPedestal(heldItem));
        }
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        TileRitualPedestal te = MiscUtils.getTileAt(world, pos, TileRitualPedestal.class, true);
        if (te != null && !world.isRemote()) {
            BlockPos toCheck = pos.up();
            BlockState other = world.getBlockState(toCheck);
            if (Block.hasSolidSide(other, world, toCheck, Direction.DOWN)) {
                ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, te.getCurrentCrystal());
                te.tryPlaceCrystalInPedestal(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 2;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileRitualPedestal();
    }
}
