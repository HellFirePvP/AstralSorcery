/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockIlluminator
 * Created by HellFirePvP
 * Date: 04.04.2020 / 16:50
 */
public class BlockIlluminator extends ContainerBlock implements CustomItemBlock {

    private final VoxelShape shape;

    public BlockIlluminator() {
        super(PropertiesGlass.coatedGlass()
                .lightValue(10)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE));
        this.shape = createShape();
    }

    protected VoxelShape createShape() {
        List<VoxelShape> shapes = new ArrayList<>();
        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                for (int zz = 0; zz < 3; zz++) {
                    shapes.add(Block.makeCuboidShape(
                            1 + xx * 5, 1 + yy * 5, 1 + zz * 5,
                            5 + xx * 5, 5 + yy * 5, 5 + zz * 5));
                }
            }
        }

        return VoxelUtils.combineAll(IBooleanFunction.OR, shapes);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote() && placer instanceof PlayerEntity) {
            TileIlluminator illuminator = MiscUtils.getTileAt(world, pos, TileIlluminator.class, true);
            if (illuminator != null)  {
                illuminator.setPlayerPlaced(true);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return this.shape;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileIlluminator();
    }
}
