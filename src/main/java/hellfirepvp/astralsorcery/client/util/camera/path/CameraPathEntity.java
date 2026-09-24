/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera.path;

import hellfirepvp.astralsorcery.client.util.camera.CameraPersistencyFunction;
import hellfirepvp.astralsorcery.client.util.camera.CameraViewEntity;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CameraPathEntity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CameraPathEntity extends CameraViewEntity {

    LinkedList<PathPoint> pathPoints = new LinkedList<>();
    private final Vector3 startVector;
    private int totalPathDuration = 0;

    private Runnable tickFn = () -> {};
    private Runnable stopFn = () -> {};
    final CameraPersistencyFunction persistency = new CameraPersistencyFunction.Simple();

    public CameraPathEntity(Vector3 startVector, Vector3 focus) {
        this.startVector = startVector;
        this.setPosRaw(startVector.getX(), startVector.getY(), startVector.getZ());
        this.xo = getX();
        this.yo = getY();
        this.zo = getZ();
        this.setCameraFocus(focus);
        this.transformToFocusOnPoint(focus, 0, false);
    }

    public void onTick(Runnable fn) {
        this.tickFn = fn;
    }

    public void onStop(Runnable fn) {
        this.stopFn = fn;
    }

    void addPoint(Vector3 point, int ticks) {
        this.pathPoints.addLast(new PathPoint(point, ticks));
        this.totalPathDuration += ticks;
    }

    @Override
    public void onStopTransforming() {
        this.stopFn.run();
    }

    @Override
    public void moveEntityTick(LocalPlayer existingPlayer, int ticksExisted) {
        this.tickFn.run();

        this.setCameraFocus(new Vector3(existingPlayer));
        if (this.tickCount > this.totalPathDuration) {
            this.persistency.setExpired();
        }
        if (this.pathPoints.isEmpty()) {
            this.persistency.setExpired();
        } else {
            Vector3 stepPos = this.queryByTicks(ticksExisted);
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            this.setPosRaw(stepPos.getX(), stepPos.getY(), stepPos.getZ());
        }
    }

    private Vector3 queryByTicks(int ticks) {
        if (ticks <= 0) {
            return startVector;
        }
        int acc = 0;
        PathPoint current = null;
        Vector3 prev;
        for (PathPoint point : pathPoints) {
            int accumulator = acc + point.ticksToGetThere;

            prev = current == null ? startVector : current.dstPoint;
            current = point;

            if (accumulator >= ticks) {
                int interp = current.ticksToGetThere - (accumulator - ticks);
                int dstJump = current.ticksToGetThere;
                return current.dstPoint.copy().subtract(prev).divide(dstJump).multiply(Mth.clamp(interp, 1, dstJump)).add(prev);
            } else {
                acc = accumulator;
            }
        }
        return pathPoints.getLast().dstPoint; //Doesn't happen since the list isn't empty.
    }

    private record PathPoint(Vector3 dstPoint, int ticksToGetThere) {}
}
