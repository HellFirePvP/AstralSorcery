/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockChalice
 * Created by HellFirePvP
 * Date: 09.11.2019 / 19:18
 */
public class BlockChalice extends ContainerBlock implements CustomItemBlock {

    private static final VoxelShape CHALICE = VoxelShapes.create(2D / 16D, 0D / 16D, 2D / 16D, 14D / 16D, 14D / 16D, 14D / 16D);

    public BlockChalice() {
        super(PropertiesMisc.defaultGoldMachinery()
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return CHALICE;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult brtr) {
        ItemStack interact = player.getHeldItem(hand);
        TileChalice tc = MiscUtils.getTileAt(world, pos, TileChalice.class, true);
        if (tc != null) {
            IFluidHandlerItem handlerItem = FluidUtil.getFluidHandler(interact).orElse(null);
            if (handlerItem != null) {
                if (!world.isRemote()) {
                    FluidStack st = FluidUtil.getFluidContained(interact).orElse(FluidStack.EMPTY);
                    if (st.isEmpty()) {
                        //Fill the stack from the tile?
                        FluidActionResult far = FluidUtil.tryFillContainer(interact, tc.getTankAccess(), FluidAttributes.BUCKET_VOLUME, player, true);
                        if (far.isSuccess()) {
                            if (!player.isCreative()) {
                                interact.shrink(1);
                                player.setHeldItem(hand, interact);
                                player.inventory.placeItemBackInInventory(world, far.getResult());
                            }
                        }
                    } else {
                        //Drain from stack into tile?
                        FluidActionResult far = FluidUtil.tryEmptyContainer(interact, tc.getTankAccess(), FluidAttributes.BUCKET_VOLUME, player, true);
                        if (far.isSuccess()) {
                            if (!player.isCreative()) {
                                interact.shrink(1);
                                player.setHeldItem(hand, interact);
                                player.inventory.placeItemBackInInventory(world, far.getResult());
                            }
                        }
                    }
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        TileChalice tc = MiscUtils.getTileAt(world, pos, TileChalice.class, false);
        if (tc != null) {
            return MathHelper.ceil(tc.getTank().getPercentageFilled() * 15F);
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
        return new TileChalice();
    }
}
