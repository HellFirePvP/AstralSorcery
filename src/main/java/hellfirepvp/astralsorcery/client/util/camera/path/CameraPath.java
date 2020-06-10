package hellfirepvp.astralsorcery.client.util.camera.path;

import hellfirepvp.astralsorcery.client.util.camera.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CameraPath
 * Created by HellFirePvP
 * Date: 02.12.2019 / 19:56
 */
public class CameraPath extends EntityCameraRenderView implements ICameraPersistencyFunction {

    LinkedList<PathPoint> pathPoints = new LinkedList<>();
    private final Vector3 startVector, focus;

    private ICameraTickListener delegate;
    private ICameraStopListener stopDelegate;

    private int totalTickDuration = 0;
    private boolean expired = false;
    private boolean stopped = false;

    CameraPath(Vector3 startPoint, Vector3 focusPoint, @Nullable ICameraTickListener tick) {
        this.startVector = startPoint;
        this.focus = focusPoint;
        this.setRawPosition(startPoint.getX(), startPoint.getY(), startPoint.getZ());
        this.prevPosX = getPosX();
        this.prevPosY = getPosY();
        this.prevPosZ = getPosZ();
        this.delegate = tick;
        setCameraFocus(focusPoint);
        transformToFocusOnPoint(focusPoint, 0, false);
    }

    public void setTickListener(ICameraTickListener delegate) {
        this.delegate = delegate;
    }

    public void setStopListener(ICameraStopListener delegate) {
        this.stopDelegate = delegate;
    }

    void addPoint(Vector3 point, int ticks) {
        this.pathPoints.addLast(new PathPoint(point, ticks));
        this.totalTickDuration += ticks;
    }

    @Override
    public void moveEntityTick(EntityCameraRenderView entity, EntityClientReplacement replacement, int ticksExisted) {
        if (delegate != null) {
            delegate.onCameraTick(entity, replacement);
        }
        setCameraFocus(Vector3.atEntityCenter(replacement));
        this.expired = this.ticksExisted > totalTickDuration;
        if (pathPoints.isEmpty()) {
            this.expired = true;
        } else {
            Vector3 position = queryByTicks(ticksExisted);
            this.prevPosX = this.getPosX();
            this.prevPosY = this.getPosY();
            this.prevPosZ = this.getPosZ();
            this.setRawPosition(position.getX(), position.getY(), position.getZ());
        }
    }

    @Override
    public void onStopTransforming() {
        if (stopDelegate != null && Minecraft.getInstance().world != null) {
            stopDelegate.onCameraStop();
        }
    }
    //Prev-Next tuple

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
                return current.dstPoint.clone().subtract(prev).divide(dstJump).multiply(MathHelper.clamp(interp, 1, dstJump)).add(prev);
            } else {
                acc = accumulator;
            }
        }
        return pathPoints.getLast().dstPoint; //Doesn't happen since the list isn't empty.
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void setExpired() {
        this.expired = true;
    }

    @Override
    public void forceStop() {
        this.stopped = true;
        setExpired();
    }

    @Override
    public boolean wasForciblyStopped() {
        return stopped;
    }

    CameraPath copy() {
        CameraPath c = new CameraPath(this.startVector, this.focus, this.delegate);
        for (PathPoint fp : this.pathPoints) {
            c.pathPoints.addLast(new PathPoint(fp.dstPoint, fp.ticksToGetThere));
        }
        c.totalTickDuration = this.totalTickDuration;
        return c;
    }

    private static class PathPoint {

        private final Vector3 dstPoint;
        private final int ticksToGetThere;

        PathPoint(Vector3 dstPoint, int ticksToGetThere) {
            this.dstPoint = dstPoint;
            this.ticksToGetThere = ticksToGetThere;
        }
    }
}
