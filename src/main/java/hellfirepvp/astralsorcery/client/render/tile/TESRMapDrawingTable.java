/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.base.ASstarmapper;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRMapDrawingTable
 * Created by HellFirePvP
 * Date: 23.04.2017 / 22:39
 */
public class TESRMapDrawingTable extends TileEntitySpecialRenderer<TileMapDrawingTable> {

    private static final ASstarmapper modelDrawingTable = new ASstarmapper();
    private static final BindableResource texDrawingTable = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "starmapper/astralsorcery_starmapper");

    @Override
    public void renderTileEntityAt(TileMapDrawingTable te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.scale(0.0625, 0.0625, 0.0625);

        RenderHelper.disableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        texDrawingTable.bind();
        modelDrawingTable.render(null, te.hasParchment() ? 1 : 0, 0, 0, 0, 0, 1F);
        RenderHelper.disableStandardItemLighting();
        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();


        //GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        //GL11.glPushMatrix();
        //GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
        //GL11.glRotated(180, 1, 0, 0);
        //GL11.glScaled(0.0625, 0.0625, 0.0625);
        //RenderHelper.disableStandardItemLighting();

        //GlStateManager.pushMatrix();
        ////GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        //GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        //RenderHelper.enableStandardItemLighting();
        //GlStateManager.popMatrix();

        //texAltar2.bind();
        //modelAltar2.render(null, (float) jump, 0, 0, 0, 0, 1F);
        //RenderHelper.disableStandardItemLighting();
        //GL11.glPopMatrix();
        //GL11.glPopAttrib();
    }

}
