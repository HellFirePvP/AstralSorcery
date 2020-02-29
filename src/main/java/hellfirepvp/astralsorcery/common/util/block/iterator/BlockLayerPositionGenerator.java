/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block.iterator;

import hellfirepvp.astralsorcery.common.util.block.BlockGeometry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockLayerPositionGenerator
 * Created by HellFirePvP
 * Date: 01.02.2020 / 10:43
 */
public class BlockLayerPositionGenerator extends BlockPositionGenerator {

    private int layer = 0;

    private LinkedList<BlockPos> currentPositions = new LinkedList<>();

    @Override
    protected BlockPos genNext(BlockPos offset, double radius) {
        int size = MathHelper.floor(radius);

        while (currentPositions.isEmpty()) {
            generatePositions(size);
        }
        return null;
    }

    private void generatePositions(int maxLayers) {
        if (maxLayers <= 0) {
            this.currentPositions.add(BlockPos.ZERO);
            return;
        }
        this.layer++;
        if (this.layer > maxLayers) {
            this.layer = -maxLayers;
        }
        Collection<BlockPos> positions = BlockGeometry.getPlane(Direction.UP, maxLayers);
        positions.forEach(pos -> this.currentPositions.add(pos.add(0, this.layer, 0)));
        Collections.shuffle(this.currentPositions, new Random(0xF518E23A05B27C19L));
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        nbt.putInt("layer", this.layer);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        this.layer = nbt.getInt("layer");
    }
}
