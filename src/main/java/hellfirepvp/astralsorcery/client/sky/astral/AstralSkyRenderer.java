/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky.astral;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.BatchedVertexList;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.ActiveCelestialsHandler;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralSkyRenderer
 * Created by HellFirePvP
 * Date: 13.01.2020 / 20:11
 */
@OnlyIn(Dist.CLIENT)
public class AstralSkyRenderer implements IRenderHandler {

    private static final Random RAND = new Random();
    private static final ResourceLocation REF_TEX_MOON_PHASES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation REF_TEX_SUN =         new ResourceLocation("textures/environment/sun.png");

    public static AstralSkyRenderer INSTANCE = new AstralSkyRenderer();

    private BatchedVertexList sky = new BatchedVertexList(DefaultVertexFormats.POSITION);
    private BatchedVertexList skyHorizon = new BatchedVertexList(DefaultVertexFormats.POSITION);
    private List<StarDrawList> starLists = new LinkedList<>();

    private boolean initialized = false;

    private AstralSkyRenderer() {}

    public void reset() {
        sky.reset();
        skyHorizon.reset();
        starLists.forEach(BatchedVertexList::reset);
        starLists.clear();

        this.initialized = false;
    }

    private void initialize() {
        sky.batch(AstralSkyRendererSetup::generateSky);
        skyHorizon.batch(AstralSkyRendererSetup::generateSkyHorizon);
        for (int i = 0; i < 20; i++) {
            AbstractRenderableTexture starTexture = (i % 2 == 0 ? TexturesAS.TEX_STAR_1 : TexturesAS.TEX_STAR_2);
            int flicker = 12 + RAND.nextInt(5);

            StarDrawList starList = new StarDrawList(starTexture, flicker);
            starList.batch(buf -> AstralSkyRendererSetup.generateStars(buf, 60 + RAND.nextInt(60), 1.1F + RAND.nextFloat() * 0.3F));
            starLists.add(starList);
        }

        this.initialized = true;
    }

    @Override
    public void render(int ticks, float pTicks, ClientWorld world, Minecraft mc) {
        if (AssetLibrary.isReloading()) {
            return;
        }
        if (!initialized) {
            initialize();
        }

        //Massive assumptions here noone messed with the rendering stack.
        MatrixStack renderStack = new MatrixStack();
        ActiveRenderInfo ari = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        renderStack.rotate(Vector3f.ZP.rotationDegrees(0));
        renderStack.rotate(Vector3f.XP.rotationDegrees(ari.getPitch()));
        renderStack.rotate(Vector3f.YP.rotationDegrees(ari.getYaw() + 180.0F));

        Vec3d color = world.getSkyColor(mc.gameRenderer.getActiveRenderInfo().getBlockPos(), pTicks);
        float skyR = (float) color.x;
        float skyG = (float) color.y;
        float skyB = (float) color.z;
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);

        if (ctx != null && ctx.getCelestialHandler().isSolarEclipseActive()) {
            float perc = ctx.getCelestialHandler().getSolarEclipsePercent();
            perc = 0.05F + (perc * 0.95F);

            skyR *= perc;
            skyG *= perc;
            skyB *= perc;
        }

        //Sky
        RenderSystem.disableTexture();
        FogRenderer.applyFog();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color4f(skyR, skyG, skyB, 1F);
        this.sky.render(renderStack);
        RenderSystem.disableFog();

        //Sunrise/Sunset tint
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        float[] duskDawnColors = world.getDimension().calcSunriseSunsetColors(world.getCelestialAngle(pTicks), pTicks);
        if (duskDawnColors != null) {
            this.renderDuskDawn(duskDawnColors, renderStack, world, pTicks);
        }
        RenderSystem.shadeModel(GL11.GL_FLAT);

        //Prep celestials
        RenderSystem.enableTexture();
        Blending.ADDITIVE_ALPHA.apply();

        renderStack.push();
        renderStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
        renderStack.rotate(Vector3f.XP.rotationDegrees(world.getCelestialAngle(pTicks) * 360.0F));

        this.renderCelestials(world, renderStack, pTicks);
        this.renderStars(world, renderStack, pTicks);

        renderStack.pop();

        //Constellations
        renderStack.push();
        renderStack.rotate(Vector3f.XP.rotationDegrees(180));

        renderConstellationsSky(world, renderStack, pTicks);

        renderStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();

        //Draw horizon
        RenderSystem.disableTexture();

