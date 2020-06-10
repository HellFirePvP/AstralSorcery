/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockPrism;
import hellfirepvp.astralsorcery.common.item.lens.LensColorType;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.tile.TilePrism;
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
import net.minecraft.state.BooleanProperty;
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
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockPrism
 * Created by HellFirePvP
 * Date: 24.08.2019 / 23:10
 */
public class BlockPrism extends BlockStarlightNetwork implements CustomItemBlock, BlockDynamicColor {

    private static final VoxelShape PRISM_DOWN =  VoxelShapes.create(3D / 16D, 0,      3D / 16D, 13D / 16D, 14D / 16D, 13D / 16D);
    private static final VoxelShape PRISM_UP =    VoxelShapes.create(3D / 16D, 2D / 16D, 3D / 16D, 13D / 16D, 1,       13D / 16D);
    private static final VoxelShape PRISM_NORTH = VoxelShapes.create(3D / 16D, 3D / 16D, 0,      13D / 16D, 13D / 16D, 14D / 16D);
    private static final VoxelShape PRISM_SOUTH = VoxelShapes.create(3D / 16D, 3D / 16D, 2D / 16D, 13D / 16D, 13D / 16D, 1);
    private static final VoxelShape PRISM_EAST =  VoxelShapes.create(2D / 16D, 3D / 16D, 3D / 16D, 1,       13D / 16D, 13D / 16D);
    private static final VoxelShape PRISM_WEST =  VoxelShapes.create(0,      3D / 16D, 3D / 16D, 14D / 16D, 13D / 16D, 13D / 16D);
    
    public static EnumProperty<Direction> PLACED_AGAINST = EnumProperty.create("against", Direction.class);
    public static BooleanProperty HAS_COLORED_LENS = BooleanProperty.create("has_lens");

    public BlockPrism() {
        super(PropertiesGlass.coatedGlass()
                .harvestTool(ToolType.PICKAXE));
        setDefaultState(this.getStateContainer().getBaseState().with(PLACED_AGAINST, Direction.DOWN).with(HAS_COLORED_LENS, false));
    }

    @Override
    public Class<? extends BlockItem> getItemBlockClass() {
        return ItemBlockPrism.class;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TilePrism lens = MiscUtils.getTileAt(world, pos, TilePrism.class, true);
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
            TilePrism lens = MiscUtils.getTileAt(world, pos, TilePrism.class, true);
            if (lens != null && lens.getColorType() != null) {
                ItemStack drop = lens.getColorType().getStack();
                if (!player.isCreative()) {
                    if (player.getHeldItem(hand).isEmpty()) {
                        player.setHeldItem(hand, drop);
                    } else {
                        if (!player.inventory.addItemStackToInventory(drop)) {
                            ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                        }
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
        builder.add(PLACED_AGAINST, HAS_COLORED_LENS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(PLACED_AGAINST, context.getFace().getOpposite());
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getColor(BlockState state, @Nullable ILightReader world, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex != 3) { //prism_colored_all.json
            return 0xFFFFFFFF;
        }
        TilePrism prism = MiscUtils.getTileAt(world, pos, TilePrism.class, false);
        if (prism != null) {
            LensColorType type = prism.getColorType();
            if (type != null) {
                return type.getColor().getRGB();
            }
        }
        return 0xFFFFFFFF;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(PLACED_AGAINST)) {
            case UP:
                return PRISM_UP;
            case NORTH:
                return PRISM_NORTH;
            case SOUTH:
                return PRISM_SOUTH;
            case WEST:
                return PRISM_WEST;
            case EAST:
                return PRISM_EAST;
            default:
            case DOWN:
                return PRISM_DOWN;
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TilePrism();
    }
}
