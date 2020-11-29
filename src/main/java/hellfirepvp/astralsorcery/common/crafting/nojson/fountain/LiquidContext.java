/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.fountain;

import hellfirepvp.astralsorcery.common.util.block.BlockGeometry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidContext
 * Created by HellFirePvP
 * Date: 01.11.2020 / 13:33
 */
public class LiquidContext extends FountainEffect.EffectContext {

    private final List<BlockPos> digPositions;
    private int tickLiquidProduction = 0;

    public Object fountainSprite;

    public LiquidContext(BlockPos fountainPos) {
        this.digPositions = BlockGeometry.getVerticalCone(fountainPos.down(3), 5);
    }

    public List<BlockPos> getDigPositions() {
        return digPositions;
    }

    public void resetLiquidProductionTick(Random rand) {
        this.tickLiquidProduction = 20 + rand.nextInt(10);
    }

    public boolean tickLiquidProduction() {
        this.tickLiquidProduction--;
        return this.tickLiquidProduction <= 0;
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {}

    @Override
    public void writeToNBT(CompoundNBT compound) {}
}
