package hellfirepvp.astralsorcery.common.util;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Axis
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:57
 */
public class Axis {

    public static final Axis X_AXIS = new Axis(new Vector3(1, 0, 0));
    public static final Axis Y_AXIS = new Axis(new Vector3(0, 1, 0));
    public static final Axis Z_AXIS = new Axis(new Vector3(0, 0, 1));

    private Vector3 axis;

    public Axis(Vector3 axis) {
        this.axis = axis;
    }

    public static Axis persisentRandomAxis() {
        //Actually quite important to use only Y-positive here since if we
        //would use negative y, the axis may turn counter-
        //clockwise, what's intended to be set in another variable, which
        //may cause bugs if we want to use only clockwise axis'.
        return new Axis(Vector3.positiveYRandom());
    }

    public Vector3 getAxis() {
        return axis.clone();
    }

}
