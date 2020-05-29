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
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderCollectorCrystal
 * Created by HellFirePvP
 * Date: 28.05.2020 / 18:37
 */
public class RenderCollectorCrystal extends CustomTileEntityRenderer<TileCollectorCrystal> {

    @Override
    public void render(TileCollectorCrystal tile, double x, double y, double z, float pTicks, int destroyStage) {
        if (!tile.doesSeeSky()) {
            return;
        }
        GlStateManager.enableBlend();

        Color c = tile.getCollectorType().getDisplayColor();

        long seed = RenderingUtils.getPositionSeed(tile.getPos());
        RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 0.5, z + 0.5, c, seed, 24, 24, 12);

        seed ^= 0x54FF129A4B11C382L;
        if (tile.isEnhanced()) {
            c = tile.getAttunedConstellation().getConstellationColor();
        }
        RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 0.5, z + 0.5, c, seed, 24, 24, 12);

        GlStateManager.disableBlend();
    }
}
