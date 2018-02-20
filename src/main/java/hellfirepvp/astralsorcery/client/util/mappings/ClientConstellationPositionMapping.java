/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.mappings;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientConstellationPositionMapping
 * Created by HellFirePvP
 * Date: 22.11.2016 / 13:26
 */
@SideOnly(Side.CLIENT)
public class ClientConstellationPositionMapping {

    public static LinkedList<RenderPosition> availablePositions = new LinkedList<>();

    private Map<IConstellation, RenderPosition> activePositions = new HashMap<>();

    //Collection should be sorted: Major ones first, Minor ones later.
    public void updatePositions(LinkedList<IConstellation> activeConstellations) {
        activePositions.clear();
        for (int i = 0; i < Math.min(activeConstellations.size(), availablePositions.size()); i++) {
            activePositions.put(activeConstellations.get(i), availablePositions.get(i));
        }
    }

    public Map<IConstellation, RenderPosition> getCurrentRenderPositions() {
        return Collections.unmodifiableMap(activePositions);
    }

    static {
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.2,  -0.2,     0,   5));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.2,  -0.2,  -0.05,  5));
        availablePositions.add(RenderPosition.createRenderInfoFor(   0,  -0.25, -0.2,   8));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.4,  -0.6,   0.5,  18));
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.3,  -0.5,   0.5,  19));
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.15, -0.2,  -0.1,   5));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.05, -0.3,   0.4,  10));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.3,  -0.3,   0.1,  10));
        availablePositions.add(RenderPosition.createRenderInfoFor(-0.3,  -0.4,  -0.35, 15));
        availablePositions.add(RenderPosition.createRenderInfoFor( 0.4,  -0.4,   0.2,  15));
    }

    public static class RenderPosition {

        public final Vector3 offset, incU, incV;

        public RenderPosition(Vector3 offsetVecUV00, Vector3 vecUV10, Vector3 vecUV01) {
            this.offset = offsetVecUV00;
            this.incU = vecUV10;
            this.incV = vecUV01;
        }

        public static RenderPosition createRenderInfoFor(double x, double y, double z, double rSize) {
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
