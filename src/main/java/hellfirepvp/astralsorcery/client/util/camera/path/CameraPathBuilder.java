/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera.path;

import hellfirepvp.astralsorcery.client.util.camera.CameraManager;
import hellfirepvp.astralsorcery.client.util.camera.CameraTransformerPlayerFocus;
import hellfirepvp.astralsorcery.client.util.camera.RevertableCameraTransformer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CameraPathBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CameraPathBuilder {

    private final CameraPathEntity path;

    private CameraPathBuilder(Vector3 start, Vector3 cameraFocus) {
        this.path = new CameraPathEntity(start, cameraFocus);
    }

    public static CameraPathBuilder builder(Vector3 start, Vector3 cameraFocus) {
        return new CameraPathBuilder(start, cameraFocus);
    }

    public CameraPathBuilder addPoint(Vector3 nextPoint, int ticksToFlyThere) {
        if (ticksToFlyThere < 0) {
            return this;
        }
        this.path.addPoint(nextPoint, ticksToFlyThere);
        return this;
    }

    public CameraPathBuilder addCircularPoints(Vector3 centerOffset, double radius, int amountOfPointsOnCircle, int ticksBetweenEachPoint) {
        return addCircularPoints(centerOffset, (deg) -> radius, amountOfPointsOnCircle, ticksBetweenEachPoint);
    }

    public CameraPathBuilder addCircularPoints(Vector3 centerOffset, DynamicRadiusGetter radiusFn, int amountOfPointsOnCircle, int ticksBetweenEachPoint) {
        if (ticksBetweenEachPoint < 0) {
            return this;
        }
        double degPerPoint = 360D / ((double) amountOfPointsOnCircle);
        for (int i = 0; i < amountOfPointsOnCircle; i++) {
            double deg = i * degPerPoint;
            Vector3 point = Vector3.RotAxis.Y_AXIS.getVector().perpendicular().normalize().multiply(radiusFn.getRadius(deg)).rotate(Math.toRadians(deg), Vector3.RotAxis.Y_AXIS).add(centerOffset);
            addPoint(point, ticksBetweenEachPoint);
        }
        return this;
    }

    public CameraPathBuilder setTickDelegate(Runnable delegate) {
        this.path.onTick(delegate);
        return this;
    }

    public CameraPathBuilder setStopDelegate(Runnable delegate) {
        this.path.onStop(delegate);
        return this;
    }

    public CameraTransformerPlayerFocus finishAndStart() {
        if (this.path.pathPoints.isEmpty()) {
            throw new IllegalArgumentException();
        }

        CameraTransformerPlayerFocus cameraTransformer = new CameraTransformerPlayerFocus(this.path, this.path.persistency);
        CameraManager.getInstance().addTransformer(cameraTransformer);
        return cameraTransformer;
    }

    public interface DynamicRadiusGetter {

        double getRadius(double degree);

        static DynamicRadiusGetter dyanmicIncrease(double base, double incPerStep) {
            return new DynamicRadiusGetter() {

                private final double baseDst = base;
                private int count = 0;

                @Override
                public double getRadius(double degree) {
                    double rad = baseDst + count * incPerStep;
                    count++;
                    return rad;
                }
            };
        }

    }
}
