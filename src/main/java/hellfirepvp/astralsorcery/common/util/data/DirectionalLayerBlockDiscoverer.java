/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DirectionalLayerBlockDiscoverer
 * Created by HellFirePvP
 * Date: 01.11.2016 / 15:53
 */
public class DirectionalLayerBlockDiscoverer {

    private World world;
    private BlockPos start;
    private int rad, stepWidth;

    public DirectionalLayerBlockDiscoverer(World world, BlockPos start, int discoverRadius, int stepWidth) {
        this.world = world;
        this.start = start;
        this.rad = discoverRadius;
        this.stepWidth = stepWidth;
    }

    public LinkedList<BlockPos> discoverApplicableBlocks() {
        LinkedList<BlockPos> visited = new LinkedList<>();
        //LinkedList<BlockPos> validCache = new LinkedList<>();

        int xPos = start.getX();
        int yPos = start.getY();
        int zPos = start.getZ();
        BlockPos currentPos = start;
        tryAdd(start, visited);

        EnumFacing dir = EnumFacing.NORTH;
        while ( Math.abs(currentPos.getX() - xPos) <= rad &&
                Math.abs(currentPos.getY() - yPos) <= rad &&
                Math.abs(currentPos.getZ() - zPos) <= rad) {
            currentPos = currentPos.offset(dir, stepWidth);
            tryAdd(currentPos, visited);
            EnumFacing tryDirNext = dir.rotateY();
            if(!visited.contains(currentPos.offset(tryDirNext, stepWidth))) {
                dir = tryDirNext;
            }
        }

        return visited;
    }

    private void tryAdd(BlockPos at, List<BlockPos> visited) {
        if(!visited.contains(at)) {
            visited.add(at);
        }
    }

}
