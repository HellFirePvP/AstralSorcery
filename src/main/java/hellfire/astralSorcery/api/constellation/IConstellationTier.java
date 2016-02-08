package hellfire.astralSorcery.api.constellation;

import hellfire.astralSorcery.common.util.Vector3;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 21:48
 */
public interface IConstellationTier {

    public int tierNumber();

    public List<IConstellation> getConstellations();

    public void addConstellation(IConstellation constellation);

    public RInformation getRenderInformation();

    public Color calcRenderColor();

    public float getShowupChance();

    public static class RInformation {

        public final Vector3 offset, incU, incV;

        public RInformation(Vector3 offsetVecUV00, Vector3 vecUV10, Vector3 vecUV01) {
            this.offset = offsetVecUV00;
            this.incU = vecUV10;
            this.incV = vecUV01;
        }

        // #WeCareAboutRenderPerformance
        // There are things in life, you just don't need to understand. That's one of them.
        // Pass coordinates between -1 and 1 size may be between 1 and ~6 (don't ask) but try yourself if needed.
        public static RInformation createRenderInfoFor(double x, double y, double z, double rSize) {
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
            return new RInformation(vecUV00, vecUV10, vecUV01);
        }

    }

}
