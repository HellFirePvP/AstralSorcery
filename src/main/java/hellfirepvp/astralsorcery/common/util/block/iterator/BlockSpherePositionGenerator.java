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
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockSpherePositionGenerator
 * Created by HellFirePvP
 * Date: 24.11.2019 / 09:11
 */
public class BlockSpherePositionGenerator extends BlockPositionGenerator {

    private int currentRadius = 0;

    private List<BlockPos> currentPositions = new ArrayList<>();

    @Override
    public BlockPos genNext(BlockPos offset, double radius) {
        if (this.currentRadius > radius) {
            this.currentPositions.clear();
        }

        while (currentPositions.isEmpty()) {
            generatePositions(radius);
        }
        return currentPositions.remove(0).add(offset);
    }

    private void generatePositions(double maxRadius) {
        if (maxRadius <= 0) {
            this.currentPositions.add(BlockPos.ZERO);
            return;
        }
        if (this.currentRadius >= maxRadius || this.currentRadius < 0) {
            this.currentRadius = 0;
        }
        this.currentRadius++;

        this.currentPositions.addAll(BlockGeometry.getHollowSphere(this.currentRadius, this.currentRadius - 1));
        Collections.shuffle(this.currentPositions, new Random(0xF518E23A05B27C19L));
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {
        nbt.putInt("currentRadius", this.currentRadius);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        this.currentRadius = nbt.getInt("currentRadius");
    }

}
