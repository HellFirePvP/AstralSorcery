/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.model.builtin.ModelRefractionTable;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileRefractionTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderRefractionTable
 * Created by HellFirePvP
 * Date: 26.04.2020 / 21:17
 */
public class RenderRefractionTable extends CustomTileEntityRenderer<TileRefractionTable> {

    private static final ModelRefractionTable MODEL_REFRACTION_TABLE = new ModelRefractionTable();

    @Override
    public void render(TileRefractionTable tile, double x, double y, double z, float pTicks, int destroyStage) {
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        if (!tile.hasParchment() && !tile.getInputStack().isEmpty()) {
            ItemStack input = tile.getInputStack();

            GlStateManager.pushMatrix();
            GlStateManager.translated(x + 0.5, y + 0.85, z + 0.5);
            GlStateManager.scaled(0.625F, 0.625F, 0.625F);

            Minecraft.getInstance().getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.GROUND);
            GlStateManager.enableBlend();

            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 1.5, z + 0.5);
        GlStateManager.rotated(180, 1, 0, 0);
        GlStateManager.scaled(0.0625, 0.0625, 0.0625);

        MODEL_REFRACTION_TABLE.render(tile.hasParchment(), destroyStage);

        if (!tile.getGlassStack().isEmpty()) {
            GlStateManager.depthMask(false);
            MODEL_REFRACTION_TABLE.renderGlass(destroyStage);
            GlStateManager.depthMask(true);
        }

        GlStateManager.popMatrix();
        TextureHelper.refreshTextureBind();
    }
}
