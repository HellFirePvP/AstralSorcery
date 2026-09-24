/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SphereBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SphereBuilder {

    public static List<TriangleFace> buildFaces(Vector3 axis, int fractionsSplit, int fractionsCircle) {
        List<TriangleFace> sphereFaces = new ArrayList<>();
        Vector3 centerPerp = axis.copy().perpendicular();
        double degSplit =       180D / ((double) fractionsSplit);
        double degCircleSplit = 360D / ((double) fractionsCircle);
        double degCircleOffsetShifted = degCircleSplit / 2D;
        boolean shift = false;

        Vector3[] prevArray = new Vector3[fractionsCircle];
        Vector3 prev = axis.copy();
        Arrays.fill(prevArray, prev.copy());
        for (int i = 1; i <= fractionsSplit; i++) {
            Vector3 splitVec = axis.copy().rotate(Math.toRadians(degSplit * i), centerPerp);

            Vector3[] circlePositions = new Vector3[fractionsCircle];
            for (int j = 0; j < fractionsCircle; j++) {
                double deg = shift ? degCircleOffsetShifted : 0;
                deg += degCircleSplit * j;
                circlePositions[j] = splitVec.copy().rotate(Math.toRadians(deg), axis);
            }

            for (int k = 0; k < fractionsCircle; k++) {
                int prevIndex = shift ? k : k - 1;
                if (prevIndex < 0) {
                    prevIndex = fractionsCircle - 1;
                }
                int nextIndex = shift ? k + 1 : k;
                if (nextIndex >= fractionsCircle) {
                    nextIndex = 0;
                }
                sphereFaces.add(new TriangleFace(prevArray[prevIndex], prevArray[nextIndex], circlePositions[k]));
                int nextCircle = k + 1;
                if (nextCircle >= fractionsCircle) {
                    nextCircle = 0;
                }
                sphereFaces.add(new TriangleFace(circlePositions[k], prevArray[nextIndex], circlePositions[nextCircle]));
            }

            prevArray = circlePositions;
            shift = !shift;
        }
        return sphereFaces;
    }

    public static class TriangleFace {

        private final Vector3 v1;
        private final Vector3 v2;
        private final Vector3 v3;

        private TriangleFace(Vector3 v1, Vector3 v2, Vector3 v3) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }

        public Vector3 getV1() {
            return v1;
        }

        public Vector3 getV2() {
            return v2;
        }

        public Vector3 getV3() {
            return v3;
        }
    }
}
