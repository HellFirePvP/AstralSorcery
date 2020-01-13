/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky.astral;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.BatchedVertexList;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
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

    private BatchedVertexList sky = new BatchedVertexList(DefaultVertexFormats.POSITION, 12);
    private BatchedVertexList skyHorizon = new BatchedVertexList(DefaultVertexFormats.POSITION, 12);
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
        if (!initialized && !AssetLibrary.isReloading()) {
            initialize();
        }

        Vec3d color = world.getSkyColor(mc.gameRenderer.getActiveRenderInfo().getBlockPos(), pTicks);

        //Sky
        GlStateManager.disableTexture();
        GlStateManager.color3f((float) color.x, (float) color.y, (float) color.z);
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color3f((float) color.x, (float) color.y, (float) color.z);
        this.sky.render();
        GlStateManager.disableFog();

        //Sunrise/Sunset tint
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();

        float[] duskDawnColors = world.getDimension().calcSunriseSunsetColors(world.getCelestialAngle(pTicks), pTicks);
        if (duskDawnColors != null) {
            this.renderDuskDawn(duskDawnColors, world, pTicks);
        }

        //Prep celestials
        GlStateManager.enableTexture();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(-90F, 0F, 1F, 0F);
        GlStateManager.rotatef(world.getCelestialAngle(pTicks) * 360F, 1F, 0F, 0F);

        this.renderCelestials(world, pTicks);
        this.renderStars(world, pTicks);
        this.renderConstellations(world, pTicks);

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableFog();
        GlStateManager.disableTexture();

        //Draw horizon
        GlStateManager.color3f(0F, 0F, 0F);
        float horizonDiff = (float) (Minecraft.getInstance().player.getEyePosition(pTicks).y - world.getHorizon());
        if (horizonDiff < 0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0F, 12F, 0F);
            this.skyHorizon.render();
            GlStateManager.popMatrix();
        }

        if (world.getDimension().isSkyColored()) {
            GlStateManager.color3f(
                    (float) color.x * 0.2F + 0.04F,
                    (float) color.y * 0.2F + 0.04F,
                    (float) color.z * 0.6F + 0.1F);
        } else {
            GlStateManager.color3f((float) color.x, (float) color.y, (float) color.z);
        }

        //Draw viewshield horizon
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0F, -(horizonDiff - 16.0F), 0F);
        this.skyHorizon.render();
        GlStateManager.popMatrix();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
    }

    private void renderConstellations(World world, float pTicks) {
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
        final float brightness = world.getStarBrightness(pTicks) * rainDim;
        if (brightness <= 0.0F) {
            return;
        }

        PlayerProgress clientProgress = ResearchHelper.getClientProgress();
        Map<IConstellation, ActiveCelestialsHandler.RenderPosition> constellations = ctx.getActiveCelestialsHandler().getCurrentRenderPositions();
        for (IConstellation cst : constellations.keySet()) {
            if (!clientProgress.hasConstellationDiscovered(cst) ||
                    !ctx.getConstellationHandler().isActiveCurrently(cst, MoonPhase.fromWorld(world))) {
                continue;
            }
            ActiveCelestialsHandler.RenderPosition pos = constellations.get(cst);

            //TODO heh...
        }
    }

    private void renderStars(World world, float pTicks) {
        float starBrightness = world.getStarBrightness(pTicks);
        if (starBrightness > 0) {
            this.starLists.forEach((list) -> {
                float br = RenderingConstellationUtils.stdFlicker(ClientScheduler.getClientTick(), pTicks, list.flickerSpeed) * (starBrightness * 1.5F);
                GlStateManager.color4f(starBrightness, starBrightness, starBrightness, br);
                list.render();
            });
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            TextureHelper.refreshTextureBind();
        }
    }

    private void renderCelestials(World world, float pTicks) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);

        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        float rainAlpha = 1F - world.getRainStrength(pTicks);
        GlStateManager.color4f(1F, 1F, 1F, rainAlpha);

        if (ctx != null && ctx.getCelestialHandler().isSolarEclipseActive()) {
            this.renderSolarEclipseSun(ctx.getCelestialHandler().getSolarEclipseTick());
        } else {
            this.renderSun();
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
            GlStateManager.color4f(1F, 0.4F + (0.6F * perc), 0.4F + (0.6F * perc), rainAlpha);
            this.renderMoon(world);
        } else {
            this.renderMoon(world);
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderSolarEclipseSun(int eclipseTick) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        float sunSize = 30F;

        float part = ((float) DayTimeHelper.getSolarEclipseHalfDuration() * 2) / 7F;
        float u = 0;
        float tick = eclipseTick;
        while (tick - part > 0) {
            tick -= part;
            u += 1;
        }

        TexturesAS.TEX_SOLAR_ECLIPSE.bindTexture();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(-90F, 0F, 1F, 0F);

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-sunSize, 100.0, -sunSize).tex( u      / 7F, 0.0D).endVertex();
        buf.pos( sunSize, 100.0, -sunSize).tex((u + 1) / 7F, 0.0D).endVertex();
        buf.pos( sunSize, 100.0,  sunSize).tex((u + 1) / 7F, 1.0D).endVertex();
        buf.pos(-sunSize, 100.0,  sunSize).tex( u      / 7F, 1.0D).endVertex();
        tes.draw();

        GlStateManager.popMatrix();
        TextureHelper.refreshTextureBind();
    }

    private void renderSun() {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        float sunSize = 30F;

        Minecraft.getInstance().getTextureManager().bindTexture(REF_TEX_SUN);
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-sunSize, 100.0, -sunSize).tex(0, 0).endVertex();
        buf.pos( sunSize, 100.0, -sunSize).tex(1, 0).endVertex();
        buf.pos( sunSize, 100.0,  sunSize).tex(1, 1).endVertex();
        buf.pos(-sunSize, 100.0,  sunSize).tex(0, 1).endVertex();
        tes.draw();
    }

    private void renderMoon(World world) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        float moonSize = 20F;

        //Don't ask me.. i'm just copying this and be done with it
        int moonPhase = world.getMoonPhase();
        int l = moonPhase % 4;
        int i1 = moonPhase / 4 % 2;
        float f13 = (float)(l) / 4.0F;
        float f14 = (float)(i1) / 2.0F;
        float f15 = (float)(l + 1) / 4.0F;
        float f9 =  (float)(i1 + 1) / 2.0F;

        Minecraft.getInstance().getTextureManager().bindTexture(REF_TEX_MOON_PHASES);
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-moonSize, -100.0,  moonSize).tex(f15, f9).endVertex();
        buf.pos( moonSize, -100.0,  moonSize).tex(f13, f9).endVertex();
        buf.pos( moonSize, -100.0, -moonSize).tex(f13, f14).endVertex();
        buf.pos(-moonSize, -100.0, -moonSize).tex(f15, f14).endVertex();
        tes.draw();
    }

    private void renderDuskDawn(float[] duskDawnColors, World world, float pTicks) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.pushMatrix();
        buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

        GlStateManager.rotatef(90F, 1F, 0F, 0F);
        GlStateManager.rotatef(MathHelper.sin(world.getCelestialAngleRadians(pTicks)) < 0F ? 180F : 0F, 0F, 0F, 1F);
        GlStateManager.rotatef(90F, 0F, 0F, 1F);

        float r = duskDawnColors[0];
        float g = duskDawnColors[1];
        float b = duskDawnColors[2];
        float a = duskDawnColors[3];

        buf.pos(0, 100, 0).color(r, g, b, a).endVertex();
        for (int i = 0; i <= 16; i++) {
            float f6 = (float) i * ((float) Math.PI * 2F) / 16F;
            float f7 = MathHelper.sin(f6);
            float f8 = MathHelper.cos(f6);
            buf.pos(f7 * 120.0F, f8 * 120.0F, -f8 * 40.0F * a).color(r, g, b, 0F).endVertex();
        }

        tes.draw();
        GlStateManager.popMatrix();
        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    private static class StarDrawList extends BatchedVertexList {

        private final AbstractRenderableTexture texture;
        private final int flickerSpeed;

        private StarDrawList(AbstractRenderableTexture texture, int flickerSpeed) {
            super(DefaultVertexFormats.POSITION_TEX, 20);

            this.texture = texture;
            this.flickerSpeed = flickerSpeed;
        }

        @Override
        public void render() {
            this.texture.bindTexture();
            super.render();
        }
    }
}
