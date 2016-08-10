package hellfirepvp.astralsorcery.common.ritual.constraints;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RitualConstraint
 * Created by HellFirePvP
 * Date: 09.08.2016 / 15:50
 */
public abstract class RitualConstraint {

    public static final RitualConstraint SYMMETRY = new ConstraintSymmetry();
    public static final RitualConstraint ASYMMETRY = new ConstraintAsymmetry();
    public static final RitualConstraint HORIZONTAL = new ConstraintHorizontal();
    public static final RitualConstraint DISTANCE_2 = new ConstraintDistance(2);
    public static final RitualConstraint DISTANCE_3 = new ConstraintDistance(3);

    public static final SizeConstraint SIZE_4 = new GeneralSizeConstraint(4);
    public static final SizeConstraint SIZE_5 = new GeneralSizeConstraint(5);
    public static final SizeConstraint SIZE_6 = new GeneralSizeConstraint(6);
    public static final SizeConstraint SIZE_7 = new GeneralSizeConstraint(7);
    public static final SizeConstraint SIZE_8 = new GeneralSizeConstraint(8);

    //0 = nothing, >= 1 enhancement, <= -1 worse
    public abstract int getFulfillment(World world, BlockPos central, Map<BlockPos, Integer> offsetMetaMap, Constellation type);

    public abstract String getUnlocalizedName();

    public abstract double getWeight();

}
