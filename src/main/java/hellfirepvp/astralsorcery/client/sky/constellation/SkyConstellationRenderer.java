/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky.constellation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.lib.ShadersAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.DebugConstellation;
import hellfirepvp.astralsorcery.common.constellation.level.ConstellationHandler;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyContext;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SkyConstellationRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SkyConstellationRenderer {

    private static final float yawPerXCoord = -0.35F;
    private static final float pitchPerYCoord = 0.35F;

    private static final List<ConstellationMapping> constellationMappings = new ArrayList<>();

    public static void renderSkyConstellations(PoseStack pose, ClientLevel level, float partialTick, Runnable skyFogSetup) {
        constellationMappings.clear();

        float starBrightness = level.getStarBrightness(partialTick) * (1.0F - level.getRainLevel(partialTick));
        if (starBrightness <= 0) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        buildConstellationMappings(level);
        if (constellationMappings.isEmpty()) return;

        FogRenderer.setupNoFog();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();

        if (!constellationMappings.isEmpty()) {
            List<ConstellationMapping> drawnCst = constellationMappings.stream()
                    .filter(mapping -> mapping.getDrawInfo().discovered())
                    .toList();

            if (!drawnCst.isEmpty()) {
                TexturesAS.STAR_LINE.bindTexture();
                RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
                    drawnCst.forEach(mapping -> {
                        drawConstellationConnections(pose, mapping, buf, partialTick, starBrightness);
                    });
                });
            }
        }

        TexturesAS.STAR_1.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
            constellationMappings.forEach(mapping -> {
                drawConstellationStars(pose, mapping, mapping.getDrawInfo().discovered(), mapping.getDrawInfo().seen(), buf, partialTick, starBrightness);
            });
        });


        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        skyFogSetup.run();
    }

    private static void buildConstellationMappings(Level level) {
        List<SkyConstellationPosition> positions = SkyConstellationPositionLoader.getInstance().getPositions();
        List<BaseConstellation> active = LevelSkyHandler.getContext(level)
                .map(LevelSkyContext::getConstellationHandler)
                .map(ConstellationHandler::getActiveConstellations)
                .map(ArrayList::new)
                .orElse(new ArrayList<>());
        if (SkyConstellationPositionLoader.getInstance().renderDebug()) {
            DebugConstellation debugCst = DebugConstellation.create();
            active = new ArrayList<>();
            for (int i = 0; i < positions.size(); i++) {
                active.add(debugCst);
            }
        }
        int mappedSize = Math.min(active.size(), positions.size());
        List<Pair<BaseConstellation, FloatPoint>> constellationPositions = new ArrayList<>(mappedSize);
        for (int i = 0; i < mappedSize; i++) {
            constellationPositions.add(new Pair<>(active.get(i), positions.get(i).asPoint()));
        }
        constellationPositions.forEach(cstPair -> {
            BaseConstellation cst = cstPair.getFirst();
            FloatPoint center = cstPair.getSecond();

            ConstellationDrawInformation info = ConstellationDrawInformation.of(cst);
            if (SkyConstellationPositionLoader.getInstance().renderDebug()) {
                info = new ConstellationDrawInformation(cst, true, true);
            }
            ConstellationMapping mapping = new ConstellationMapping(info, center);
            cst.getStars().forEach(starPos -> {
                IntPoint offsetStar = starPos.asPoint().subtract(16, 16);

                float pitch = center.y() + offsetStar.y() * pitchPerYCoord;
                float yaw = center.x() + adjustYaw(offsetStar.x() * yawPerXCoord, pitch);
                FloatPoint starPoint = new FloatPoint(yaw, pitch);

                mapping.addStar(starPos, starPoint);
            });

            constellationMappings.add(mapping);
        });
    }

    private static void drawConstellationStars(PoseStack pose, ConstellationMapping mapping, boolean discovered, boolean seen, BufferBuilder buf, float partialTick, float starBrightness) {
        mapping.getRenderedStars().values().forEach(starPoint -> {
            RandomSource rand = RandomSource.create(starPoint.hashCode() * 31L);
            float flickerSpeed = 0.02F + rand.nextFloat() * 0.04F;
            float brightness = EffectUtil.flicker(flickerSpeed, partialTick) * starBrightness;
            brightness = brightness * 0.5F + 0.3F * starBrightness;

            float size = discovered ? 1F : seen ? 0.8F : 0.5F;
            ColorWrapper color = ColorWrapper.WHITE;
            if (discovered) {
                color = mapping.getConstellation().getConstellationColor();
            } else if (seen) {
                color = ColorUtil.blendColors(color, mapping.getConstellation().getConstellationColor(), 0.5F);
            }
            color = color.copyWithAlpha(Mth.floor(brightness * 255F));

            pose.pushPose();
            pose.mulPose(Axis.YP.rotationDegrees(starPoint.x()));
            pose.mulPose(Axis.ZP.rotationDegrees(starPoint.y()));

            Matrix4f mat = pose.last().pose();
            buf.addVertex(mat, -size, 100,  size).setUv(1, 1).setColor(color.getColor());
            buf.addVertex(mat,  size, 100,  size).setUv(0, 1).setColor(color.getColor());
            buf.addVertex(mat,  size, 100, -size).setUv(0, 0).setColor(color.getColor());
            buf.addVertex(mat, -size, 100, -size).setUv(1, 0).setColor(color.getColor());

            pose.popPose();
        });
    }

    private static float adjustYaw(float yaw, float atPitch) {
        float scaleFactor = (float) Math.pow(Math.max(1F - atPitch / 80, 0), 3);
        return yaw + (yaw * (scaleFactor * 4.5F));
    }

    private static void drawConstellationConnections(PoseStack pose, ConstellationMapping mappedConstellation, BufferBuilder buf, float partialTick, float starBrightness) {
        float size = 0.5F;

        mappedConstellation.getConstellation().getStarConnections().forEach(connection -> {
            FloatPoint start = mappedConstellation.getRenderedStars().get(connection.getLeft());
            FloatPoint end = mappedConstellation.getRenderedStars().get(connection.getRight());
            if (start == null || end == null) return; // Shouldn't happen

            RandomSource rand = RandomSource.create(connection.getLeft().hashCode() * 31L ^ connection.getRight().hashCode() * 31L);
            float flickerSpeed = 0.02F + rand.nextFloat() * 0.04F;
            float brightness = EffectUtil.flicker(flickerSpeed, partialTick) * starBrightness;
            brightness = brightness * 0.5F + 0.4F * starBrightness;
            ColorWrapper color = mappedConstellation.getConstellation().getConstellationColor();
            color = color.copyWithAlpha(Mth.floor(brightness * 255F));

            // Yes i know this doesn't work. it's not pretty. but it's close enough and that's enough atm.
            Vector2f offset = new Vector2f(end.x() - start.x(), end.y() - start.y())
                    .perpendicular()
                    .normalize(size / 2F);

            Matrix4f poseMatrix = pose.last().pose();

            Matrix4f rot = new Matrix4f()
                    .rotate(Axis.YP.rotationDegrees(start.x() - offset.x()))
                    .rotate(Axis.ZP.rotationDegrees(start.y() - offset.y()));
            buf.addVertex(new Matrix4f(poseMatrix).mul(rot), 0, 100, 0).setUv(0, 0).setColor(color.getColor());

            rot = new Matrix4f()
                    .rotate(Axis.YP.rotationDegrees(end.x() - offset.x()))
                    .rotate(Axis.ZP.rotationDegrees(end.y() - offset.y()));
            buf.addVertex(new Matrix4f(poseMatrix).mul(rot), 0, 100, 0).setUv(1, 0).setColor(color.getColor());

            rot = new Matrix4f()
                    .rotate(Axis.YP.rotationDegrees(end.x() + offset.x()))
                    .rotate(Axis.ZP.rotationDegrees(end.y() + offset.y()));
            buf.addVertex(new Matrix4f(poseMatrix).mul(rot), 0, 100, 0).setUv(1, 1).setColor(color.getColor());

            rot = new Matrix4f()
                    .rotate(Axis.YP.rotationDegrees(start.x() + offset.x()))
                    .rotate(Axis.ZP.rotationDegrees(start.y() + offset.y()));
            buf.addVertex(new Matrix4f(poseMatrix).mul(rot), 0, 100, 0).setUv(0, 1).setColor(color.getColor());
        });
    }

    public static List<ConstellationMapping> getConstellationMappings() {
        return Collections.unmodifiableList(constellationMappings);
    }

    public static class ConstellationMapping {

        private final ConstellationDrawInformation drawInfo;
        private final FloatPoint center;
        private final Map<StarLocation, FloatPoint> renderedStars = new HashMap<>();
        private final Map<StarLocation, FloatPoint> playerViewRenderedStars = new HashMap<>();

        public ConstellationMapping(ConstellationDrawInformation drawInfo, FloatPoint center) {
            this.drawInfo = drawInfo;
            this.center = center;
        }

        public ConstellationDrawInformation getDrawInfo() {
            return this.drawInfo;
        }

        public BaseConstellation getConstellation() {
            return this.getDrawInfo().constellation();
        }

        public FloatPoint getCenter() {
            return this.center;
        }

        private void addStar(StarLocation star, FloatPoint renderedPos) {
            this.renderedStars.put(star, renderedPos);

            float playerYaw = 90 - renderedPos.x();
            this.playerViewRenderedStars.put(star, new FloatPoint(playerYaw, -90 + renderedPos.y()));
        }

        public Map<StarLocation, FloatPoint> getRenderedStars() {
            return Collections.unmodifiableMap(this.renderedStars);
        }

        public Map<StarLocation, FloatPoint> getPlayerViewRenderedStars() {
            return Collections.unmodifiableMap(this.playerViewRenderedStars);
        }
    }

    public record ConstellationDrawInformation(BaseConstellation constellation, boolean seen, boolean discovered) {

        public static ConstellationDrawInformation of(BaseConstellation constellation) {
            PlayerProgress clientProgress = ResearchManager.getClientProgress();
            boolean seen = clientProgress.hasSeenConstellation(constellation);
            boolean discovered = clientProgress.hasDiscoveredConstellation(constellation);
            return new ConstellationDrawInformation(constellation, seen, discovered);
        }

    }
}
