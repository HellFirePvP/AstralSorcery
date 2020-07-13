/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderChalice
 * Created by HellFirePvP
 * Date: 11.11.2019 / 20:27
 */
public class RenderChalice extends CustomTileEntityRenderer<TileChalice> {

    public RenderChalice(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileChalice tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        FluidStack stack = tile.getTank().getFluid();
        if (stack.isEmpty()) {
            return;
        }
        TextureAtlasSprite tas = RenderingUtils.getParticleTexture(stack);
        if (tas == null) {
            return;
        }

        Vector3 rotation = RenderingVectorUtils.interpolate(tile.getPrevRotation(), tile.getRotation(), pTicks);
        Color color = new Color(ColorUtils.getOverlayColor(stack));
        float percSize = 0.125F + (tile.getTank().getPercentageFilled() * 0.375F);

        float ulength = tas.getMaxU() - tas.getMinU();
        float vlength = tas.getMaxV() - tas.getMinV();

        float uPart = ulength * percSize;
        float vPart = vlength * percSize;
        float uOffset = tas.getMinU() + ulength / 2F - uPart / 2F;
        float vOffset = tas.getMinV() + vlength / 2F - vPart / 2F;

        renderStack.push();
        renderStack.translate(0.5F, 1.4F, 0.5F);
        renderStack.rotate(Vector3f.XP.rotationDegrees((float) rotation.getX()));
        renderStack.rotate(Vector3f.YP.rotationDegrees((float) rotation.getY()));
        renderStack.rotate(Vector3f.ZP.rotationDegrees((float) rotation.getZ()));
        renderStack.scale(percSize, percSize, percSize);

        IVertexBuilder buf = renderTypeBuffer.getBuffer(RenderTypesAS.TER_CHALICE_LIQUID);
        RenderingDrawUtils.renderTexturedCubeCentralColorNormal(renderStack, buf,
                uOffset, vOffset, uPart, vPart,
                color.getRed(), color.getGreen(), color.getBlue(), 255,
                renderStack.getLast().getNormal());

        renderStack.pop();
    }
}
