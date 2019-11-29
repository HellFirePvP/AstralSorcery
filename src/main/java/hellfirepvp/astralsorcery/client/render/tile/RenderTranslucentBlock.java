/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferBuilderDecorator;
import hellfirepvp.astralsorcery.client.util.image.ColorUtil;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileTranslucentBlock;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderTranslucentBlock
 * Created by HellFirePvP
 * Date: 28.11.2019 / 19:52
 */
public class RenderTranslucentBlock extends CustomFastTileEntityRenderer<TileTranslucentBlock> {

    @Override
    public void renderTileEntityFast(TileTranslucentBlock tile, double x, double y, double z, float pTicks, int destroyStage, BufferBuilder buf) {
        BlockState fakedState = tile.getFakedState();
        Color blendColor = ColorUtils.flareColorFromDye(tile.getOverlayColor());
        int[] color = new int[] { blendColor.getRed(), blendColor.getGreen(), blendColor.getBlue(), 128 };

        BufferBuilderDecorator overlay = BufferBuilderDecorator.decorate(buf);
        overlay.setColorDecorator((r, g, b, a) -> color);

        BlockPos offset = tile.getPos();
        overlay.withTranslation(x - offset.getX(), y - offset.getY(), z - offset.getZ(), () ->
                RenderingUtils.renderSimpleBlockModelCurrentWorld(fakedState, overlay, offset, null, true));
    }
}
