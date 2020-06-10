/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.model.builtin.ModelTelescope;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

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

    public RenderTelescope(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileTelescope tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        renderStack.push();
        renderStack.translate(0.5F, 1.28F, 0.5F);
        renderStack.rotate(Vector3f.XP.rotationDegrees(180F));
        renderStack.rotate(Vector3f.YP.rotationDegrees(180F));
        renderStack.rotate(Vector3f.ZP.rotationDegrees(tile.getRotation().ordinal() * 45F));
        renderStack.scale(0.0625F, 0.0625F, 0.0625F);

        MODEL_TELESCOPE.render(renderStack, renderTypeBuffer.getBuffer(MODEL_TELESCOPE.getGeneralType()), combinedLight, combinedOverlay);

        renderStack.pop();
    }
}
