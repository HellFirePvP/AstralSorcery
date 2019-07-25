/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.block.marble.BlockMarblePillar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.Constants;

import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureDesertShrine
 * Created by HellFirePvP
 * Date: 22.07.2019 / 09:02
 */
public class StructureDesertShrine extends StructureBlockArray {

    private static Random rand = new Random();

    public StructureDesertShrine() {
        super(StructureTypesAS.STYPE_DESERT.getRegistryName());

        makeStructure();
    }

    @Override
    public Map<BlockPos, BlockState> placeInWorld(IWorld world, BlockPos center, Predicate<BlockPos> posFilter) {
        Map<BlockPos, BlockState> result = super.placeInWorld(world, center, posFilter);
        placeSand(world, center, posFilter);
        return result;
    }

    private void placeSand(IWorld world, BlockPos center, Predicate<BlockPos> posFilter) {
        BlockState mch = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState mpl = BlocksAS.MARBLE_PILLAR.getDefaultState();
        BlockState mplUp   = BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, BlockMarblePillar.PillarType.TOP);
        BlockState mplDown = BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, BlockMarblePillar.PillarType.BOTTOM);

        setBlockCube(world, center, at -> world.getBiome(at).getSurfaceBuilderConfig().getTop(), -4, 1, -4, 4, 1, 4);

        setBlockState(world, center.add(0, 1, 0), mpl);
        setBlockState(world, center.add(0, 2, 0), mch);

        setBlockState(world, center.add(3, 1, 3), mplDown);
        setBlockState(world, center.add(3, 2, 3), mplUp);
        setBlockState(world, center.add(3, 3, 3), mch);

        setBlockState(world, center.add(-3, 1,  3), mplDown);
        setBlockState(world, center.add(-3, 2,  3), mplUp);
        setBlockState(world, center.add(-3, 3,  3), mch);

        setBlockState(world, center.add(-3, 1, -3), mplDown);
        setBlockState(world, center.add(-3, 2, -3), mplUp);
        setBlockState(world, center.add(-3, 3, -3), mch);

        setBlockState(world, center.add(3, 1, -3), mplDown);
        setBlockState(world, center.add(3, 2, -3), mplUp);
        setBlockState(world, center.add(3, 3, -3), mch);

        topBlockWithOffset(3, 4, 3, world, center, 1F);
        topBlockWithOffset(-3, 4,  3, world, center, 1F);
        topBlockWithOffset( 3, 4, -3, world, center, 1F);
        topBlockWithOffset(-3, 4, -3, world, center, 1F);
        topBlockWithOffset( 0, 3,  0, world, center, 1F);

        topBlockWithOffset( 1, 2,  0, world, center, 1F);
        topBlockWithOffset(-1, 2,  0, world, center, 1F);
        topBlockWithOffset( 0, 2,  1, world, center, 1F);
        topBlockWithOffset( 0, 2, -1, world, center, 1F);

        topBlockWithOffset(-3, 2, -4, world, center, 1F);
        topBlockWithOffset(-4, 2, -3, world, center, 1F);
        topBlockWithOffset(-3, 2, -2, world, center, 1F);
        topBlockWithOffset(-2, 2, -3, world, center, 1F);
        topBlockWithOffset(-3, 2,  4, world, center, 1F);
        topBlockWithOffset(-4, 2,  3, world, center, 1F);
        topBlockWithOffset(-3, 2,  2, world, center, 1F);
        topBlockWithOffset(-2, 2,  3, world, center, 1F);
        topBlockWithOffset( 3, 2, -4, world, center, 1F);
        topBlockWithOffset( 4, 2, -3, world, center, 1F);
        topBlockWithOffset( 3, 2, -2, world, center, 1F);
        topBlockWithOffset( 2, 2, -3, world, center, 1F);
        topBlockWithOffset( 3, 2,  4, world, center, 1F);
        topBlockWithOffset( 4, 2,  3, world, center, 1F);
        topBlockWithOffset( 3, 2,  2, world, center, 1F);
        topBlockWithOffset( 2, 2,  3, world, center, 1F);


        topBlockWithOffset( 3, 3,  4, world, center, 0.5);
        topBlockWithOffset( 4, 3,  3, world, center, 0.5);
        topBlockWithOffset( 3, 3,  2, world, center, 0.5);
        topBlockWithOffset( 2, 3,  3, world, center, 0.5);
        topBlockWithOffset(-3, 3,  4, world, center, 0.5);
        topBlockWithOffset(-4, 3,  3, world, center, 0.5);
        topBlockWithOffset(-3, 3,  2, world, center, 0.5);
        topBlockWithOffset(-2, 3,  3, world, center, 0.5);
        topBlockWithOffset( 3, 3, -4, world, center, 0.5);
        topBlockWithOffset( 4, 3, -3, world, center, 0.5);
        topBlockWithOffset( 3, 3, -2, world, center, 0.5);
        topBlockWithOffset( 2, 3, -3, world, center, 0.5);
        topBlockWithOffset(-3, 3, -4, world, center, 0.5);
        topBlockWithOffset(-4, 3, -3, world, center, 0.5);
        topBlockWithOffset(-3, 3, -2, world, center, 0.5);
        topBlockWithOffset(-2, 3, -3, world, center, 0.5);

        topBlockWithOffset( 1, 2,  1, world, center, 0.5);
        topBlockWithOffset(-1, 2,  1, world, center, 0.5);
        topBlockWithOffset( 1, 2, -1, world, center, 0.5);
        topBlockWithOffset(-1, 2, -1, world, center, 0.5);

        topBlockWithOffset( 0, 3,  1, world, center, 0.4);
        topBlockWithOffset( 1, 3,  0, world, center, 0.4);
        topBlockWithOffset( 0, 3, -1, world, center, 0.4);
        topBlockWithOffset(-1, 3,  0, world, center, 0.4);
        topBlockWithOffset( 0, 2,  2, world, center, 0.4);
        topBlockWithOffset( 2, 2,  0, world, center, 0.4);
        topBlockWithOffset( 0, 2, -2, world, center, 0.4);
        topBlockWithOffset(-2, 2,  0, world, center, 0.4);

        topBlockWithOffset( 2, 2,  2, world, center, 0.3);
        topBlockWithOffset( 4, 2,  2, world, center, 0.3);
        topBlockWithOffset( 2, 2,  4, world, center, 0.3);
        topBlockWithOffset( 4, 2,  4, world, center, 0.3);
        topBlockWithOffset( 2, 2, -2, world, center, 0.3);
        topBlockWithOffset( 4, 2, -2, world, center, 0.3);
        topBlockWithOffset( 2, 2, -4, world, center, 0.3);
        topBlockWithOffset( 4, 2, -4, world, center, 0.3);
        topBlockWithOffset(-2, 2,  2, world, center, 0.3);
        topBlockWithOffset(-4, 2,  2, world, center, 0.3);
        topBlockWithOffset(-2, 2,  4, world, center, 0.3);
        topBlockWithOffset(-4, 2,  4, world, center, 0.3);
        topBlockWithOffset(-2, 2, -2, world, center, 0.3);
        topBlockWithOffset(-4, 2, -2, world, center, 0.3);
        topBlockWithOffset(-2, 2, -4, world, center, 0.3);
        topBlockWithOffset(-4, 2, -4, world, center, 0.3);
    }

    private void makeStructure() {
        BlockState mbr = BlocksAS.MARBLE_BRICKS.getDefaultState();
        BlockState mrw = BlocksAS.MARBLE_RAW.getDefaultState();
        BlockState mar = BlocksAS.MARBLE_ARCH.getDefaultState();
        BlockState mpl = BlocksAS.MARBLE_PILLAR.getDefaultState();
        BlockState mplUp   = BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, BlockMarblePillar.PillarType.TOP);
        BlockState mplDown = BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, BlockMarblePillar.PillarType.BOTTOM);

        addBlockCube(mrw, -4, 0, -3, 4, -7, 3);
        addBlockCube(mrw, -3, 0, -4, 3, -7, 4);

        addBlockCube(mar, -4, 0, -3, -4, 0,  3);
        addBlockCube(mar,  4, 0, -3,  4, 0,  3);
        addBlockCube(mar, -3, 0, -4,  3, 0, -4);
        addBlockCube(mar, -3, 0,  4,  3, 0,  4);

        addBlockCube(mbr, -3, 0, -3,  3, 0,  3);
        addBlockCube(mrw, -2, 0, -2,  2, 0,  2);

        addBlockCube(Blocks.AIR.getDefaultState(), -2, -2, -2,  2, -5,  2);

        addBlock(-3, -5, -1, mplDown);
        addBlockCube(mpl, -3, -3, -1, -3, -4, -1);
        addBlock(-3, -2, -1, mplUp);

        addBlock(-3, -5,  1, mplDown);
        addBlockCube(mpl, -3, -3,  1, -3, -4,  1);
        addBlock(-3, -2,  1, mplUp);

        addBlock( 3, -5, -1, mplDown);
        addBlockCube(mpl,  3, -3, -1,  3, -4, -1);
        addBlock( 3, -2, -1, mplUp);

        addBlock( 3, -5,  1, mplDown);
        addBlockCube(mpl,  3, -3,  1,  3, -4,  1);
        addBlock( 3, -2,  1, mplUp);

        addBlock(-1, -5, -3, mplDown);
        addBlockCube(mpl, -1, -3, -3, -1, -4, -3);
        addBlock(-1, -2, -3, mplUp);

        addBlock( 1, -5, -3, mplDown);
        addBlockCube(mpl,  1, -3, -3,  1, -4, -3);
        addBlock( 1, -2, -3, mplUp);

        addBlock(-1, -5,  3, mplDown);
        addBlockCube(mpl, -1, -3,  3, -1, -4,  3);
        addBlock(-1, -2,  3, mplUp);

        addBlock( 1, -5,  3, mplDown);
        addBlockCube(mpl,  1, -3,  3,  1, -4,  3);
        addBlock( 1, -2,  3, mplUp);

        addBlockCube(mar, -2, -6, -2, 2, -6, 2);
        addBlockCube(Blocks.AIR.getDefaultState(), -2, -6,  0, 2, -6, 0);
        addBlockCube(Blocks.AIR.getDefaultState(), 0, -6, -2, 0, -6, 2);
        addBlock(-2, -6, -2, mrw);
        addBlock(-2, -6, 2, mrw);
        addBlock(2, -6, -2, mrw);
        addBlock( 2, -6,  2, mrw);

        addBlock(-3, -3,  0, Blocks.WATER.getDefaultState());
        addBlock( 3, -3,  0, Blocks.WATER.getDefaultState());
        addBlock( 0, -3, -3, Blocks.WATER.getDefaultState());
        addBlock( 0, -3,  3, Blocks.WATER.getDefaultState());
    }

    private void topBlockWithOffset(int x, int y, int z, IWorld world, BlockPos center, double chance) {
        if(rand.nextFloat() <= chance) {
            BlockPos at = center.add(x, y, z);
            setBlockState(world, at, world.getBiome(at).getSurfaceBuilderConfig().getTop());
        }
    }
}
