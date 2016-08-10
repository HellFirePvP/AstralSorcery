package hellfirepvp.astralsorcery.common.ritual.constraints;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstraintHorizontal
 * Created by HellFirePvP
 * Date: 10.08.2016 / 10:42
 */
public class ConstraintHorizontal extends RitualConstraint {

    @Override
    public int getFulfillment(World world, BlockPos central, Map<BlockPos, Integer> offsetMetaMap, Constellation type) {
        int minViolation = 0;
        for (BlockPos pos : offsetMetaMap.keySet()) {
            int v = getViolationCount(pos, offsetMetaMap.keySet());
            if(v > minViolation) minViolation = v;
        }
        return minViolation;
    }

    private int getViolationCount(BlockPos comp, Collection<BlockPos> positions) {
        int yLevel = comp.getY();
        int amt = 0;
        for (BlockPos other : positions) {
            if(other.getY() != yLevel) amt++;
        }
        return amt;
    }

    @Override
    public String getUnlocalizedName() {
        return "constraint.horizontal";
    }

    @Override
    public double getWeight() {
        return 1;
    }

}
