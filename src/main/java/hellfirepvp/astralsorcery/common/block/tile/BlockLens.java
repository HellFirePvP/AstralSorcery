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
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockLens;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
 * Class: BlockLens
 * Created by HellFirePvP
 * Date: 24.08.2019 / 22:26
 */
public class BlockLens extends BlockStarlightNetwork implements CustomItemBlock {

    private static final VoxelShape LENS_DOWN =  VoxelShapes.create(2.5D / 16D, 0,          2.5D / 16D, 13.5D / 16D, 14.5D / 16D, 13.5D / 16D);
    private static final VoxelShape LENS_UP =    VoxelShapes.create(2.5D / 16D, 1.5D / 16D, 2.5D / 16D, 13.5D / 16D, 1,           13.5D / 16D);
    private static final VoxelShape LENS_NORTH = VoxelShapes.create(2.5D / 16D, 2.5D / 16D, 0,          13.5D / 16D, 13.5D / 16D, 14.5D / 16D);
    private static final VoxelShape LENS_SOUTH = VoxelShapes.create(2.5D / 16D, 2.5D / 16D, 1.5D / 16D, 13.5D / 16D, 13.5D / 16D, 1);
    private static final VoxelShape LENS_EAST =  VoxelShapes.create(1.5D / 16D, 2.5D / 16D, 2.5D / 16D, 1,           13.5D / 16D, 13.5D / 16D);
    private static final VoxelShape LENS_WEST =  VoxelShapes.create(0,          2.5D / 16D, 2.5D / 16D, 14.5D / 16D, 13.5D / 16D, 13.5D / 16D);

    public static EnumProperty<Direction> PLACED_AGAINST = EnumProperty.create("against", Direction.class);

    public BlockLens() {
        super(PropertiesGlass.coatedGlass()
                .harvestTool(ToolType.PICKAXE));
        setDefaultState(this.getStateContainer().getBaseState().with(PLACED_AGAINST, Direction.DOWN));
    }

    @Override
    public Class<? extends BlockItem> getItemBlockClass() {
        return ItemBlockLens.class;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileLens lens = MiscUtils.getTileAt(world, pos, TileLens.class, true);
        if (lens != null && !world.isRemote() && !player.isCreative()) {
            if (lens.getColorType() != null) {
                ItemStack drop = lens.getColorType().getStack();
                ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            }
        }
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote() && player.isSneaking()) {
            TileLens lens = MiscUtils.getTileAt(world, pos, TileLens.class, true);
            if (lens != null && lens.getColorType() != null) {
                ItemStack drop = lens.getColorType().getStack();
                if (player.getHeldItem(hand).isEmpty()) {
                    player.setHeldItem(hand, drop);
                } else {
                    if (!player.inventory.addItemStackToInventory(drop)) {
                        ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                    }
                }
                SoundHelper.playSoundAround(SoundsAS.BLOCK_COLOREDLENS_ATTACH, world, pos, 0.8F, 1.5F);
                lens.setColorType(null);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PLACED_AGAINST);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(PLACED_AGAINST, context.getFace().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(PLACED_AGAINST)) {
            case UP:
                return LENS_UP;
            case NORTH:
                return LENS_NORTH;
            case SOUTH:
                return LENS_SOUTH;
            case WEST:
                return LENS_WEST;
            case EAST:
                return LENS_EAST;
            default:
            case DOWN:
                return LENS_DOWN;
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileLens();
    }
}
