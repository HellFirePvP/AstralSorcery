package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.obj.OBJModelLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:42
 */
public class TESRCollectorCrystal extends TileEntitySpecialRenderer<TileCollectorCrystal> implements IItemRenderer {

    private static final Random rand = new Random();

    private static final BindableResource texWhite = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_white");
    private static final BindableResource texBlue = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_blue");

    private static int dlCrystal = -1;

    @Override
    public void renderTileEntityAt(TileCollectorCrystal te, double x, double y, double z, float partialTicks, int destroyStage) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        BlockCollectorCrystalBase.CollectorCrystalType type = te.getType();
        long sBase = 1553015L;
        sBase ^= (long) te.getPos().getX();
        sBase ^= (long) te.getPos().getY();
        sBase ^= (long) te.getPos().getZ();
        Color c = type.displayColor;
        renderTileLightEffects(x, y, z, 1F, c, sBase);

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        renderCrystal(type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL, true);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void renderTileLightEffects(double x, double y, double z, float percFilled, Color effectColor, long s) {
        rand.setSeed(s);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        int fancy_count = !FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? 25 : 50;
        fancy_count = ((int) (((float) fancy_count) * percFilled));

        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();

        RenderHelper.disableStandardItemLighting();
        float f1 = Minecraft.getMinecraft().theWorld.getWorldTime() / 400.0F;
        float f2 = 0.4F;

        //Random random = new Random(245L);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        for (int i = 0; i < fancy_count; i++) {
            GL11.glRotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);
            vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            float fa = rand.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = rand.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(20, 10) / 10.0F);
            f4 /= 30.0F / (Math.min(20, 10) / 10.0F);
            vb.pos(0, 0, 0).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), (int) (255.0F * (1.0F - f2))).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos( 0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos( 0.0D,      fa,    1.0F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            tes.draw();
        }
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

    public static void renderCrystal(boolean isCelestial, boolean bounce) {
        GL11.glPushMatrix();
        if(bounce) {
            int t = (int) (Minecraft.getMinecraft().theWorld.getTotalWorldTime() & 255);
            float perc = (256 - t) / 256F;
            perc = MathHelper.cos((float) (perc * 2 * Math.PI));
            GL11.glTranslated(0, 0.03 * perc, 0);
        }
        RenderHelper.disableStandardItemLighting();
        if(isCelestial) {
            renderTile(texBlue);
        } else {
            renderTile(texWhite);
        }
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
    }

    private static void renderTile(BindableResource tex) {
        GL11.glPushMatrix();
        GL11.glScalef(0.13F, 0.13F, 0.13F);
        tex.bind();
        if(dlCrystal == -1) {
            dlCrystal = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(dlCrystal, GL11.GL_COMPILE);
            OBJModelLibrary.bigCrystal.renderAll(true);
            GL11.glEndList();
        }
        GL11.glCallList(dlCrystal);

        GL11.glPopMatrix();
    }

    @Override
    public void render(ItemStack stack) {
        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 0, 0.5);
        RenderHelper.disableStandardItemLighting();
        BlockCollectorCrystalBase.CollectorCrystalType type = ItemCollectorCrystal.getType(stack);
        switch (type) {
            case ROCK_CRYSTAL:
                renderTile(texWhite);
                break;
            case CELESTIAL_CRYSTAL:
                renderTile(texBlue);
                break;
        }
        GL11.glPopMatrix();
        RenderHelper.enableStandardItemLighting();
    }

}
