/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedRockCrystal;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
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

    @Override
    public void render(TileRitualPedestal pedestal, double x, double y, double z, float pTicks, int destroyStage) {
        ItemStack stack = pedestal.getCurrentCrystal();
        if (!stack.isEmpty()) {
            ItemStack display = stack;
            if (display.getItem() instanceof ItemAttunedRockCrystal) {
                display = new ItemStack(BlocksAS.ROCK_COLLECTOR_CRYSTAL);
            }
            if (display.getItem() instanceof ItemAttunedCelestialCrystal) {
                display = new ItemStack(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translated(x + 0.5, y + 1.2, z + 0.5);
            GlStateManager.scaled(0.6F, 0.6F, 0.6F);
            RenderingUtils.renderTranslucentItemStackModel(display, Color.WHITE, Blending.DEFAULT, 1F);
            GlStateManager.popMatrix();

            IWeakConstellation ritualConstellation = pedestal.getRitualConstellation();
            if (ritualConstellation != null) {
                long seed = RenderingUtils.getPositionSeed(pedestal.getPos());
                int scales = pedestal.isWorking() ? 24 : 12;
                int count = pedestal.isWorking() ? 16 : 12;

                GlStateManager.enableBlend();
                RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 1.2, z + 0.5, ritualConstellation.getConstellationColor(), seed, scales, scales, count);
                GlStateManager.disableBlend();
            }
        }
    }
}
