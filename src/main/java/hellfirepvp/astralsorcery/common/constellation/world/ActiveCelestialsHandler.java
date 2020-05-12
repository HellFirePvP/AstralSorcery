/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveCelestialsHandler
 * Created by HellFirePvP
 * Date: 03.07.2019 / 13:31
 */
public class ActiveCelestialsHandler {

    public static LinkedList<RenderPosition> availablePositions = new LinkedList<>();

    private Map<IConstellation, RenderPosition> activePositions = new HashMap<>();

    public void updatePositions(LinkedList<IConstellation> activeConstellations) {
        activePositions.clear();
        for (int i = 0; i < Math.min(activeConstellations.size(), availablePositions.size()); i++) {
            activePositions.put(activeConstellations.get(i), availablePositions.get(i));
        }
    }

    public Map<IConstellation, RenderPosition> getCurrentRenderPositions() {
        return Collections.unmodifiableMap(activePositions);
    }

    public Collection<IConstellation> getActiveConstellations() {
        return Collections.unmodifiableCollection(activePositions.keySet());
    }

    //If you found this bit of code:
    //yes. there's a hard cap of max. 15 constellations in the sky at the same time. these are the positions.
    static {
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.9, -0.40,  0.0, 25));
        availablePositions.add(RenderPosition.createRenderInfoFor( 1.0, -0.42,  0.9, 30));
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.0, -0.43,  1.0, 25));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.8, -0.44,  1.0, 30));
        availablePositions.add(RenderPosition.createRenderInfoFor(-1.1, -0.41,  0.0, 25));
        availablePositions.add(RenderPosition.createRenderInfoFor(-1.0, -0.46, -0.9, 30));
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.0, -0.38, -1.0, 25));
        availablePositions.add(RenderPosition.createRenderInfoFor( 1.1, -0.43, -0.9, 30));

        availablePositions.add(RenderPosition.createRenderInfoFor( 0.6, -0.62, -0.1, 15));
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.5, -0.64,  0.4, 20));
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.1, -0.62,  0.6, 15));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.4, -0.66,  0.5, 20));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.5, -0.62,  0.1, 15));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.6, -0.67, -0.4, 20));
        availablePositions.add(RenderPosition.createRenderInfoFor(   0, -0.62, -0.5, 15));
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.4, -0.62, -0.6, 20));
        Collections.shuffle(availablePositions, new Random(0x5F918D128A5A1423L));
    }

    public static class RenderPosition {

        public final Vector3 offset, incU, incV;

        RenderPosition(Vector3 offsetVecUV00, Vector3 vecUV10, Vector3 vecUV01) {
            this.offset = offsetVecUV00;
            this.incU = vecUV10;
            this.incV = vecUV01;
        }

        static RenderPosition createRenderInfoFor(double x, double y, double z, double rSize) {
            double modSize = 0.5 * rSize;
            double fx = x * 100.0D;
            double fy = y * 100.0D;
            double fz = z * 100.0D;
            double d8 = Math.atan2(x, z);
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(x * x + z * z), y);
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);

            double d23 = modSize * d12;
            double d24 = -(modSize * d13);
            double d25 = d24 * d9 - modSize * d10;
            double d26 = modSize * d9 + d24 * d10;
            Vector3 vecUV00 = new Vector3(fx + d25, fy + d23, fz + d26);
            d23 = -(modSize * d12);
            d24 = (modSize * d13);
            d25 = d24 * d9 - modSize * d10;
            d26 = modSize * d9 + d24 * d10;
            Vector3 vecUV10 = new Vector3(fx + d25, fy + d23, fz + d26);
            d23 = modSize * d12;
            d24 = -(modSize * d13);
            d25 = d24 * d9 + modSize * d10;
            d26 = -(modSize * d9) + d24 * d10;
            Vector3 vecUV01 = new Vector3(fx + d25, fy + d23, fz + d26);
            return new RenderPosition(vecUV00, vecUV10, vecUV01);
        }

    }

}
