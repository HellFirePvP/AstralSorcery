/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tile.PrecisionSingleFluidTank;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderWell
 * Created by HellFirePvP
 * Date: 22.09.2019 / 15:54
 */
public class RenderWell extends CustomTileEntityRenderer<TileWell> {

    @Override
    public void render(TileWell well, double x, double y, double z, float pTicks, int destroyStage) {
        PrecisionSingleFluidTank tank = well.getTank();
        if (!tank.getFluid().isEmpty() && tank.getFluidAmount() > 0) {
            FluidStack contained = tank.getFluid();
            Color fluidColor = new Color(contained.getFluid().getAttributes().getColor(well.getWorld(), well.getPos()));

            Vector3 offset = new Vector3(x, y, z).add(0.5D, 0.32D, 0.5D);
            offset.addY(tank.getPercentageFilled() * 0.6);
            TextureAtlasSprite tas = RenderingUtils.getParticleTexture(contained);

            Tessellator tes = Tessellator.getInstance();
            BufferBuilder buf = tes.getBuffer();

            GlStateManager.disableAlphaTest();
            RenderHelper.disableStandardItemLighting();

            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            RenderingDrawUtils.renderAngleRotatedTexturedRectVB(buf, offset, Vector3.RotAxis.Y_AXIS.clone(), Math.toRadians(45), 0.54,
                    tas.getMinU(), tas.getMinV(), tas.getMaxU() - tas.getMinU(), tas.getMaxV() - tas.getMinV(),
                    fluidColor.getRed() / 255F, fluidColor.getGreen() / 255F, fluidColor.getBlue() / 255F, 1F);
            tes.draw();

            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableAlphaTest();
        }
        ItemStack catalyst = well.getInventory().getStackInSlot(0);
        if (destroyStage < 0 && !catalyst.isEmpty()) {
            RenderingUtils.renderItemAsEntity(catalyst, x + 0.5, y + 0.75, z + 0.5, pTicks, well.getTicksExisted());
        }
    }
}
