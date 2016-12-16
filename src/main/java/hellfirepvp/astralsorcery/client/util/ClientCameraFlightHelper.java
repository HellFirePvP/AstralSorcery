package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.MathHelper;

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
            this.flight = new CameraFlight(start, cameraFocus);
        }

        public CameraFlightBuilder(CameraFlight flight) {
            this.flight = flight;
        }

        public CameraFlightBuilder addPoint(Vector3 nextPoint, int ticksToFlyThere) {
            if(ticksToFlyThere < 0) {
                AstralSorcery.log.warn("Tried to add a point with negative tick-timespan to a camera flight. Skipping...");
                return this;
            }
            this.flight.addPoint(nextPoint, ticksToFlyThere);
            return this;
        }

        public CameraFlightBuilder copy() {
            return new CameraFlightBuilder(this.flight.copy());
        }

        public void finishAndStart() {
            if(this.flight.flightPoints.size() <= 0) {
                AstralSorcery.log.warn("Tried to start a camera flight without any points! Skipping...");
                return;
            }
            ClientCameraManager.getInstance().addTransformer(new ClientCameraManager.CameraTransformerRenderReplacement(this.flight, this.flight));
        }

    }

    private static class CameraFlight extends ClientCameraManager.EntityRenderViewReplacement implements ClientCameraManager.PersistencyFunction {

        private LinkedList<FlightPoint> flightPoints = new LinkedList<>();
        private final Vector3 startVector, focus;

        private int totalTickDuration = 0;
        private boolean expired = false;

        private CameraFlight(Vector3 startPoint, Vector3 focusPoint) {
            this.startVector = startPoint;
            this.focus = focusPoint;
            this.posX = startPoint.getX();
            this.posY = startPoint.getY();
            this.posZ = startPoint.getZ();
            this.prevPosX = posX;
            this.prevPosY = posY;
            this.prevPosZ = posZ;
            setCameraFocus(focusPoint);
            transformToFocusOnPoint(focusPoint, 0, false);
        }

        private void addPoint(Vector3 point, int ticks) {
            this.flightPoints.addLast(new FlightPoint(point, ticks));
            this.totalTickDuration += ticks;
        }

        @Override
        public void moveEntityTick(ClientCameraManager.EntityRenderViewReplacement entity, int ticksExisted) {
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

        @Override
        public boolean needsRemoval() {
            return expired;
        }

        private CameraFlight copy() {
            CameraFlight c = new CameraFlight(this.startVector, this.focus);
            for (FlightPoint fp : this.flightPoints) {
                c.flightPoints.addLast(new FlightPoint(fp.dstPoint, fp.ticksToGetThere));
            }
            c.totalTickDuration = this.totalTickDuration;
            return c;
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

}
