/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.compound;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompoundObjectEffect
 * Created by HellFirePvP
 * Date: 16.02.2017 / 16:34
 */
public abstract class CompoundObjectEffect extends EntityComplexFX {

    @Override
    public final void render(float pTicks) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        render(vb, pTicks);
        tes.draw();
    }

    public abstract ObjectGroup getGroup();

    public abstract void render(VertexBuffer vb, float pTicks);

    public class Face {

        public Vector3 offset;
        public Vector3 vecU, vecV;

        public Face(Vector3 offset, Vector3 vecU, Vector3 vecV) {
            this.offset = offset.clone();
            this.vecU = vecU.clone();
            this.vecV = vecV.clone();
        }

        public VertexBuffer addPositionVertexForUV(VertexBuffer vb, double u, double v) {
            vb.pos(offset.getX() + vecU.getX() * u + vecV.getX() * v,
                    offset.getY() + vecU.getY() * u + vecV.getY() * v,
                    offset.getZ() + vecU.getZ() * u + vecV.getZ() * v);
            return vb;
        }

    }

    public enum ObjectGroup {

        GASEOUS_SPHERE;

        public void prepareGLContext() {
            switch (this) {
                case GASEOUS_SPHERE:
                    break;
            }
        }

        public void revertGLContext() {
            switch (this) {
                case GASEOUS_SPHERE:
                    break;
            }
        }

    }

}
