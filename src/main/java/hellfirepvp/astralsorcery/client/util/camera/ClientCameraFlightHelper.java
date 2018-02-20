/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientCameraFlightHelper
 * Created by HellFirePvP
 * Date: 15.12.2016 / 22:19
 */
public class ClientCameraFlightHelper {

    public static CameraFlightBuilder builder(Vector3 start, Vector3 cameraFocus) {
        return new CameraFlightBuilder(start, cameraFocus);
    }

    public static class CameraFlightBuilder {

        private final CameraFlight flight;

        private CameraFlightBuilder(Vector3 start, Vector3 cameraFocus) {
            this.flight = new CameraFlight(start, cameraFocus, null);
        }

        public CameraFlightBuilder(CameraFlight flight) {
            this.flight = flight;
        }

        public CameraFlightBuilder addPoint(Vector3 nextPoint, int ticksToFlyThere) {
            if(ticksToFlyThere < 0) {
                AstralSorcery.log.warn("[AstralSorcery] Tried to add a point with negative tick-timespan to a camera flight. Skipping...");
                return this;
            }
            this.flight.addPoint(nextPoint, ticksToFlyThere);
            return this;
        }

        public CameraFlightBuilder addCircularPoints(Vector3 centerOffset, double radius, int amountOfPointsOnCircle, int ticksBetweenEachPoint) {
            return addCircularPoints(centerOffset, (deg) -> radius, amountOfPointsOnCircle, ticksBetweenEachPoint);
        }

        public CameraFlightBuilder addCircularPoints(Vector3 centerOffset, DynamicRadiusGetter radius, int amountOfPointsOnCircle, int ticksBetweenEachPoint) {
            if(ticksBetweenEachPoint < 0) {
                AstralSorcery.log.warn("[AstralSorcery] Tried to add a point with negative tick-timespan to a camera flight. Skipping...");
                return this;
            }
            double degPerPoint = 360D / ((double) amountOfPointsOnCircle);
            for (int i = 0; i < amountOfPointsOnCircle; i++) {
                double deg = i * degPerPoint;
                Vector3 point = Vector3.RotAxis.Y_AXIS.clone().perpendicular().normalize().multiply(radius.getRadius(deg)).rotate(Math.toRadians(deg), Vector3.RotAxis.Y_AXIS).add(centerOffset);
                addPoint(point, ticksBetweenEachPoint);
            }
            return this;
        }

        public CameraFlightBuilder copy() {
            return new CameraFlightBuilder(this.flight.copy());
        }

        public CameraFlightBuilder setTickDelegate(TickDelegate delegate) {
            this.flight.setTickDelegate(delegate);
            return this;
        }

        public CameraFlightBuilder setStopDelegate(StopDelegate delegate) {
            this.flight.setStopDelegate(delegate);
            return this;
        }

        public CameraFlight finishAndStart() {
            if(this.flight.flightPoints.size() <= 0) {
                AstralSorcery.log.warn("[AstralSorcery] Tried to start a camera flight without any points! Skipping...");
                return null;
            }

            ClientCameraManager.CameraTransformerRenderReplacement repl = new ClientCameraManager.CameraTransformerRenderReplacement(this.flight, this.flight);
            ClientCameraManager.getInstance().addTransformer(repl);
            return this.flight;
        }

    }

    public static interface StopDelegate {

        public void onCameraFlightStop();

    }

    public static interface TickDelegate {

        public void tick(ClientCameraManager.EntityRenderViewReplacement renderView, ClientCameraManager.EntityClientReplacement focusedEntity);

    }

    public static class CameraFlight extends ClientCameraManager.EntityRenderViewReplacement implements ClientCameraManager.PersistencyFunction {

        private LinkedList<FlightPoint> flightPoints = new LinkedList<>();
        private final Vector3 startVector, focus;

        private TickDelegate delegate;
        private StopDelegate stopDelegate;

        private int totalTickDuration = 0;
        private boolean expired = false;
        private boolean stopped = false;

