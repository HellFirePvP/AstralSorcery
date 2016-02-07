package hellfire.astralSorcery.api.constellation;

import hellfire.astralSorcery.common.util.Vector3;

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

    public float getShowupChance();

    public static class RInformation {

        public final Vector3 offset, incU, incV;
        public double renderSize;

        public RInformation(Vector3 offsetVecUV00, Vector3 vecUV10, Vector3 vecUV01, double rSize) {
            this.offset = offsetVecUV00;
            this.incU = vecUV10;
            this.incV = vecUV01;
            this.renderSize = rSize;
        }

    }

}
