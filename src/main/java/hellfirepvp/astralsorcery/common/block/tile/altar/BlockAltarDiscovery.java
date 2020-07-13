/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.altar;

import hellfirepvp.astralsorcery.common.block.tile.BlockAltar;
import hellfirepvp.astralsorcery.common.util.VoxelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAltarDiscovery
 * Created by HellFirePvP
 * Date: 12.08.2019 / 21:58
 */
public class BlockAltarDiscovery extends BlockAltar {

    private final VoxelShape shape;

    public BlockAltarDiscovery() {
        super(AltarType.DISCOVERY);
        this.shape = createShape();
    }

    protected VoxelShape createShape() {
        VoxelShape base = Block.makeCuboidShape(2, 0, 2, 14, 2, 14);
        VoxelShape pillar = VoxelShapes.create(0.25, 0.125, 0.25, 0.75, 9.5 / 16.0, 0.75);
        VoxelShape head = VoxelShapes.create(0, 9.5 / 16.0, 0, 1, 15.5 / 16.0, 1);

        return VoxelUtils.combineAll(IBooleanFunction.OR, base, pillar, head);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.shape;
    }
}
