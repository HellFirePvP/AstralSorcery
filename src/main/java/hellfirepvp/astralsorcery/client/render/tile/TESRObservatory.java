/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.base.ASobservatory;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRObservatory
 * Created by HellFirePvP
 * Date: 26.05.2018 / 16:04
 */
public class TESRObservatory extends TileEntitySpecialRenderer<TileObservatory> {

    private static final ASobservatory modelTelescope = new ASobservatory();
    private static final BindableResource texTelescope = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "base/observatory");

    @Override
    public void render(TileObservatory te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.28, z + 0.5);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(0.053, 0.053, 0.053);

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        renderModel(te, 1);
        GlStateManager.popMatrix();
        GL11.glPopAttrib();
    }

    private void renderModel(TileObservatory te, float partialTicks) {
        texTelescope.bind();
        GlStateManager.disableCull();
        modelTelescope.render(null, 0, 0, 0, 0, 0, 1);
        GlStateManager.enableCull();
    }
}
