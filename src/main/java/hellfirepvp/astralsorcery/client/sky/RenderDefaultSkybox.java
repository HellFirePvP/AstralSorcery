/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderDefaultSkybox
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:44
 */
public class RenderDefaultSkybox extends IRenderHandler {

    private static VertexBuffer skyVBO;
    private static VertexBuffer sky2VBO;
    private static VertexBuffer starVBO;

    private static int starGLCallList = -1;
    private static int glSkyList = -1;
    private static int glSkyList2 = -1;

    private static VertexFormat vertexBufferFormat;

    private static final ResourceLocation MC_DEF_SUN_PNG = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation MC_DEF_MOON_PHASES_PNG = new ResourceLocation("textures/environment/moon_phases.png");

    public static void setupDefaultSkybox() {
        vertexBufferFormat = new VertexFormat();
        vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));

        Tessellator tessellator = Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder vb = tessellator.getBuffer();

        setupStars(vb);
        setupSky1(vb);
        setupSky2(vb);
    }

    private static void setupSky2(net.minecraft.client.renderer.BufferBuilder vb) {
        if (sky2VBO != null) sky2VBO.deleteGlBuffers();

        if (glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(glSkyList2);
            glSkyList2 = -1;
        }

        if (OpenGlHelper.useVbo()) {
            sky2VBO = new VertexBuffer(vertexBufferFormat);
            setupSkyVertices(vb, -16.0F, true);
            vb.finishDrawing();
            vb.reset();
            sky2VBO.bufferData(vb.getByteBuffer());
        } else {
            glSkyList2 = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(glSkyList2, GL11.GL_COMPILE);
            setupSkyVertices(vb, -16.0F, true);
            Tessellator.getInstance().draw();
            GL11.glEndList();
        }
    }

    private static void setupSky1(net.minecraft.client.renderer.BufferBuilder vb) {
        if (skyVBO != null) skyVBO.deleteGlBuffers();

        if (glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(glSkyList);
            glSkyList = -1;
        }

        if (OpenGlHelper.useVbo()) {
            skyVBO = new VertexBuffer(vertexBufferFormat);
            setupSkyVertices(vb, 16.0F, false);
            vb.finishDrawing();
            vb.reset();
            skyVBO.bufferData(vb.getByteBuffer());
        } else {
            glSkyList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(glSkyList, GL11.GL_COMPILE);
            setupSkyVertices(vb, 16.0F, false);
            Tessellator.getInstance().draw();
            GL11.glEndList();
        }
    }

    private static void setupStars(net.minecraft.client.renderer.BufferBuilder vb) {
        if (starVBO != null) starVBO.deleteGlBuffers();

        if (starGLCallList >= 0) {
            GLAllocation.deleteDisplayLists(starGLCallList);
            starGLCallList = -1;
        }

        if (OpenGlHelper.useVbo()) {
            starVBO = new VertexBuffer(vertexBufferFormat);
            setupStarVertices(vb);
            vb.finishDrawing();
            vb.reset();
            starVBO.bufferData(vb.getByteBuffer());
        } else {
            starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GL11.glNewList(starGLCallList, GL11.GL_COMPILE);
            setupStarVertices(vb);
            Tessellator.getInstance().draw();
            GL11.glEndList();
            GlStateManager.popMatrix();
        }
    }

    private static void setupSkyVertices(net.minecraft.client.renderer.BufferBuilder vb, float y, boolean invert) {
        vb.begin(7, DefaultVertexFormats.POSITION);

        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {

                float f = (float) k;
                float f1 = (float) (k + 64);

                if (invert) {
                    f1 = (float) k;
                    f = (float) (k + 64);
                }

                vb.pos((double) f, (double) y, (double) l).endVertex();
                vb.pos((double) f1, (double) y, (double) l).endVertex();
                vb.pos((double) f1, (double) y, (double) (l + 64)).endVertex();
                vb.pos((double) f, (double) y, (double) (l + 64)).endVertex();
            }
        }
    }

    private static void setupStarVertices(net.minecraft.client.renderer.BufferBuilder vb) {
        Random random = new Random(10842L);
        vb.begin(7, DefaultVertexFormats.POSITION);
        for (int i = 0; i < 1500; ++i) {
            double x = (double) (random.nextFloat() * 2.0F - 1.0F);
            double y = (double) (random.nextFloat() * 2.0F - 1.0F);
            double z = (double) (random.nextFloat() * 2.0F - 1.0F);
            double ovrSize = (double) (0.15F + random.nextFloat() * 0.1F); //Size flat increase.
            double d4 = x * x + y * y + z * z;
            if (d4 < 1.0D && d4 > 0.01D) {

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
                double size = Math.sin(d14); //Size percentage increase.
                double d16 = Math.cos(d14);

                //Set 2D vertices
                for (int j = 0; j < 4; ++j) {
                    //double d17 = 0.0D;
                    double d18 = (double) ((j & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[
                    double d19 = (double) ((j + 1 & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[
                    //double d20 = 0.0D;

                    double d21 = d18 * d16 - d19 * size;
                    double d22 = d19 * d16 + d18 * size;
                    double d23 = d21 * d12 + 0.0D * d13;

                    double d24 = 0.0D * d12 - d21 * d13;

                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    vb.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                }
            }
        }
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        renderDefaultSkybox(partialTicks);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void renderDefaultSkybox(float partialTicks) {
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

        GlStateManager.color(f, f1, f2);
        Tessellator tessellator = Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder vb = tessellator.getBuffer();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(f, f1, f2);

        if (OpenGlHelper.useVbo()) {
            skyVBO.bindBuffer();
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
            skyVBO.drawArrays(7);
            skyVBO.unbindBuffer();
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        } else {
            GlStateManager.callList(glSkyList);
        }

        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        float[] afloat = Minecraft.getMinecraft().world.provider.calcSunriseSunsetColors(Minecraft.getMinecraft().world.getCelestialAngle(partialTicks), partialTicks);

        if (afloat != null) {
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(Minecraft.getMinecraft().world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            float f6 = afloat[0];
            float f7 = afloat[1];
            float f8 = afloat[2];

            if (Minecraft.getMinecraft().gameSettings.anaglyph) {
                float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                f6 = f9;
                f7 = f10;
                f8 = f11;
            }

            vb.begin(6, DefaultVertexFormats.POSITION_COLOR);
            vb.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
            //int j = 16;

            for (int l = 0; l <= 16; ++l) {
                float f21 = (float) l * (float) Math.PI * 2.0F / 16.0F;
                float f12 = MathHelper.sin(f21);
                float f13 = MathHelper.cos(f21);
                vb.pos((double) (f12 * 120.0F), (double) (f13 * 120.0F), (double) (-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
        }

        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        GlStateManager.pushMatrix();
        float f16 = 1.0F - Minecraft.getMinecraft().world.getRainStrength(partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        float f17 = 30.0F;
        Minecraft.getMinecraft().renderEngine.bindTexture(MC_DEF_SUN_PNG);
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos((double) (-f17), 100.0D, (double) (-f17)).tex(0.0D, 0.0D).endVertex();
        vb.pos((double) f17, 100.0D, (double) (-f17)).tex(1.0D, 0.0D).endVertex();
        vb.pos((double) f17, 100.0D, (double) f17).tex(1.0D, 1.0D).endVertex();
        vb.pos((double) (-f17), 100.0D, (double) f17).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
        f17 = 20.0F;
        Minecraft.getMinecraft().renderEngine.bindTexture(MC_DEF_MOON_PHASES_PNG);
        int i = Minecraft.getMinecraft().world.getMoonPhase();
        int k = i % 4;
        int i1 = i / 4 % 2;
        float f22 = (float) (k) / 4.0F;
        float f23 = (float) (i1) / 2.0F;
        float f24 = (float) (k + 1) / 4.0F;
        float f14 = (float) (i1 + 1) / 2.0F;
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos((double) (-f17), -100.0D, (double) f17).tex((double) f24, (double) f14).endVertex();
        vb.pos((double) f17, -100.0D, (double) f17).tex((double) f22, (double) f14).endVertex();
        vb.pos((double) f17, -100.0D, (double) (-f17)).tex((double) f22, (double) f23).endVertex();
        vb.pos((double) (-f17), -100.0D, (double) (-f17)).tex((double) f24, (double) f23).endVertex();
        tessellator.draw();
        GlStateManager.disableTexture2D();
        float f15 = Minecraft.getMinecraft().world.getStarBrightness(partialTicks) * f16;

        if (f15 > 0.0F) {
            GlStateManager.color(f15, f15, f15, f15);

            if (OpenGlHelper.useVbo()) {
                starVBO.bindBuffer();
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                starVBO.drawArrays(7);
                starVBO.unbindBuffer();
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            } else {
                GlStateManager.callList(starGLCallList);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F);
        double d0 = Minecraft.getMinecraft().player.getPositionEyes(partialTicks).y - Minecraft.getMinecraft().world.getHorizon();

        if (d0 < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);

            if (OpenGlHelper.useVbo()) {
                sky2VBO.bindBuffer();
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                sky2VBO.drawArrays(7);
                sky2VBO.unbindBuffer();
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            } else {
                GlStateManager.callList(glSkyList2);
            }

            GlStateManager.popMatrix();
            //float f18 = 1.0F;
            float f19 = -((float) (d0 + 65.0D));
            //float f20 = -1.0F;
            vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
            vb.pos(-1.0D, (double) f19, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, (double) f19, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, (double) f19, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, (double) f19, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, (double) f19, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, (double) f19, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, (double) f19, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, (double) f19, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            vb.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
        }

        if (Minecraft.getMinecraft().world.provider.isSkyColored()) {
            GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
        } else {
            GlStateManager.color(f, f1, f2);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -((float) (d0 - 16.0D)), 0.0F);
        GlStateManager.callList(glSkyList2);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }

}
