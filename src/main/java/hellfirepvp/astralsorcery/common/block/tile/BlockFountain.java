package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesWood;
import hellfirepvp.astralsorcery.common.tile.TileFountain;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFountain
 * Created by HellFirePvP
 * Date: 29.10.2020 / 19:54
 */
public class BlockFountain extends ContainerBlock implements CustomItemBlock {

    private final VoxelShape shape;

    public BlockFountain() {
        super(PropertiesWood.defaultInfusedWood());

        this.shape = createShape();
    }

    protected VoxelShape createShape() {
        VoxelShape m1 = Block.makeCuboidShape(0, 10, 0, 16, 16, 16);
        VoxelShape m2 = Block.makeCuboidShape(4, 6, 4, 12, 10, 12);
        VoxelShape m3 = Block.makeCuboidShape(2, 0, 2, 14, 4, 14);
        VoxelShape m4 = Block.makeCuboidShape(0, 4, 0, 16, 6, 16);

        return VoxelUtils.combineAll(IBooleanFunction.OR, m1, m2, m3, m4);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Nullable
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileFountain();
    }
}
