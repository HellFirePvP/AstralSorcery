/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingDrawUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:27
 */
public class RenderingDrawUtils {

    public static void renderFacingFullQuadVB(BufferBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, int r, int g, int b, float alpha) {
        renderFacingQuadVB(vb, px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1, r, g, b, alpha);
    }

    public static void renderFacingQuadVB(BufferBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength, int r, int g, int b, float alpha) {
        Vector3 pos = new Vector3(px, py, pz);

        RenderInfo ri = RenderInfo.getInstance();
        ActiveRenderInfo ari = ri.getARI();

        float arX =  ri.getRotationX();
        float arZ =  ri.getRotationZ();
        float arYZ = ri.getRotationYZ();
        float arXY = ri.getRotationXY();
        float arXZ = ri.getRotationXZ();

        Vec3d view = ari.getProjectedView();
        Vec3d look = ari.getLookDirection();

        float cR = MathHelper.clamp(r / 255F, 0F, 1F);
        float cG = MathHelper.clamp(g / 255F, 0F, 1F);
        float cB = MathHelper.clamp(b / 255F, 0F, 1F);

        Vector3 iPos = new Vector3(view);
        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            float cAngle = MathHelper.cos(angle * 0.5F);
            float cAngleSq = cAngle * cAngle;

            Vector3 vAngle = new Vector3(
                    MathHelper.sin(angle * 0.5F) * look.x,
                    MathHelper.sin(angle * 0.5F) * look.y,
                    MathHelper.sin(angle * 0.5F) * look.z);

            v1 = vAngle.clone()
                    .multiply(2 * v1.dot(vAngle))
                    .add(v1.clone().multiply(cAngleSq - vAngle.dot(vAngle)))
                    .add(vAngle.clone().crossProduct(v1.clone().multiply(2 * cAngle)));
            v2 = vAngle.clone()
                    .multiply(2 * v2.dot(vAngle))
                    .add(v2.clone().multiply(cAngleSq - vAngle.dot(vAngle)))
                    .add(vAngle.clone().crossProduct(v2.clone().multiply(2 * cAngle)));
            v3 = vAngle.clone()
                    .multiply(2 * v3.dot(vAngle))
                    .add(v3.clone().multiply(cAngleSq - vAngle.dot(vAngle)))
                    .add(vAngle.clone().crossProduct(v3.clone().multiply(2 * cAngle)));
            v4 = vAngle.clone()
                    .multiply(2 * v4.dot(vAngle))
                    .add(v4.clone().multiply(cAngleSq - vAngle.dot(vAngle)))
                    .add(vAngle.clone().crossProduct(v4.clone().multiply(2 * cAngle)));
        }

        pos.clone().add(v1).subtract(iPos).drawPos(vb).tex(u + uLength,           v + vLength).color(cR, cG, cB, alpha).endVertex();
        pos.clone().add(v2).subtract(iPos).drawPos(vb).tex(u + uLength, v).color(cR, cG, cB, alpha).endVertex();
        pos.clone().add(v3).subtract(iPos).drawPos(vb).tex(u, v          ).color(cR, cG, cB, alpha).endVertex();
        pos.clone().add(v4).subtract(iPos).drawPos(vb).tex(u,           v + vLength).color(cR, cG, cB, alpha).endVertex();
    }

    public static void renderTexturedCubeCentralColor(BufferContext buf, double size,
                                                                  double u, double v, double uLength, double vLength,
                                                                  float cR, float cG, float cB, float cA) {
        double half = size / 2D;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        buf.pos(-half, -half, -half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half,  half,  half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half, -half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half, -half).tex(u, v).color(cR, cG, cB, cA).endVertex();

        buf.pos( half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u, v).color(cR, cG, cB, cA).endVertex();

        buf.pos( half, -half, -half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half, -half,  half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();

        buf.draw();
    }

    public static void renderTexturedCubeCentralWithLightAndColor(BufferContext buf, double size,
                                                                  double u, double v, double uLength, double vLength,
                                                                  int lX, int lY,
                                                                  float cR, float cG, float cB, float cA) {
        double half = size / 2D;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        buf.pos(-half, -half, -half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half, -half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half,  half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half,  half,  half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half, -half,  half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half,  half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half, -half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos( half, -half, -half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos( half, -half, -half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half, -half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half, -half,  half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half,  half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.draw();
    }

    public static void renderAngleRotatedTexturedRectVB(BufferBuilder buf, Vector3 renderOffset, Vector3 axis, double angleRad, double scale, double u, double v, double uLength, double vLength, float r, float g, float b, float a) {
        Vector3 renderStart = axis.clone().perpendicular().rotate(angleRad, axis).normalize();

        Vector3 vec = renderStart.clone().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(buf).tex(u,           v + vLength).color(r, g, b, a).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(buf).tex(u + uLength, v + vLength).color(r, g, b, a).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(buf).tex(u + uLength, v          ).color(r, g, b, a).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(renderOffset);
        vec.drawPos(buf).tex(u,           v          ).color(r, g, b, a).endVertex();
    }

}