        RenderSystem.color4f(0F, 0F, 0F, 1F);
        double horizonDiff = Minecraft.getInstance().player.getEyePosition(pTicks).y - world.getHorizonHeight();
        if (horizonDiff < 0D) {
            renderStack.push();
            renderStack.translate(0, 12, 0);
            this.skyHorizon.render(renderStack);
            renderStack.pop();
        }
        RenderSystem.color4f(1F, 1F, 1F, 1F);

        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }

    /*
    private static void debugRenderSky() {
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        RenderSystem.rotatef(180F, 1F, 0F, 0F);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        GL11.glColor4f(1F, 0, 0, 1F);

        List<double[]> poss = ActiveCelestialsHandler.getDisplayPos();

        for (double[] position : poss) {
            double x = position[0];
            double y = position[1];
            double z = position[2];
            double size = position[3];

            double fx = x * 100.0D;
            double fy = y * 100.0D;
            double fz = z * 100.0D;
            double d8 = Math.atan2(x, z); // [-PI - PI]
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(x * x + z * z), y); // [-PI - PI]
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);
            double rotation = 0;
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            for (int j = 0; j < 4; ++j) {
                double d18 = (double) ((j & 2) - 1) * 0.5;
                double d19 = (double) ((j + 1 & 2) - 1) * 0.5;
                double d21 = d18 * rotation - d19 * size;
                double d22 = d19 * rotation + d18 * size;
                double d23 = d21 * d12;
                double d24 = -(d21 * d13);
                double d25 = d24 * d9 - d22 * d10;
                double d26 = d22 * d9 + d24 * d10;
                vb.pos(fx + d25, fy + d23, fz + d26).tex(((j + 1) & 2) >> 1, ((j + 2) & 2) >> 1).endVertex();
            }
            tes.draw();
        }
        TextureHelper.refreshTextureBind();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }
    */

    public static void renderConstellationsSky(ClientWorld world, MatrixStack renderStack, float pTicks) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);
        if (ctx == null) {
            return;
        }

        int dayLength = GeneralConfig.CONFIG.dayLength.get();
        long wTime = ((world.getDayTime() % dayLength) + dayLength) % dayLength;
        if (wTime < (dayLength / 2F)) {
            return; //Daytime.
        }
        float rainDim = 1.0F - world.getRainStrength(pTicks);
        float brightness = world.getStarBrightness(pTicks) * rainDim;
        if (brightness <= 0.0F) {
            return;
        }

        Random gen = ctx.getRandom();

        PlayerProgress clientProgress = ResearchHelper.getClientProgress();
        Map<IConstellation, ActiveCelestialsHandler.RenderPosition> constellations = ctx.getActiveCelestialsHandler().getCurrentRenderPositions();
        for (IConstellation cst : constellations.keySet()) {
            if (!clientProgress.hasConstellationDiscovered(cst) ||
                    !ctx.getConstellationHandler().isActiveCurrently(cst, MoonPhase.fromWorld(world))) {
                continue;
            }
            ActiveCelestialsHandler.RenderPosition pos = constellations.get(cst);

            RenderingConstellationUtils.renderConstellationSky(cst, renderStack, pos,
                    () -> RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), pTicks, 10 + gen.nextInt(5)) * brightness * 1.25F);
        }
    }

    private void renderStars(ClientWorld world, MatrixStack renderStack, float pTicks) {
        float starBrightness = world.getStarBrightness(pTicks) * (1.0F - world.getRainStrength(pTicks));
        if (starBrightness > 0) {
            this.starLists.forEach((list) -> {
                float br = RenderingConstellationUtils.stdFlicker(ClientScheduler.getClientTick(), pTicks, list.flickerSpeed) * starBrightness;
                RenderSystem.color4f(starBrightness, starBrightness, starBrightness, br);
                list.render(renderStack);
            });
            RenderSystem.color4f(1F, 1F, 1F, 1F);
        }
    }

    private void renderCelestials(ClientWorld world, MatrixStack renderStack, float pTicks) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);

        float rainAlpha = 1F - world.getRainStrength(pTicks);
        RenderSystem.color4f(1F, 1F, 1F, rainAlpha);

        if (ctx != null && ctx.getCelestialHandler().isSolarEclipseActive()) {
            this.renderSolarEclipseSun(renderStack, ctx.getCelestialHandler().getSolarEclipseTick());
        } else {
            this.renderSun(renderStack);
        }

        if (ctx != null && ctx.getCelestialHandler().isLunarEclipseActive()) {
            int lunarHalf = DayTimeHelper.getLunarEclipseHalfDuration();

            int eclTick = ctx.getCelestialHandler().getLunarEclipseTick();
            if (eclTick >= lunarHalf) { //fading out
                eclTick -= lunarHalf;
            } else {
                eclTick = lunarHalf - eclTick;
            }
            float perc = ((float) eclTick) / DayTimeHelper.getLunarEclipseHalfDuration();
            RenderSystem.color4f(1F, 0.4F + (0.6F * perc), 0.4F + (0.6F * perc), rainAlpha);
            this.renderMoon(renderStack, world);
        } else {
            this.renderMoon(renderStack, world);
        }
        RenderSystem.color4f(1F, 1F, 1F, 1F);
    }

    private void renderSolarEclipseSun(MatrixStack renderStack, int eclipseTick) {
        float sunSize = 30F;

        float part = ((float) DayTimeHelper.getSolarEclipseHalfDuration() * 2) / 7F;
        float u = 0;
        float tick = eclipseTick;
        while (tick - part > 0) {
            tick -= part;
            u += 1;
        }
        float uOffset = u;

        TexturesAS.TEX_SOLAR_ECLIPSE.bindTexture();
        renderStack.push();
        renderStack.rotate(Vector3f.YP.rotationDegrees(-90F));
        Matrix4f matr = renderStack.getLast().getMatrix();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, buf -> {
            buf.pos(matr, -sunSize, 100, -sunSize).tex( uOffset      / 7F, 0).endVertex();
            buf.pos(matr,  sunSize, 100, -sunSize).tex((uOffset + 1) / 7F, 0).endVertex();
            buf.pos(matr,  sunSize, 100,  sunSize).tex((uOffset + 1) / 7F, 1).endVertex();
            buf.pos(matr, -sunSize, 100,  sunSize).tex( uOffset      / 7F, 1).endVertex();
        });

        renderStack.pop();
    }

    private void renderSun(MatrixStack renderStack) {
        float sunSize = 30F;

        Matrix4f matr = renderStack.getLast().getMatrix();

        Minecraft.getInstance().getTextureManager().bindTexture(REF_TEX_SUN);
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, buf -> {
            buf.pos(matr, -sunSize, 100, -sunSize).tex(0, 0).endVertex();
            buf.pos(matr,  sunSize, 100, -sunSize).tex(1, 0).endVertex();
            buf.pos(matr,  sunSize, 100,  sunSize).tex(1, 1).endVertex();
            buf.pos(matr, -sunSize, 100,  sunSize).tex(0, 1).endVertex();
        });
    }

    private void renderMoon(MatrixStack renderStack, World world) {
        float moonSize = 20F;

        //Don't ask me.. i'm just copying this and be done with it
        int moonPhase = world.getMoonPhase();
        int i = moonPhase % 4;
        int j = moonPhase / 4 % 2;
        float minU = (i) / 4F;
        float minV = (j) / 2F;
        float maxU = (i + 1) / 4F;
        float maxV = (j + 1) / 2F;

        Matrix4f matr = renderStack.getLast().getMatrix();

        Minecraft.getInstance().getTextureManager().bindTexture(REF_TEX_MOON_PHASES);
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, buf -> {
            buf.pos(matr, -moonSize, -100,  moonSize).tex(maxU, maxV).endVertex();
            buf.pos(matr,  moonSize, -100,  moonSize).tex(minU, maxV).endVertex();
            buf.pos(matr,  moonSize, -100, -moonSize).tex(minU, minV).endVertex();
            buf.pos(matr, -moonSize, -100, -moonSize).tex(maxU, minV).endVertex();
        });
    }

    private void renderDuskDawn(float[] duskDawnColors, MatrixStack renderStack, ClientWorld world, float pTicks) {
        float f3 = MathHelper.sin(world.getCelestialAngleRadians(pTicks)) < 0.0F ? 180.0F : 0.0F;

        renderStack.push();
        renderStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
        renderStack.rotate(Vector3f.ZP.rotationDegrees(f3));
        renderStack.rotate(Vector3f.ZP.rotationDegrees(90.0F));

        float r = duskDawnColors[0];
        float g = duskDawnColors[1];
        float b = duskDawnColors[2];
        float a = duskDawnColors[3];

        RenderingUtils.draw(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR, buf -> {
            buf.pos(0, 100, 0).color(r, g, b, a).endVertex();
            for (int i = 0; i <= 16; i++) {
                float f6 = (float) i * ((float) Math.PI * 2F) / 16F;
                float f7 = MathHelper.sin(f6);
                float f8 = MathHelper.cos(f6);
                buf.pos(f7 * 120F, f8 * 120F, -f8 * 40F * a).color(r, g, b, 0F).endVertex();
            }
        });

        renderStack.pop();
    }

    private static class StarDrawList extends BatchedVertexList {

        private final AbstractRenderableTexture texture;
        private final int flickerSpeed;

        private StarDrawList(AbstractRenderableTexture texture, int flickerSpeed) {
            super(DefaultVertexFormats.POSITION_TEX);

            this.texture = texture;
            this.flickerSpeed = flickerSpeed;
        }

        @Override
        public void render(MatrixStack renderStack) {
            this.texture.bindTexture();
            super.render(renderStack);
        }
    }
}
