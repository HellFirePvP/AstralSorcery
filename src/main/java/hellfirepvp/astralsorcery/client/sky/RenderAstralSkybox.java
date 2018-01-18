/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.mappings.ClientConstellationPositionMapping;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.CelestialEvent;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderAstralSkybox
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:50
 */
public class RenderAstralSkybox extends IRenderHandler {

    private long worldSeed;
    private boolean initialized = false;

    private static final ResourceLocation MC_DEF_SUN_PNG = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation MC_DEF_MOON_PHASES_PNG = new ResourceLocation("textures/environment/moon_phases.png");

    public static final BindableResource TEX_STAR_1 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1");
    public static final BindableResource TEX_STAR_2 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star2");
    public static final BindableResource TEX_STAR_3 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star2");
    public static final BindableResource TEX_STAR_4 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1");

    public static final BindableResource TEX_CONNECTION = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");
    public static final BindableResource TEX_SOLAR_ECLIPSE = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "solareclipse");

    private static int glSkyList = -1; //Sky background vertices.
    private static int glSkyList2 = -1; // - "" -

    private static final int[] starAmountMap = new int[]{200, 200, 100, 100, 100, /**/ 200, 100, 50, 50, 100, /**/ 50, 50, 100, 100, 100, /**/ 50, 50, 100, 100, 100};
    private static final double[] starSizeMap = new double[]{1, 1, 1, 1.2, 1,    /**/ 1, 1.1, 1.2, 1, 1,     /**/ 1.2, 1.1, 1, 1, 1,      /**/ 1.2, 1.3, 1, 1, 1};
    private static StarDList[] starLists = new StarDList[0];

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        if (!isInitialized()) return;

        //Avg 0,36-0,5ms rendering time.

        //long n = System.nanoTime();
        //GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        //GL11.glPushMatrix();
        renderSky(partialTicks);
        //GL11.glPopMatrix();
        //GL11.glPopAttrib();
        //AstralSorcery.log.info(System.nanoTime() - n);
    }

    public void refreshRender() {
        initialized = false;
    }

    public boolean isInitialized() {
        return initialized;
    }

    //Sets up skybox with given seed.
    public void setInitialized(long worldSeed) {
        this.worldSeed = worldSeed;
        setupSkybox();
        setupStarVertices();
        this.initialized = true;
    }

    private void setupStarVertices() {
        cleanStarVertices();

        starLists = new StarDList[20];
        for (int i = 0; i < starLists.length; i++) {
            starLists[i] = new StarDList();
        }

        BufferBuilder vb = Tessellator.getInstance().getBuffer();

        Random vRand = new Random(worldSeed);
        int list = GLAllocation.generateDisplayLists(20);
        for (int i = 0; i < starLists.length; i++) {
            StarDList l = starLists[i];
            l.glList = list + i;
            l.sinDivisor = 10 + vRand.nextInt(15);
            switch (i / (starLists.length / 4)) {
                case 0:
                    l.resource = TEX_STAR_1;
                    break;
                case 1:
                    l.resource = TEX_STAR_2;
                    break;
                case 2:
                    l.resource = TEX_STAR_3;
                    break;
                case 3:
                    l.resource = TEX_STAR_4;
                    break;
            }
            GlStateManager.glNewList(l.glList, GL11.GL_COMPILE);
            l.resource.bind();
            vb.begin(7, DefaultVertexFormats.POSITION_TEX);
            setupStars(vb, vRand, starAmountMap[i], starSizeMap[i]);
            Tessellator.getInstance().draw();
            GlStateManager.glEndList();
        }
    }

    private void cleanStarVertices() {
        for (StarDList list : starLists) {
            if (list.glList != -1) {
                GLAllocation.deleteDisplayLists(list.glList);
                list.glList = -1;
            }
        }
    }

    private void setupStars(BufferBuilder vb, Random random, int amount, double multiplier) {
        for (int i = 0; i < amount; ++i) { //Amount of stars.
            double x = (double) (random.nextFloat() * 2.0F - 1.0F);
            double y = (double) (random.nextFloat() * 2.0F - 1.0F);
            double z = (double) (random.nextFloat() * 2.0F - 1.0F);
            double ovrSize = (double) (0.15F + random.nextFloat() * 0.2F); //Size flat increase.
            double d4 = x * x + y * y + z * z;
            if (d4 < 1.0D && d4 > 0.01D) {

                //d4 = Vector3.fastInvSqrt(d4);
                d4 = 1.0D / Math.sqrt(d4);
                x *= d4;
                y *= d4;
                z *= d4;

                double d5 = x * 100.0D;
                double d6 = y * 100.0D;
                double d7 = z * 100.0D;

                double d8 = Math.atan2(x, z);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);

                double d11 = Math.atan2(Math.sqrt(x * x + z * z), y);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);

                //Sizes
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double size = Math.sin(d14) * 2; //Size percentage increase.
                double d16 = Math.cos(d14);

                size *= multiplier;

                //Set 2D vertices
                for (int j = 0; j < 4; ++j) {
                    double d18 = (double) ((j & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[
                    double d19 = (double) ((j + 1 & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[

                    double d21 = d18 * d16 - d19 * size;
                    double d22 = d19 * d16 + d18 * size;
                    double d23 = d21 * d12 + 0.0D * d13;

                    double d24 = 0.0D * d12 - d21 * d13;

                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;

                    vb.pos(d5 + d25, d6 + d23, d7 + d26).tex(((j + 1) & 2) >> 1, ((j + 2) & 2) >> 1).endVertex();
                }
            }
        }
    }

    private void setupSkybox() {
        if (glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(glSkyList);
            glSkyList = -1;
        }
        glSkyList = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(glSkyList, GL11.GL_COMPILE);
        setupBackground(false);
        Tessellator.getInstance().draw();
        GlStateManager.glEndList();

        if (glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(glSkyList2);
            glSkyList2 = -1;
        }
        glSkyList2 = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(glSkyList2, GL11.GL_COMPILE);
        setupBackground(true);
        Tessellator.getInstance().draw();
        GlStateManager.glEndList();
    }

    private void setupBackground(boolean invert) {
        BufferBuilder vb = Tessellator.getInstance().getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION);

        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {
                float px = k + 64;
                float p = k;
                if (invert) {
                    px = k;
                    p = k + 64;
                }
                vb.pos(p, 16, l).endVertex();
                vb.pos(px, 16, l).endVertex();
                vb.pos(px, 16, l + 64).endVertex();
                vb.pos(p, 16, l + 64).endVertex();
            }
        }
    }

    private void renderSky(float partialTicks) {
        GlStateManager.disableTexture2D();
        Vec3d vec3 = Minecraft.getMinecraft().world.getSkyColor(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);
        float f = (float) vec3.x;
        float f1 = (float) vec3.y;
        float f2 = (float) vec3.z;

        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();

        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(f, f1, f2);

        GlStateManager.callList(glSkyList);

        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();
        float[] sunsetColors = Minecraft.getMinecraft().world.provider.calcSunriseSunsetColors(Minecraft.getMinecraft().world.getCelestialAngle(partialTicks), partialTicks);
        if (sunsetColors != null) {
            renderSunsetToBackground(sunsetColors, partialTicks);
        }
        renderDefaultCelestials(partialTicks);

        double absPlayerHorizon = Minecraft.getMinecraft().player.getPositionEyes(partialTicks).y - Minecraft.getMinecraft().world.getHorizon();
        if (absPlayerHorizon < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);
            GlStateManager.callList(glSkyList2);
            GlStateManager.popMatrix();
        }

        if (Minecraft.getMinecraft().world.provider.isSkyColored()) {
            GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
        } else {
            GlStateManager.color(f, f1, f2);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -((float)(absPlayerHorizon - 16.0D)), 0.0F);
        //GlStateManager.callList(glSkyList2); //TODO
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }

    private void renderDefaultCelestials(float partialTicks) {
        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();

        //Bind alpha according to rain strength - if it rains "completely", moon, sun and stars are not rendered.
        float alphaSubRain = 1.0F - Minecraft.getMinecraft().world.getRainStrength(partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alphaSubRain);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        if(handle != null && handle.getCurrentlyActiveEvent() == CelestialEvent.SOLAR_ECLIPSE) {
            renderSolarEclipseSun(handle);
        } else {
            renderSun();
        }

        if(handle != null && handle.getCurrentlyActiveEvent() == CelestialEvent.LUNAR_ECLIPSE) {
            int eclTick = handle.lunarEclipseTick;
            if (eclTick >= ConstellationSkyHandler.LUNAR_ECLIPSE_HALF_DUR) { //fading out
                eclTick -= ConstellationSkyHandler.LUNAR_ECLIPSE_HALF_DUR;
            } else {
                eclTick = ConstellationSkyHandler.LUNAR_ECLIPSE_HALF_DUR - eclTick;
            }
            float perc = ((float) eclTick) / ConstellationSkyHandler.LUNAR_ECLIPSE_HALF_DUR;
            GlStateManager.color(1F, 0.4F + (0.6F * perc), 0.4F + (0.6F * perc), alphaSubRain);
            renderMoon();
        } else {
            renderMoon();
        }

        renderStars(Minecraft.getMinecraft().world, partialTicks);

        renderConstellations(Minecraft.getMinecraft().world, partialTicks);

        /*Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        List<double[]> poss = new LinkedList<>();

        poss.add(new double[] { 0.2,  -0.2,     0,   5});
        poss.add(new double[] {-0.2,  -0.2,  -0.05,  5});
        poss.add(new double[] {   0,  -0.25, -0.2,   8});
        poss.add(new double[] {-0.4,  -0.6,   0.5,  18});
        poss.add(new double[] { 0.3,  -0.5,   0.5,  19});
        poss.add(new double[] { 0.15, -0.2,  -0.1,   5});
        poss.add(new double[] {-0.05, -0.3,   0.4,  10});
        poss.add(new double[] {-0.3,  -0.3,   0.1,  10});
        poss.add(new double[] {-0.3,  -0.4,  -0.35, 15});
        poss.add(new double[] { 0.4,  -0.4,   0.2,  15});

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
            //                                pythagoras?
            double d11 = Math.atan2(Math.sqrt(x * x + z * z), y); // [-PI - PI]
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);
            //double d14 = random.nextDouble() * Math.PI * 2.0D;
            //double d16 = Math.cos(d14); rotation!
            double rotation = 0;
            GL11.glColor4f(1F, 1F, 1F, 1F);
            TEX_DEBUG.bind();
            vb.begin(7, DefaultVertexFormats.POSITION_TEX);
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
        TextureHelper.refreshTextureBindState();*/

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F);
    }

    private void renderSolarEclipseSun(WorldSkyHandler handle) {
        double xzSize = 30F;

        float part = ((float) ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR * 2) / 7F;
        float u = 0;
        float tick = handle.solarEclipseTick;
        while (tick - part > 0) {
            tick -= part;
            u += 1;
        }

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-90, 0, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        TEX_SOLAR_ECLIPSE.bind();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(-xzSize, 100.0D, -xzSize).tex( u      / 7F, 0.0D).endVertex();
        vb.pos( xzSize, 100.0D, -xzSize).tex((u + 1) / 7F, 0.0D).endVertex();
        vb.pos( xzSize, 100.0D,  xzSize).tex((u + 1) / 7F, 1.0D).endVertex();
        vb.pos(-xzSize, 100.0D,  xzSize).tex( u      / 7F, 1.0D).endVertex();
        tessellator.draw();
        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();
    }

    public static void renderConstellationsWrapped(final World w, final float pticks) {
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        float alphaSubRain = 1.0F - Minecraft.getMinecraft().world.getRainStrength(pticks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alphaSubRain);
        GlStateManager.rotate(-90F, 0F, 1F, 0F);
        GlStateManager.rotate(Minecraft.getMinecraft().world.getCelestialAngle(pticks) * 360.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(false);

        renderConstellations(w, pticks);

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GlStateManager.color(0, 0, 0);
    }

    private static void renderConstellations(final World w, final float partialTicks) {
        long wTime = w.getWorldTime() % 24000;
        if (wTime < 12000) return; //Daytime.
        float rainDim = 1.0F - w.getRainStrength(partialTicks);
        final float brightness = w.getStarBrightness(partialTicks) * rainDim;
        if (brightness <= 0.0F) return;
        final Random flRand = new Random(w.getSeed());

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(w);
        if(handle != null) {
            ClientConstellationPositionMapping mapping = handle.getConstellationPositionMapping();
            if(mapping != null) {
                Map<IConstellation, ClientConstellationPositionMapping.RenderPosition> renderMap = mapping.getCurrentRenderPositions();
                for (Map.Entry<IConstellation, ClientConstellationPositionMapping.RenderPosition> entry : renderMap.entrySet()) {
                    IConstellation c = entry.getKey();
                    if (!ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName()) ||
                            !handle.isActive(c)) continue;

                    RenderConstellation.renderConstellation(c, entry.getValue(), new RenderConstellation.BrightnessFunction() {
                        @Override
                        public float getBrightness() {
                            return RenderConstellation.conCFlicker(w.getWorldTime(), partialTicks, 5 + flRand.nextInt(10)) * (2 * brightness);
                        }
                    });
                }
            }
        }
    }

    private void renderMoon() {
        double xzSize = 20F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(MC_DEF_MOON_PHASES_PNG);
        int i = Minecraft.getMinecraft().world.getMoonPhase();
        int k = i % 4;
        int i1 = i / 4 % 2;
        float maxU = (float) (k) / 4.0F;
        float maxV = (float) (i1) / 2.0F;
        float minU = (float) (k + 1) / 4.0F;
        float minV = (float) (i1 + 1) / 2.0F;

        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(-xzSize, -100.0D,  xzSize).tex((double) minU, (double) minV).endVertex();
        vb.pos( xzSize, -100.0D,  xzSize).tex((double) maxU, (double) minV).endVertex();
        vb.pos( xzSize, -100.0D, -xzSize).tex((double) maxU, (double) maxV).endVertex();
        vb.pos(-xzSize, -100.0D, -xzSize).tex((double) minU, (double) maxV).endVertex();
        tessellator.draw();
    }

    private void renderSun() {
        double xzSize = 30F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(MC_DEF_SUN_PNG);

        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(-xzSize, 100.0D, -xzSize).tex(0.0D, 0.0D).endVertex();
        vb.pos( xzSize, 100.0D, -xzSize).tex(1.0D, 0.0D).endVertex();
        vb.pos( xzSize, 100.0D,  xzSize).tex(1.0D, 1.0D).endVertex();
        vb.pos(-xzSize, 100.0D,  xzSize).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
    }

    private void renderStars(World w, float partialTicks) {
        float rainDim = 1.0F - w.getRainStrength(partialTicks);
        float brightness = w.getStarBrightness(partialTicks) * rainDim;
        TextureHelper.refreshTextureBindState();

        if (brightness > 0.0F) {
            Tessellator tes = Tessellator.getInstance();
            BufferBuilder vb = tes.getBuffer();
            for (StarDList list : starLists) {
                if (list.glList > 0) {
                    float sinBr = RenderConstellation.stdFlicker(w.getWorldTime(), partialTicks, list.sinDivisor) - brightness;
                    GlStateManager.color(brightness, brightness, brightness, sinBr < 0 ? 0 : sinBr);
                    list.resource.bind();
                    vb.begin(7, DefaultVertexFormats.POSITION_TEX);
                    GlStateManager.callList(list.glList);
                    tes.draw();
                    TextureHelper.refreshTextureBindState();
                }
            }
        }
    }

    private void renderSunsetToBackground(float[] sunsetColors, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(MathHelper.sin(Minecraft.getMinecraft().world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        float f6 = sunsetColors[0];
        float f7 = sunsetColors[1];
        float f8 = sunsetColors[2];

        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
            float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
            float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
            f6 = f9;
            f7 = f10;
            f8 = f11;
        }

        vb.begin(6, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, sunsetColors[3]).endVertex();
        //int j = 16;

        for (int l = 0; l <= 16; ++l) {
            float f21 = (float) l * (float) Math.PI * 2.0F / 16.0F;
            float f12 = MathHelper.sin(f21);
            float f13 = MathHelper.cos(f21);
            vb.pos((double) (f12 * 120.0F), (double) (f13 * 120.0F), (double) (-f13 * 40.0F * sunsetColors[3])).color(sunsetColors[0], sunsetColors[1], sunsetColors[2], 0.0F).endVertex();
        }

        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    private static class StarDList {

        private int glList = -1;
        private BindableResource resource;
        private int sinDivisor = 1;

    }

}
