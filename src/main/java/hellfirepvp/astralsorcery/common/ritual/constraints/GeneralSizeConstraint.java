package hellfirepvp.astralsorcery.common.ritual.constraints;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GeneralSizeConstraint
 * Created by HellFirePvP
 * Date: 09.08.2016 / 15:59
 */
public class GeneralSizeConstraint extends SizeConstraint {

    private final int rad;

    public GeneralSizeConstraint(int radius) {
        this.rad = radius;
    }

    @Override
    public int getStructureSize() {
        return rad;
    }

    @Override
    public int getFulfillment(World world, BlockPos central, Map<BlockPos, Integer> offsetMetaMap, Constellation type) {
        int amt = 0;
        for (BlockPos offset : offsetMetaMap.keySet()) {
            if(offset.getX() > rad || offset.getX() < -rad ||
                    offset.getY() > rad || offset.getY() < -rad ||
                    offset.getZ() > rad || offset.getZ() < -rad) {
                amt -= 1;
            }
        }
        return amt;
    }

    @Override
    public String getUnlocalizedName() {
        return "constraint.maxsize." + rad;
    }

    @Override
    public double getWeight() {
        return 1;
    }
}
