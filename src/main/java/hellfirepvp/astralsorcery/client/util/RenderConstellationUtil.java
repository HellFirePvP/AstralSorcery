/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.ShadersAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderConstellationUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderConstellationUtil {

    public static Map<FloatPoint, FloatRectangle> drawConstellationUI(ColorWrapper color, BaseConstellation cst, PoseStack pose,
                                                                      float x, float y, float width, float height,
                                                                      float lineWidth, Supplier<Float> alphaSupplier,
                                                                      boolean isKnown, boolean applyLevelStarBrightness) {

        float uLength = width / BaseConstellation.STAR_GRID_WIDTH_HEIGHT;
        float vLength = height / BaseConstellation.STAR_GRID_WIDTH_HEIGHT;
        Matrix4f offset = pose.last().pose();

        float brightness;
        if (applyLevelStarBrightness && Minecraft.getInstance().level != null) {
            if (DayTimeHelper.isDay(Minecraft.getInstance().level)) {
                return Collections.emptyMap();
            }
            float daytime = DayTimeHelper.getCurrentDaytimeDistribution(Minecraft.getInstance().level);
            brightness = Mth.sqrt(daytime);
        } else {
            brightness = 1F;
        }

        if (isKnown) {
            TexturesAS.STAR_LINE.bindTexture();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
                cst.getStarConnections().forEach(conn -> {
                    int alpha = Mth.clamp((int) (alphaSupplier.get() * brightness * 255F), 0, 255);
                    int c = color.copyWithAlpha(alpha).getColor();

                    Vector3 fromStar = new Vector3(x + conn.getLeft().x() * uLength,   y + conn.getLeft().y() * vLength,    0);
                    Vector3 toStar   = new Vector3(x + conn.getRight().x()  * uLength, y + conn.getRight().y() * vLength,   0);

                    Vector3 dir = toStar.copy().subtract(fromStar);
                    Vector3 degLot = dir.copy().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(lineWidth);

                    Vector3 vec00 = fromStar.copy().add(degLot);
                    Vector3 vecV = degLot.copy().multiply(-2);

                    for (int i = 0; i < 4; i++) {
                        int u = ((i + 1) & 2) >> 1;
                        int v = ((i + 2) & 2) >> 1;

                        Vector3 pos = vec00.copy().add(dir.copy().multiply(u)).add(vecV.copy().multiply(v));
                        pos.drawPos(offset, buf)
                                .setColor(c)
                                .setUv(u, v);
                    }
                });
            });
        }

        Map<FloatPoint, FloatRectangle> stars = new HashMap<>();
        TexturesAS.STAR_1.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
            cst.getStars().forEach(star -> {
                int alpha = Mth.clamp((int) (alphaSupplier.get() * brightness * 255F), 0, 255);
                alpha = Math.round(alpha * 0.9F + 0.1F);
                int c = color.copyWithAlpha(alpha).getColor();
                if (!isKnown) {
                    c = ColorWrapper.of(alpha, alpha, alpha, alpha).getColor();
                }

                int starX = star.x();
                int starY = star.y();
                Vector3 starVec = new Vector3(starX * uLength - uLength, starY * vLength - vLength, 0).add(x, y, 0);

                for (int i = 0; i < 4; i++) {
                    int u = ((i + 1) & 2) >> 1;
                    int v = ((i + 2) & 2) >> 1;

                    Vector3 pos = starVec.copy().addX(uLength * u * 2).addY(vLength * v * 2);
                    pos.drawPos(offset, buf)
                            .setColor(c)
                            .setUv(u, v);
                }

                stars.put(star.asPoint().toFloat(), new FloatRectangle((float) starVec.getX(), (float) starVec.getY(), uLength * 2, vLength * 2));
            });
        });

        return stars;
    }

    public static void drawConstellationInWorld(BaseConstellation cst, PoseStack poseStack, MultiBufferSource buffers, Vector3 pos, float scale, float lineWidth, float alpha) {
        drawConstellationInWorld(cst.getConstellationColor(), cst, poseStack, buffers, pos, scale, lineWidth, alpha);
    }

    public static void drawConstellationInWorld(ColorWrapper color, BaseConstellation cst, PoseStack poseStack, MultiBufferSource buffers, Vector3 pos, float scale, float lineWidth, float alpha) {
        Matrix4f pose = poseStack.last().pose();
        int connectionColor = color.copyWithAlpha(Math.round(color.getAlpha() * alpha * 0.8F)).getColor();
        int starColor = color.copyWithAlpha(Math.round(color.getAlpha() * alpha)).getColor();
        float starSize = 1F / BaseConstellation.STAR_GRID_WIDTH_HEIGHT * scale;
        Vector3 drawOffset = new Vector3(BaseConstellation.STAR_GRID_WIDTH_HEIGHT, 0, BaseConstellation.STAR_GRID_WIDTH_HEIGHT).multiply(-0.5F * starSize);

        Vector3 dirU, dirV;
        VertexConsumer vb;

        vb = buffers.getBuffer(RenderTypesAS.CONSTELLATION_WORLD_CONNECTION);
        for (StarConnection conn : cst.getStarConnections()) {
            pos = pos.copy().addY(0.001F);

            dirU = conn.getRight().asLevelVector().subtract(conn.getLeft().asLevelVector()).multiply(starSize);
            dirV = dirU.copy().crossProduct(Vector3.RotAxis.Y_AXIS.getVector()).setY(0).normalize().multiply(lineWidth * starSize);

            Vector3 offset = pos.copy()
                    .add(conn.getLeft().asLevelVector().multiply(starSize))
                    .subtract(dirV.copy().divide(2))
                    .add(drawOffset);

            Vector3 at = offset.copy().add(dirU.copy().multiply(0)).add(dirV.copy().multiply(1));
            at.drawPos(pose, vb).setUv(1, 0).setColor(connectionColor);

            at = offset.copy().add(dirU.copy().multiply(1)).add(dirV.copy().multiply(1));
            at.drawPos(pose, vb).setUv(0, 0).setColor(connectionColor);

            at = offset.copy().add(dirU.copy().multiply(1)).add(dirV.copy().multiply(0));
            at.drawPos(pose, vb).setUv(0, 1).setColor(connectionColor);

            //at = offset.copy().add(dirU.copy().multiply(0)).add(dirV.copy().multiply(0));
            offset.drawPos(pose, vb).setUv(1, 1).setColor(connectionColor);
        }

        dirU = new Vector3(starSize * 2, 0, 0);
        dirV = new Vector3(0, 0, starSize * 2);
        pos = pos.copy().addY(0.002F);

        vb = buffers.getBuffer(RenderTypesAS.CONSTELLATION_WORLD_STAR);
        for (StarLocation star : cst.getStars()) {
            Vector3 offset = pos.copy()
                    .add(star.asLevelVector().multiply(starSize))
                    .add(-starSize, 0, -starSize)
                    .add(drawOffset);

            Vector3 at = offset.copy().add(dirU.copy().multiply(0)).add(dirV.copy().multiply(1));
            at.drawPos(pose, vb).setUv(1, 0).setColor(starColor);

            at = offset.copy().add(dirU.copy().multiply(1)).add(dirV.copy().multiply(1));
            at.drawPos(pose, vb).setUv(0, 0).setColor(starColor);

            at = offset.copy().add(dirU.copy().multiply(1)).add(dirV.copy().multiply(0));
            at.drawPos(pose, vb).setUv(0, 1).setColor(starColor);

            //at = offset.copy().add(dirU.copy().multiply(0)).add(dirV.copy().multiply(0));
            offset.drawPos(pose, vb).setUv(1, 1).setColor(starColor);
        }
    }
}
