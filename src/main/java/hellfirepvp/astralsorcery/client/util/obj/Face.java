/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.obj;

import com.mojang.blaze3d.vertex.IVertexBuilder;
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
                    .endVertex();
        }
    }
}