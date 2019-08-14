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
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefactionContext;
import hellfirepvp.astralsorcery.common.lib.CapabilitiesAS;
import hellfirepvp.astralsorcery.common.lib.MaterialsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidActionResult;
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidUtil;
import hellfirepvp.astralsorcery.common.util.fluid.handler.CompatEmptyFluidHandler;
import hellfirepvp.astralsorcery.common.util.fluid.handler.ICompatFluidHandler;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockWell
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:26
 */
public class BlockWell extends BlockStarlightNetwork implements CustomItemBlock {

    private final VoxelShape shape;

    public BlockWell() {
        super(PropertiesMarble.defaultMarble()
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE));
        this.shape = createShape();
    }

    protected VoxelShape createShape() {
        VoxelShape footing = Block.makeCuboidShape(1, 0, 1, 15, 2, 15);
        VoxelShape floor = Block.makeCuboidShape(3, 2, 3, 13, 4, 13);
        VoxelShape basinFloor = Block.makeCuboidShape(1, 4, 1, 15, 5, 15);
        VoxelShape w1 = Block.makeCuboidShape(1, 5, 1, 2, 16, 14);
        VoxelShape w2 = Block.makeCuboidShape(2, 5, 1, 15, 16, 2);
        VoxelShape w3 = Block.makeCuboidShape(14, 5, 2, 15, 16, 15);
        VoxelShape w4 = Block.makeCuboidShape(1, 5, 14, 14, 16, 15);

        return VoxelUtils.combineAll(IBooleanFunction.OR,
                footing, floor, basinFloor, w1, w2, w3, w4);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return this.shape;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote()) {
            ItemStack heldItem = player.getHeldItem(hand);
            if(!heldItem.isEmpty()) {
                TileWell tw = MiscUtils.getTileAt(world, pos, TileWell.class, false);
                if(tw == null) {
                    return true;
                }

                WellLiquefaction entry = RecipeTypesAS.TYPE_WELL.findRecipe(new WellLiquefactionContext(heldItem));
                if(entry != null) {
                    ItemStackHandler handle = tw.getInventory();
                    if (!handle.getStackInSlot(0).isEmpty()) {
                        return true;
                    }
                    if (!world.isAirBlock(pos.up())) {
                        return true;
                    }

                    handle.setStackInSlot(0, ItemUtils.copyStackWithSize(heldItem, 1));
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                            ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);

                    if(!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    if(heldItem.getCount() <= 0) {
                        player.setHeldItem(hand, ItemStack.EMPTY);
                    }
                }

                ICompatFluidHandler handler = tw.getCapability(CapabilitiesAS.FLUID_HANDLER_COMPAT, null).orElse(null);
                if (handler != null) {
                    CompatFluidActionResult far = CompatFluidUtil.tryFillContainerAndStow(heldItem,
                            handler, new InvWrapper(player.inventory), CompatFluidStack.BUCKET_VOLUME, player, true);
                    if(far.isSuccess()) {
                        player.setHeldItem(hand, far.getResult());
                        SoundHelper.playSoundAround(SoundEvents.ITEM_BUCKET_FILL, world, pos, 1F, 1F);
                        tw.markForUpdate();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileWell tw = MiscUtils.getTileAt(worldIn, pos, TileWell.class, true);
        if (tw != null && !worldIn.isRemote) {
            ItemStack stack = tw.getInventory().getStackInSlot(0);
            if (!stack.isEmpty()) {
                tw.breakCatalyst();
            }
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        TileWell tw = MiscUtils.getTileAt(world, pos, TileWell.class, false);
        if (tw != null) {
            return MathHelper.ceil(tw.getTank().getPercentageFilled() * 15F);
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileWell();
    }

}
