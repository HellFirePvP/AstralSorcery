/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderChalice
 * Created by HellFirePvP
 * Date: 11.11.2019 / 20:27
 */
public class RenderChalice extends CustomTileEntityRenderer<TileChalice> {

    @Override
    public void render(TileChalice tile, double x, double y, double z, float pTicks, int destroyStage) {
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

        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 1.4, z + 0.5);
        GlStateManager.rotated((float) rotation.getX(), 1, 0, 0);
        GlStateManager.rotated((float) rotation.getY(), 0, 1, 0);
        GlStateManager.rotated((float) rotation.getZ(), 0, 0, 1);
        this.setLightmapDisabled(true);
        GlStateManager.disableLighting();

        TextureHelper.bindBlockAtlas();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        RenderingDrawUtils.renderTexturedCubeCentralColorNormal(buf, percSize,
                uOffset, vOffset, uPart, vPart,
                color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1F,
                rotation);
        tes.draw();

        GlStateManager.enableLighting();
        this.setLightmapDisabled(false);
        GlStateManager.popMatrix();
    }
}
