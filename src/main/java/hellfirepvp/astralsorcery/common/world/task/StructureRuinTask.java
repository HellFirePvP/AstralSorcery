/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.task;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureRuinTask
 * Created by HellFirePvP
 * Date: 17.08.2018 / 22:21
 */
public class StructureRuinTask implements Runnable {

    private final WorldServer world;
    private final BlockPos pos;
    private final Random random;

    public StructureRuinTask(WorldServer world, BlockPos pos, Random random) {
        this.world = world;
        this.pos = pos;
        this.random = random;
    }

    @Override
    public void run() {
        /*
        lblSpawns:
        for (EnumFacing horizontal : EnumFacing.HORIZONTALS) {
            BlockPos at = this.pos.offset(horizontal, 50 + random.nextInt(35));
            BlockPos top = MiscUtils.itDownTopBlock(this.world, at);
            if (top != null && Math.abs(top.getY() - this.pos.getY()) <= 20) {

                for (int yy = -1; yy <= 0; yy++) {
                    for (int xx = -1; xx <= 1; xx++) {
                        for (int zz = -1; zz <= 1; zz++) {
                            BlockPos test = top.add(xx, yy, zz);
                            IBlockState testState = world.getBlockState(test);
                            Material mat = testState.getMaterial();
                            if (mat.isLiquid()) {
                                continue lblSpawns; //Not spawning on water.
                            }
                        }
                    }
                }

                for (BlockPos offset : MultiBlockArrays.smallRuinConduit.getPattern().keySet()) {
                    if (offset.getY() > 1 && !world.isAirBlock(offset.add(top))) {
                        continue lblSpawns; //Occupied.
                    }
                }

                MultiBlockArrays.smallRuinConduit.placeInWorld(this.world, top);
            }
        }
        */
    }

}