        private CameraFlight(Vector3 startPoint, Vector3 focusPoint, @Nullable TickDelegate tick) {
            this.startVector = startPoint;
            this.focus = focusPoint;
            this.posX = startPoint.getX();
            this.posY = startPoint.getY();
            this.posZ = startPoint.getZ();
            this.prevPosX = posX;
            this.prevPosY = posY;
            this.prevPosZ = posZ;
            this.delegate = tick;
            setCameraFocus(focusPoint);
            transformToFocusOnPoint(focusPoint, 0, false);
        }

        public void setTickDelegate(TickDelegate delegate) {
            this.delegate = delegate;
        }

        private void addPoint(Vector3 point, int ticks) {
            this.flightPoints.addLast(new FlightPoint(point, ticks));
            this.totalTickDuration += ticks;
        }

        @Override
        public void moveEntityTick(ClientCameraManager.EntityRenderViewReplacement entity, ClientCameraManager.EntityClientReplacement replacement, int ticksExisted) {
            if(delegate != null) {
                delegate.tick(entity, replacement);
            }
            setCameraFocus(Vector3.atEntityCenter(replacement));
            this.expired = this.ticksExisted > totalTickDuration;
            if(flightPoints.isEmpty()) {
                this.expired = true;
            } else {
                Vector3 position = queryByTicks(ticksExisted);
                this.prevPosX = this.posX;
                this.prevPosY = this.posY;
                this.prevPosZ = this.posZ;
                this.posX = position.getX();
                this.posY = position.getY();
                this.posZ = position.getZ();
            }
        }

        @Override
        public void onStopTransforming() {
            if(stopDelegate != null && Minecraft.getMinecraft().world != null) {
                stopDelegate.onCameraFlightStop();
            }
        }

        //Prev-Next tuple
        private Vector3 queryByTicks(int ticks) {
            if(ticks <= 0) {
                return startVector;
            }
            int acc = 0;
            FlightPoint current = null;
            Vector3 prev;
            for (FlightPoint point : flightPoints) {
                int accumulator = acc + point.ticksToGetThere;

                prev = current == null ? startVector : current.dstPoint;
                current = point;

                if(accumulator >= ticks) {
                    int interp = current.ticksToGetThere - (accumulator - ticks);
                    int dstJump = current.ticksToGetThere;
                    return current.dstPoint.clone().subtract(prev).divide(dstJump).multiply(MathHelper.clamp(interp, 1, dstJump)).add(prev);
                } else {
                    acc = accumulator;
                }
            }
            return flightPoints.getLast().dstPoint; //Doesn't happen since the list isn't empty.
        }

        public boolean isExpired() {
            return expired;
        }

        public void setExpired() {
            this.expired = true;
        }

        public void forceStop() {
            this.stopped = true;
            setExpired();
        }

        public boolean wasForciblyStopped() {
            return stopped;
        }

        @Override
        public boolean needsRemoval() {
            return expired;
        }

        private CameraFlight copy() {
            CameraFlight c = new CameraFlight(this.startVector, this.focus, this.delegate);
            for (FlightPoint fp : this.flightPoints) {
                c.flightPoints.addLast(new FlightPoint(fp.dstPoint, fp.ticksToGetThere));
            }
            c.totalTickDuration = this.totalTickDuration;
            return c;
        }

        public void setStopDelegate(StopDelegate delegate) {
            this.stopDelegate = delegate;
        }

        private static class FlightPoint {

            private final Vector3 dstPoint;
            private final int ticksToGetThere;

            private FlightPoint(Vector3 dstPoint, int ticksToGetThere) {
                this.dstPoint = dstPoint;
                this.ticksToGetThere = ticksToGetThere;
            }
        }

    }

    public static interface DynamicRadiusGetter {

        public double getRadius(double degree);

        public static DynamicRadiusGetter dyanmicIncrease(double base, double incPerStep) {
            return new DynamicRadiusGetter() {

                private double baseDst = base;
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
