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
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tile.PrecisionSingleFluidTank;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderWell
 * Created by HellFirePvP
 * Date: 22.09.2019 / 15:54
 */
public class RenderWell extends CustomTileEntityRenderer<TileWell> {

    public RenderWell(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileWell tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        PrecisionSingleFluidTank tank = tile.getTank();
        if (!tank.getFluid().isEmpty() && tank.getFluidAmount() > 0) {
            FluidStack contained = tank.getFluid();
            TextureAtlasSprite tas = RenderingUtils.getParticleTexture(contained);
            Color fluidColor = new Color(contained.getFluid().getAttributes().getColor(tile.getWorld(), tile.getPos()));
            IVertexBuilder buf = renderTypeBuffer.getBuffer(RenderTypesAS.TER_WELL_LIQUID);

            Vector3 offset = new Vector3(0.5D, 0.32D, 0.5D).addY(tank.getPercentageFilled() * 0.6);

            RenderingDrawUtils.renderAngleRotatedTexturedRectVB(buf, renderStack, offset, Vector3.RotAxis.Y_AXIS, (float) Math.toRadians(45F), 0.54F,
                    tas.getMinU(), tas.getMinV(), tas.getMaxU() - tas.getMinU(), tas.getMaxV() - tas.getMinV(),
                    fluidColor.getRed(), fluidColor.getGreen(), fluidColor.getBlue(), 255);
        }

        ItemStack catalyst = tile.getInventory().getStackInSlot(0);
        if (!catalyst.isEmpty()) {
            RenderingUtils.renderItemAsEntity(catalyst, renderStack, renderTypeBuffer, 0.5F, 0.75F, 0.5F, combinedLight, pTicks, tile.getTicksExisted());
        }
    }
}
