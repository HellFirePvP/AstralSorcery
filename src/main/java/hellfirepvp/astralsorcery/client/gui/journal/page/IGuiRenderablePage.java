package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IGuiRenderablePage
 * Created by HellFirePvP
 * Date: 30.08.2016 / 11:21
 */
public interface IGuiRenderablePage {

    static final BindableResource resStar = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1");

    public void render(float offsetX, float offsetY, float pTicks, float zLevel);

    default public void drawItemStack(ItemStack stack, int offsetX, int offsetY, float zLevel) {
        drawItemStack(stack, offsetX, offsetY, zLevel, getStandardFontRenderer(), getRenderItem());
    }

    default public void drawItemStack(ItemStack stack, int offsetX, int offsetY, float zLevel, FontRenderer fontRenderer, RenderItem ri) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        float zIR = ri.zLevel;
        ri.zLevel = zLevel;

        ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().thePlayer, stack, offsetX, offsetY);
        ri.renderItemOverlayIntoGUI  (fontRenderer,                       stack, offsetX, offsetY, null);

        GlStateManager.enableAlpha(); //Because Mc item rendering..
        ri.zLevel = zIR;
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    //TODO uuuuuuh...... do?
    default public Rectangle drawInfoStar(float offsetX, float offsetY, float zLevel, float widthHeight, float pTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glTranslated(offsetX, offsetY, zLevel);
        resStar.bind();
        double deg = ((ClientScheduler.getClientTick() + pTicks) % 360F);
        Vector3 offset = new Vector3(0, -widthHeight, 0);
        //RenderingUtils.renderAngleRotatedTexturedRect(new Vector3(0, 0, 0), Vector3.RotAxis.Z_AXIS, Math.toRadians(deg), widthHeight, 0, 0, 1, 1, pTicks);
        //Vector3 offset = new Vector3(1, 0, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        //GL11.glTranslated(offset.getX(), offset.getY(), offset.getZ());
        //GL11.glRotated(deg, 0, 0, 1);
        //drawRect(-widthHeight / 2F, -widthHeight / 2F, widthHeight / 2F, widthHeight / 2F, zLevel);

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        return null;
    }

    default public void drawRect(double offsetX, double offsetY, double width, double height, double zLevel) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

    default public void drawRectPart(double offsetX, double offsetY, double width, double height, double zLevel, double u, double v, double uLength, double vLength) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(u,           v + vLength).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(u + uLength, v)          .endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(u,           v)          .endVertex();
        tes.draw();
    }

    default public RenderItem getRenderItem() {
        return Minecraft.getMinecraft().getRenderItem();
    }

    default public FontRenderer getStandardFontRenderer() {
        return Minecraft.getMinecraft().fontRendererObj;
    }

    default public FontRenderer getStandardGalFontRenderer() {
        return Minecraft.getMinecraft().standardGalacticFontRenderer;
    }

    default public String getDescriptionFromStarlightAmount(int amtRequired) {
        String base = "astralsorcery.journal.recipe.amt.";
        String ext;
        if(amtRequired <= 100) {
            ext = "lowest";
        } else if(amtRequired <= 500) {
            ext = "low";
        } else if(amtRequired <= 1000) {
            ext = "avg";
        } else if(amtRequired <= 2500) {
            ext = "more";
        } else if(amtRequired <= 4000) {
            ext = "high";
        } else {
            ext = "highest";
        }
        return base + ext;
    }

}
