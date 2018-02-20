/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.base.ASgrindingstone;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.auxiliary.SwordSharpenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRGrindstone
 * Created by HellFirePvP
 * Date: 10.11.2016 / 22:30
 */
public class TESRGrindstone extends TileEntitySpecialRenderer<TileGrindstone> {

    private static final ASgrindingstone modelGrindstone = new ASgrindingstone();
    private static final BindableResource texGrindstone = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "base/grindingstone");

    @Override
    public void render(TileGrindstone te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.65, z + 0.5);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.scale(0.067, 0.067, 0.067);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        renderModel(te, 1);
        GlStateManager.popMatrix();
        GL11.glPopAttrib();

        ItemStack grind = te.getGrindingItem();
        if(!grind.isEmpty()) {
            TextureHelper.refreshTextureBindState();
            TextureHelper.setActiveTextureToAtlasSprite();

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.75, z + 0.6);
            GL11.glRotated(125, 1, 0, 0);
            GL11.glRotated(180, 0, 0, 1);

            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItem(grind, ItemCameraTransforms.TransformType.GROUND);
            RenderHelper.disableStandardItemLighting();
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void renderModel(TileGrindstone te, float partialTicks) {
        texGrindstone.bind();
        double oldDeg = (((double) te.prevTickWheelAnimation) / (20D) * 360) % 360;
        double newDeg = (((double) te.tickWheelAnimation)     / (20D) * 360) % 360;
        modelGrindstone.render(null, (float) RenderingUtils.interpolate(oldDeg, newDeg, partialTicks), 0, 0, 0, 0, 1);
    }

}
