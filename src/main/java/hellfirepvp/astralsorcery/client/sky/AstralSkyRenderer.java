/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.lib.ShadersAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.sky.constellation.SkyConstellationRenderer;
import hellfirepvp.astralsorcery.client.util.BatchedVertexBuffer;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyContext;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralSkyRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralSkyRenderer {

    private static final AstralSkyRenderer instance = new AstralSkyRenderer();
    private static final RandomSource rand = RandomSource.create();
    private static final ResourceLocation REF_TEX_MOON_PHASES = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    private static final ResourceLocation REF_TEX_SUN =         ResourceLocation.withDefaultNamespace("textures/environment/sun.png");

    private final BatchedVertexBuffer skyBuffer = new BatchedVertexBuffer();
    private final BatchedVertexBuffer horizonBuffer = new BatchedVertexBuffer();
    private final List<StarVertexBuffer> starBuffers = new ArrayList<>();

    private boolean initialized = false;

    private AstralSkyRenderer() {}

    public static AstralSkyRenderer getInstance() {
        return instance;
    }

    public void reset() {
        this.skyBuffer.close();
        this.horizonBuffer.close();
        this.starBuffers.forEach(BatchedVertexBuffer::close);
        this.starBuffers.clear();

        this.initialized = false;
    }

    private void initialize() {
        this.skyBuffer.initialize(AstralSkyRendererSetup::generateSky);
        this.horizonBuffer.initialize(AstralSkyRendererSetup::generateSkyHorizon);
        for (int i = 0; i < 40; i++) {
            AbstractRenderTexture starTexture = (i % 2 == 0 ? TexturesAS.STAR_1 : TexturesAS.STAR_2);
            float flickerSpeed = 0.02F + rand.nextFloat() * 0.04F;

            StarVertexBuffer buffer = new StarVertexBuffer(starTexture, flickerSpeed);
            buffer.initialize(() -> AstralSkyRendererSetup.generateStars(100 + rand.nextInt(60), 0.8F + rand.nextFloat() * 0.3F));
            this.starBuffers.add(buffer);
        }

        this.initialized = true;
    }

    public boolean shouldRenderSpecialSky(ClientLevel level) {
        //TODO config?
        return level.dimension() == Level.OVERWORLD && level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL;
    }

    public void renderSky(ClientLevel level, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup) {
        // Vanilla behavior
        skyFogSetup.run();
        if (isFoggy) return;
        FogType fogtype = camera.getFluidInCamera();
        if (fogtype == FogType.POWDER_SNOW || fogtype == FogType.LAVA) return;
        if (this.doesMobEffectBlockSky(camera)) return;
        // Vanilla behavior end

        LevelSkyContext ctx = LevelSkyHandler.getContext(level).orElse(null);
        Tesselator tesselator = Tesselator.getInstance();
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (AssetLibrary.isReloading() || player == null) {
            return;
        }
        if (!this.initialized) {
            this.initialize();
        }

        PoseStack pose = new PoseStack();
        pose.mulPose(frustumMatrix);

        // Calc skycolor - added: solar eclipse dimming
        Vec3 skyColor = level.getSkyColor(mc.gameRenderer.getMainCamera().getPosition(), partialTick);
        float skyR = (float) skyColor.x;
        float skyG = (float) skyColor.y;
        float skyB = (float) skyColor.z;
        if (ctx != null && ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
            float percent = ctx.getCelestialEventHandler().getSolarEclipsePercent();
            percent = 0.05F + (percent * 0.95F);

            skyR *= percent;
            skyG *= percent;
            skyB *= percent;
        }

        // Draw Sky
        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(skyR, skyG, skyB, 1);
        this.skyBuffer.drawWithShader(pose.last().pose(), projectionMatrix);

        // Sunrise/Sunset tint
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        float[] duskDawnColors = level.effects().getSunriseColor(level.getTimeOfDay(partialTick), partialTick);
        if (duskDawnColors != null) {
            this.renderDuskDawn(duskDawnColors, pose, level, partialTick);
        }

        // Prep Celestials (Sun/Moon)
        Blending.ADDITIVE_ALPHA_FLAT.apply();
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-90));

        pose.pushPose();
        pose.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360));
        this.renderCelestials(ctx, pose, level, partialTick);
        pose.popPose();

        this.renderStars(pose, projectionMatrix, level, partialTick, skyFogSetup);

        RenderSystem.disableBlend();
        Blending.DEFAULT.apply();
        pose.popPose();

        SkyConstellationRenderer.renderSkyConstellations(pose, level, partialTick, skyFogSetup);

        //Draw horizon
        RenderSystem.setShaderColor(0F, 0F, 0F, 1F);
        double horizonDiff = player.getEyePosition(partialTick).y - level.getLevelData().getHorizonHeight(level);
        if (horizonDiff < 0) {
            pose.pushPose();
            pose.translate(0, 12, 0);
            this.horizonBuffer.drawWithShader(pose.last().pose(), projectionMatrix);
            pose.popPose();
        }

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.depthMask(true);
    }

    private void renderStars(PoseStack pose, Matrix4f projMatrix, ClientLevel level, float partialTick, Runnable skyFogSetup) {
        float starBrightness = level.getStarBrightness(partialTick) * (1.0F - level.getRainLevel(partialTick));
        if (starBrightness > 0) {
            Matrix4f poseMatrix = pose.last().pose();
            FogRenderer.setupNoFog();

            this.starBuffers.forEach(buffer -> {
                float br = EffectUtil.flicker(buffer.flickerSpeed, partialTick) * starBrightness;
                RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, br);
                buffer.drawWithShader(poseMatrix, projMatrix, ShadersAS.getPositionTexAlphaShader());
            });
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            skyFogSetup.run();
        }
    }

    private void renderCelestials(LevelSkyContext ctx, PoseStack pose, ClientLevel level, float partialTick) {
        float rainAlpha = 1F - level.getRainLevel(partialTick);
        RenderSystem.setShaderColor(1F, 1F, 1F, rainAlpha);

        if (ctx != null && ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
            this.renderSolarEclipseSun(pose, ctx, partialTick);
        } else {
            this.renderSun(pose);
        }

        if (ctx != null && ctx.getCelestialEventHandler().getLunarEclipse().isActiveNow()) {
            int lunarHalf = ctx.getCelestialEventHandler().getLunarEclipse().getEventDuration() / 2;

            float eclTick = ctx.getCelestialEventHandler().getLunarEclipse().getEffectTick(0F);
            if (eclTick >= lunarHalf) { //fading out
                eclTick -= lunarHalf;
            } else {
                eclTick = lunarHalf - eclTick;
            }
            float perc = eclTick / lunarHalf;
            RenderSystem.setShaderColor(1F, 0.4F + (0.6F * perc), 0.4F + (0.6F * perc), rainAlpha);
            this.renderMoon(pose, level);
        } else {
            this.renderMoon(pose, level);
        }
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    private void renderMoon(PoseStack renderStack, Level world) {
        float moonSize = 20F;

        int moonPhase = world.getMoonPhase();
        int i = moonPhase % 4;
        int j = moonPhase / 4 % 2;
        float minU = (i) / 4F;
        float minV = (j) / 2F;
        float maxU = (i + 1) / 4F;
        float maxV = (j + 1) / 2F;

        Matrix4f matr = renderStack.last().pose();

        RenderSystem.setShaderTexture(0, REF_TEX_MOON_PHASES);
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, GameRenderer::getPositionTexShader, buf -> {
            buf.addVertex(matr, -moonSize, -100,  moonSize).setUv(maxU, maxV);
            buf.addVertex(matr,  moonSize, -100,  moonSize).setUv(minU, maxV);
            buf.addVertex(matr,  moonSize, -100, -moonSize).setUv(minU, minV);
            buf.addVertex(matr, -moonSize, -100, -moonSize).setUv(maxU, minV);
        });
    }

    private void renderSolarEclipseSun(PoseStack renderStack, LevelSkyContext ctx, float pTicks) {
        float sunSize = 30F;

        int eclipseTick = Mth.floor(ctx.getCelestialEventHandler().getSolarEclipse().getEffectTick(pTicks));
        int partTickLength = Mth.floor(ctx.getCelestialEventHandler().getSolarEclipse().getEventDuration() / 7F);
        int uOffset = eclipseTick / partTickLength;

        TexturesAS.SOLAR_ECLIPSE.bindTexture();
        renderStack.pushPose();
        renderStack.mulPose(Axis.YP.rotationDegrees(-90F));
        Matrix4f matr = renderStack.last().pose();

        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, GameRenderer::getPositionTexShader, buf -> {
            buf.addVertex(matr, -sunSize, 100, -sunSize).setUv( uOffset      / 7F, 0);
            buf.addVertex(matr,  sunSize, 100, -sunSize).setUv((uOffset + 1) / 7F, 0);
            buf.addVertex(matr,  sunSize, 100,  sunSize).setUv((uOffset + 1) / 7F, 1);
            buf.addVertex(matr, -sunSize, 100,  sunSize).setUv( uOffset      / 7F, 1);
        });

        renderStack.popPose();
    }

    private void renderSun(PoseStack renderStack) {
        float sunSize = 30F;
        Matrix4f matr = renderStack.last().pose();

        RenderSystem.setShaderTexture(0, REF_TEX_SUN);
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, GameRenderer::getPositionTexShader, buf -> {
            buf.addVertex(matr, -sunSize, 100, -sunSize).setUv(0, 0);
            buf.addVertex(matr,  sunSize, 100, -sunSize).setUv(1, 0);
            buf.addVertex(matr,  sunSize, 100,  sunSize).setUv(1, 1);
            buf.addVertex(matr, -sunSize, 100,  sunSize).setUv(0, 1);
        });
    }

    private void renderDuskDawn(float[] duskDawnColors, PoseStack pose, ClientLevel level, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        float horizonAngle = Mth.sin(level.getSunAngle(partialTick)) < 0.0F ? 180.0F : 0.0F;

        pose.pushPose();
        pose.mulPose(Axis.XP.rotationDegrees(90));
        pose.mulPose(Axis.ZP.rotationDegrees(horizonAngle));
        pose.mulPose(Axis.ZP.rotationDegrees(90));
        float r = duskDawnColors[0];
        float g = duskDawnColors[1];
        float b = duskDawnColors[2];
        float a = duskDawnColors[3];

        RenderUtil.draw(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR, GameRenderer::getPositionColorShader, buf -> {
            buf.addVertex(0, 100, 0).setColor(r, g, b, a);

            for (int i = 0; i <= 16; i++) {
                float f6 = (float) i * ((float) Math.PI * 2F) / 16F;
                float f7 = Mth.sin(f6);
                float f8 = Mth.cos(f6);
                buf.addVertex(f7 * 120F, f8 * 120F, -f8 * 40F * a).setColor(r, g, b, 0F);
            }
        });

        pose.popPose();
    }

    private boolean doesMobEffectBlockSky(Camera camera) {
        return camera.getEntity() instanceof LivingEntity livingentity &&
                (livingentity.hasEffect(MobEffects.BLINDNESS) || livingentity.hasEffect(MobEffects.DARKNESS));
    }

    private static class StarVertexBuffer extends BatchedVertexBuffer {

        private final AbstractRenderTexture texture;
        private final float flickerSpeed;

        public StarVertexBuffer(AbstractRenderTexture texture, float flickerSpeed) {
            this.texture = texture;
            this.flickerSpeed = flickerSpeed;
        }

        @Override
        public void draw() {
            this.texture.bindTexture();
            super.draw();
        }

        @Override
        public void drawWithShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader) {
            this.texture.bindTexture();
            super.drawWithShader(modelViewMatrix, projectionMatrix, shader);
        }
    }
}
