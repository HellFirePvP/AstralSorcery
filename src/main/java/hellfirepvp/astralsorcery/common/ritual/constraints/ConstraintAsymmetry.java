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
 * Class: ConstraintAsymmetry
 * Created by HellFirePvP
 * Date: 10.08.2016 / 10:27
 */
public class ConstraintAsymmetry extends RitualConstraint {

    public static final int PENALTY_BLOCK = -2;
    public static final int REWARD_MATCH = 1;

    @Override
    public int getFulfillment(World world, BlockPos central, Map<BlockPos, Integer> offsetMetaMap, Constellation type) {
        int amt = 0;
        for (BlockPos offset : offsetMetaMap.keySet()) {
            amt += checkSymmetry(world, offset);
        }
        return amt;
    }

    private int checkSymmetry(World world, BlockPos offset) {
        for (BlockPos offsetPos : mirrorPos(offset)) {
            if(!world.isAirBlock(offsetPos)) return PENALTY_BLOCK;
        }
        return REWARD_MATCH;
    }

    @Override
    public String getUnlocalizedName() {
        return "constraint.asymmetry";
    }

    @Override
    public double getWeight() {
        return 1;
    }

    public List<BlockPos> mirrorPos(BlockPos offsetPos) {
        List<BlockPos> out = new ArrayList<>(7);
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
