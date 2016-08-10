package hellfirepvp.astralsorcery.common.ritual.constraints;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstraintDistance
 * Created by HellFirePvP
 * Date: 09.08.2016 / 16:38
 */
public class ConstraintDistance extends RitualConstraint {

    public static final int PENALTY_DST = -6;

    private final int dst, dstSq;

    public ConstraintDistance(int dst) {
        this.dst = dst;
        this.dstSq = dst * dst;
    }

    @Override
    public int getFulfillment(World world, BlockPos central, Map<BlockPos, Integer> offsetMetaMap, Constellation type) {
        int amt = 0;
        for (BlockPos offset : offsetMetaMap.keySet()) {
            for (BlockPos other : offsetMetaMap.keySet()) {
                if(offset.equals(other)) continue;
                if(offset.distanceSq(other) < dstSq) amt += PENALTY_DST;
            }
        }
        return amt;
    }

    @Override
    public String getUnlocalizedName() {
        return "constraint.mindst." + dst;
    }

    @Override
    public double getWeight() {
        return 1;
    }
}
