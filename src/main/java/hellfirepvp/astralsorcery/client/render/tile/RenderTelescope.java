/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.model.builtin.ModelTelescope;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import net.minecraft.client.renderer.RenderHelper;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderTelescope
 * Created by HellFirePvP
 * Date: 15.01.2020 / 17:11
 */
public class RenderTelescope extends CustomTileEntityRenderer<TileTelescope> {

    private static final ModelTelescope MODEL_TELESCOPE = new ModelTelescope();

    @Override
    public void render(TileTelescope tile, double x, double y, double z, float pTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 1.28, z + 0.5);
        GlStateManager.rotatef(180F, 1F, 0, 0);
        GlStateManager.rotatef(180F, 0, 1F, 0);
        GlStateManager.rotatef(tile.getRotation().ordinal() * 45F, 0, 1F, 0);
        GlStateManager.scalef(0.0625F, 0.0625F, 0.0625F);

        GlStateManager.pushMatrix();
        GlStateManager.rotatef((tile.getRotation().ordinal()) * 45F + 152.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        GlStateManager.disableCull();
        MODEL_TELESCOPE.render(destroyStage);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}
