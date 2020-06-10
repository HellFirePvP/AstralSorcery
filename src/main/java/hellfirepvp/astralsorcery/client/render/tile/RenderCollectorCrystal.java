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
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import net.minecraft.client.renderer.IRenderTypeBuffer;

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
    public void render(TileCollectorCrystal tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        if (!tile.doesSeeSky()) {
            return;
        }
        IVertexBuilder lightRayBuf = renderTypeBuffer.getBuffer(RenderTypesAS.EFFECT_LIGHTRAY_FAN);
        Color color = tile.getCollectorType().getDisplayColor();
        long seed = RenderingUtils.getPositionSeed(tile.getPos());

        renderStack.push();
        renderStack.translate(0.5F, 0.5F, 0.5F);

        RenderingDrawUtils.renderLightRayFan(renderStack, lightRayBuf, color, seed, 24, 24, 12);

        seed ^= 0x54FF129A4B11C382L;
        if (tile.isEnhanced()) {
            color = tile.getAttunedConstellation().getConstellationColor();
        }

        RenderingDrawUtils.renderLightRayFan(renderStack, lightRayBuf, color, seed, 24, 24, 12);
        renderStack.pop();
    }
}
