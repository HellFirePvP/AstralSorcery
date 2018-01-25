/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.base.ASlens;
import hellfirepvp.astralsorcery.client.models.base.ASlens_color;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRLens
 * Created by HellFirePvP
 * Date: 20.09.2016 / 13:07
 */
public class TESRLens extends TileEntitySpecialRenderer<TileCrystalLens> {

    private static final ASlens modelLensPart = new ASlens();
    private static final BindableResource texLensPart = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "base/lens");

    private static final ASlens_color modelLensColoredFrame = new ASlens_color();
    private static final BindableResource texLensColorFrame = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "lens/lens_color");

    @Override
    public void render(TileCrystalLens te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        List<BlockPos> linked = te.getLinkedPositions();
        float yaw = 0; //Degree
        float pitch = 0; //Degree
        if(!linked.isEmpty() && linked.size() == 1) {
            BlockPos to = linked.get(0);
            BlockPos from = te.getTrPos();
            Vector3 dir = new Vector3(to).subtract(new Vector3(from));

            pitch = (float) Math.atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));

            yaw = (float) Math.atan2(dir.getX(), dir.getZ());

            yaw = 180F + (float) Math.toDegrees(-yaw);
            pitch = (float) Math.toDegrees(pitch);
        }
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 1.335, z + 0.5);
        GL11.glScaled(0.055, 0.055, 0.055);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glRotated(yaw % 360, 0, 1, 0);
        renderHandle(yaw, pitch);
        if(te.getLensColor() != null) {
            GL11.glRotated(180, 0, 1, 0);
            Color c = te.getLensColor().wrappedColor;
            GL11.glColor4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
            renderColoredLens(yaw, -pitch);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
        TextureHelper.refreshTextureBindState();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderColoredLens(float yaw, float pitch) {
        texLensColorFrame.bind();
        modelLensColoredFrame.render(null, yaw, pitch, 0, 0, 0, 1);
    }

    private void renderHandle(float yaw, float pitch) {

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        texLensPart.bind();
        modelLensPart.render(null, yaw, pitch, 0, 0, 0, 1);
        RenderHelper.disableStandardItemLighting();
    }

}
