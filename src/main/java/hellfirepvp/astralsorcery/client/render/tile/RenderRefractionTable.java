/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.model.builtin.ModelRefractionTable;
import hellfirepvp.astralsorcery.common.tile.TileRefractionTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
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

    public RenderRefractionTable(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileRefractionTable tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        if (!tile.hasParchment() && !tile.getInputStack().isEmpty()) {
            ItemStack input = tile.getInputStack();

            renderStack.push();
            renderStack.translate(0.5F, 0.85F, 0.5F);
            renderStack.scale(0.625F, 0.625F, 0.625F);

            Minecraft.getInstance().getItemRenderer().renderItem(input, ItemCameraTransforms.TransformType.GROUND, combinedLight, combinedOverlay, renderStack, renderTypeBuffer);

            renderStack.pop();
        }

        renderStack.push();
        renderStack.translate(0.5F, 1.5F, 0.5F);
        renderStack.rotate(Vector3f.XP.rotationDegrees(180F));
        renderStack.scale(0.0625F, 0.0625F, 0.0625F);

        MODEL_REFRACTION_TABLE.renderFrame(renderStack, renderTypeBuffer.getBuffer(MODEL_REFRACTION_TABLE.getGeneralType()),
                combinedLight, combinedOverlay, 1F, 1F, 1F, 1F, tile.hasParchment());

        if (!tile.getGlassStack().isEmpty()) {
            MODEL_REFRACTION_TABLE.renderGlass(renderStack, renderTypeBuffer.getBuffer(MODEL_REFRACTION_TABLE.getGeneralType()),
                    combinedLight, combinedOverlay, 1F, 1F, 1F, 1F);
        }

        renderStack.pop();
    }
}
