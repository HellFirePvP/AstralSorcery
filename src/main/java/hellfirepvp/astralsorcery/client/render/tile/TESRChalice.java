/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 22:09
 */
public class TESRChalice extends TileEntitySpecialRenderer<TileChalice> {

    @Override
    public void render(TileChalice te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Fluid filled = te.getHeldFluid();
        if(filled != null && te.getFluidAmount() > 0) {
            TileChalice.DrawSize size = te.getDrawSize();
            FluidStack fs = te.getTank().getFluid();
            if(fs != null) {
                TextureAtlasSprite tas = RenderingUtils.tryGetFlowingTextureOfFluidStack(fs);
                Vector3 rot = getInterpolatedRotation(te, partialTicks);

                double ulength = tas.getMaxU() - tas.getMinU();
                double vlength = tas.getMaxV() - tas.getMinV();

                double uPart = ulength * (size.partTexture / 2);
                double vPart = vlength * (size.partTexture / 2);

                double uOffset = tas.getMinU() + ulength / 2D - uPart / 2D;
                double vOffset = tas.getMinV() + vlength / 2D - vPart / 2D;

                if(size == TileChalice.DrawSize.SMALL) {
                    uOffset = tas.getMinU();
                    vOffset = tas.getMinV();
                }

                GlStateManager.pushMatrix();
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.translate(x + 0.5, y + 1.4, z + 0.5);
                GlStateManager.rotate((float) rot.getX(), 1, 0, 0);
                GlStateManager.rotate((float) rot.getY(), 0, 1, 0);
                GlStateManager.rotate((float) rot.getZ(), 0, 0, 1);
                GlStateManager.disableCull();
                GlStateManager.scale(0.9, 0.9, 0.9);
                TextureHelper.setActiveTextureToAtlasSprite();
                RenderHelper.enableGUIStandardItemLighting();

                RenderingUtils.renderTexturedCubeCentral(new Vector3(0, 0, 0), size.partTexture, uOffset, vOffset, uPart, vPart);

                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        }
    }

    private Vector3 getInterpolatedRotation(TileChalice tc, float percent) {
        return new Vector3(
                RenderingUtils.interpolate(tc.prevRotationDegreeAxis.getX(), tc.rotationDegreeAxis.getX(), percent),
                RenderingUtils.interpolate(tc.prevRotationDegreeAxis.getY(), tc.rotationDegreeAxis.getY(), percent),
                RenderingUtils.interpolate(tc.prevRotationDegreeAxis.getZ(), tc.rotationDegreeAxis.getZ(), percent));
    }

}
