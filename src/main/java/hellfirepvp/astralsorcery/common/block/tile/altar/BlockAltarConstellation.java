/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.altar;

import hellfirepvp.astralsorcery.common.block.base.LargeBlock;
import hellfirepvp.astralsorcery.common.block.tile.BlockAltar;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAltarConstellation
 * Created by HellFirePvP
 * Date: 12.08.2019 / 21:59
 */
public class BlockAltarConstellation extends BlockAltar implements LargeBlock {

    private static final AxisAlignedBB PLACEMENT_BOX = new AxisAlignedBB(-1, 0, -1, 1, 1, 1);

    private final VoxelShape shape;

    public BlockAltarConstellation() {
        super(AltarType.CONSTELLATION);
        this.shape = createShape();
    }

    @Override
    public AxisAlignedBB getBlockSpace() {
        return PLACEMENT_BOX;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.canPlaceAt(context) ? this.getDefaultState() : null;
    }

    protected VoxelShape createShape() {
        VoxelShape base = Block.makeCuboidShape(0, 0, 0, 16, 4, 16);
        VoxelShape pillar = Block.makeCuboidShape(4, 4, 4, 12, 8, 12);
        VoxelShape head = Block.makeCuboidShape(0, 8, 0, 16, 16, 16);

        return VoxelUtils.combineAll(IBooleanFunction.OR, base, pillar, head);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.shape;
    }
}
