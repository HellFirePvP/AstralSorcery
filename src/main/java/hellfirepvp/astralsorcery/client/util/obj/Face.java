/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.obj;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * HellFirePvP@Admin
 * Date: 15.06.2015 / 00:07
 * on WingsExMod
 * Face
 */
public class Face {
    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    //public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;

    @SideOnly(Side.CLIENT)
    public void addFaceForRender(BufferBuilder vb) {
        addFaceForRender(vb, 0.0005F);
    }

    @SideOnly(Side.CLIENT)
    public void addFaceForRender(BufferBuilder vb, float textureOffset) {
        /*if (faceNormal == null) {
            faceNormal = this.calculateFaceNormal();
        }

        vb.normal(faceNormal.x, faceNormal.y, faceNormal.z);*/

        float averageU = 0F;
        float averageV = 0F;

        if ((textureCoordinates != null) && (textureCoordinates.length > 0)) {
            for (int i = 0; i < textureCoordinates.length; ++i) {
                averageU += textureCoordinates[i].u;
                averageV += textureCoordinates[i].v;
            }

            averageU = averageU / textureCoordinates.length;
            averageV = averageV / textureCoordinates.length;
        }

        float offsetU, offsetV;

        for (int i = 0; i < vertices.length; ++i) {

            if ((textureCoordinates != null) && (textureCoordinates.length > 0)) {
                offsetU = textureOffset;
                offsetV = textureOffset;

                if (textureCoordinates[i].u > averageU) {
                    offsetU = -offsetU;
                }
                if (textureCoordinates[i].v > averageV) {
                    offsetV = -offsetV;
                }

                vb.pos(vertices[i].x, vertices[i].y, vertices[i].z).tex(textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV).endVertex();
                //tessellator.addVertexWithUV(vertices[i].x, vertices[i].y, vertices[i].z, textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV);
            } else {
                vb.pos(vertices[i].x, vertices[i].y, vertices[i].z).endVertex();
                //tessellator.addVertex(vertices[i].x, vertices[i].y, vertices[i].z);
            }

        }
    }

    public Vertex calculateFaceNormal() {
        Vector3 v1 = new Vector3(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vector3 v2 = new Vector3(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
        Vector3 normalVector = v1.crossProduct(v2).normalize();

        return new Vertex((float) normalVector.getX(), (float) normalVector.getY(), (float) normalVector.getZ());
    }
}