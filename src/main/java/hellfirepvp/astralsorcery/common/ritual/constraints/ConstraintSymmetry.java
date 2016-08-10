package hellfirepvp.astralsorcery.common.ritual.constraints;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstraintSymmetry
 * Created by HellFirePvP
 * Date: 09.08.2016 / 16:14
 */
public class ConstraintSymmetry extends RitualConstraint {

    public static final int PENALTY_MISSING = -1;
    public static final int PENALTY_MISMATCH = -2;
    public static final int REWARD_MATCH = 1;

    @Override
    public int getFulfillment(World world, BlockPos central, Map<BlockPos, Integer> offsetMetaMap, Constellation type) {
        int amt = 0;
        for (BlockPos offset : offsetMetaMap.keySet()) {
            int meta = offsetMetaMap.get(offset);
            amt += checkSymmetry(offset, meta, offsetMetaMap);
        }
        return amt;
    }

    private int checkSymmetry(BlockPos offset, int meta, Map<BlockPos, Integer> offsetMetaMap) {
        for (BlockPos offsetPos : mirrorPos(offset)) {
            if(!offsetMetaMap.containsKey(offsetPos)) return PENALTY_MISSING;
            if(offsetMetaMap.get(offsetPos) != meta) return PENALTY_MISMATCH;
        }
        return REWARD_MATCH;
    }

    @Override
    public String getUnlocalizedName() {
        return "constraint.symmetry";
    }

    @Override
    public double getWeight() {
        return 1.0 / 8.0;
    }

    public List<BlockPos> mirrorPos(BlockPos offsetPos) {
        List<BlockPos> out = new ArrayList<>(8);
        out.add(offsetPos);
        out.add(new BlockPos(-offsetPos.getX(),  offsetPos.getY(),  offsetPos.getZ()));
        out.add(new BlockPos( offsetPos.getX(), -offsetPos.getY(),  offsetPos.getZ()));
        out.add(new BlockPos( offsetPos.getX(),  offsetPos.getY(), -offsetPos.getZ()));
        out.add(new BlockPos(-offsetPos.getX(), -offsetPos.getY(),  offsetPos.getZ()));
        out.add(new BlockPos( offsetPos.getX(), -offsetPos.getY(), -offsetPos.getZ()));
        out.add(new BlockPos(-offsetPos.getX(),  offsetPos.getY(), -offsetPos.getZ()));
        out.add(new BlockPos(-offsetPos.getX(), -offsetPos.getY(), -offsetPos.getZ()));
        return out;
    }

}
