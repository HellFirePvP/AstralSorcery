/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.obj;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * HellFirePvP@Admin
 * Date: 15.06.2015 / 00:07
 * on WingsExMod
 * Face
 */
public class Face {

    Vertex[] vertices;
    Vertex[] vertexNormals;
    Vertex faceNormal;
    TextureCoordinate[] textureCoordinates;

    @OnlyIn(Dist.CLIENT)
    void addFaceForRender(IVertexBuilder vb) {
        addFaceForRender(vb, 0.0004F);
    }

    @OnlyIn(Dist.CLIENT)
    void addFaceForRender(IVertexBuilder vb, float textureOffset) {
        float averageU = 0F;
        float averageV = 0F;

        for (TextureCoordinate textureCoordinate : textureCoordinates) {
            averageU += textureCoordinate.u;
            averageV += textureCoordinate.v;
        }

        averageU = averageU / textureCoordinates.length;
        averageV = averageV / textureCoordinates.length;

        for (int i = 0; i < vertices.length; ++i) {
            float offsetU = textureOffset;
            float offsetV = textureOffset;

            if (textureCoordinates[i].u > averageU) {
                offsetU = -offsetU;
            }
            if (textureCoordinates[i].v > averageV) {
                offsetV = -offsetV;
            }

            vb.pos(vertices[i].x, vertices[i].y, vertices[i].z)
                    .color(255, 255, 255, 255)
                    .tex(textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV)
                    .normal(faceNormal.x, faceNormal.y, faceNormal.z)
                    .endVertex();
        }
    }

    Vertex calculateFaceNormal() {
        Vector3 v1 = new Vector3(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vector3 v2 = new Vector3(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
        Vector3 normalVector = v1.crossProduct(v2).normalize();

        return new Vertex((float) normalVector.getX(), (float) normalVector.getY(), (float) normalVector.getZ());
    }
}