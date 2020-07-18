/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedRockCrystal;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderRitualPedestal
 * Created by HellFirePvP
 * Date: 28.05.2020 / 21:33
 */
public class RenderRitualPedestal extends CustomTileEntityRenderer<TileRitualPedestal> {

    public RenderRitualPedestal(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileRitualPedestal tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = tile.getCurrentCrystal();
        if (stack.isEmpty()) {
            return;
        }

        ItemStack display = stack;
        if (display.getItem() instanceof ItemAttunedRockCrystal) {
            display = new ItemStack(BlocksAS.ROCK_COLLECTOR_CRYSTAL);
        }
        if (display.getItem() instanceof ItemAttunedCelestialCrystal) {
            display = new ItemStack(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL);
        }

        renderStack.push();
        renderStack.translate(0.5F, 0.9F, 0.5F);
        renderStack.scale(2F, 2F, 2F);
        RenderingUtils.renderTranslucentItemStackModel(display, renderStack, Color.WHITE, Blending.DEFAULT, 255);
        renderStack.pop();

        IWeakConstellation ritualConstellation = tile.getRitualConstellation();
        if (ritualConstellation != null) {
            long seed = RenderingUtils.getPositionSeed(tile.getPos());
            int scales = tile.isWorking() ? 24 : 12;
            int count = tile.isWorking() ? 16 : 12;

            renderStack.push();
            renderStack.translate(0.5F, 1.2F, 0.5F);

            RenderingDrawUtils.renderLightRayFan(renderStack, renderTypeBuffer, ritualConstellation.getConstellationColor(), seed, scales, scales, count);

            renderStack.pop();
        }
    }
}
