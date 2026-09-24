/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.lumen;

import hellfirepvp.astralsorcery.client.screen.base.ScalingSizeHandler;
import hellfirepvp.astralsorcery.client.screen.tome.lumen.data.LumenDisplayPositionLoader;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenOverviewSizeHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenOverviewSizeHandler extends ScalingSizeHandler {

    @Nullable
    @Override
    public FloatRectangle buildRequiredRectangle() {
        Collection<IntPoint> points = LumenDisplayPositionLoader.getInstance().getPositions().values()
                .stream().map(position -> new IntPoint(position.x(), position.y())).toList();

        int minX = points.stream().mapToInt(IntPoint::x).min().orElse(0);
        int minY = points.stream().mapToInt(IntPoint::y).min().orElse(0);
        int maxX = points.stream().mapToInt(IntPoint::x).max().orElse(0);
        int maxY = points.stream().mapToInt(IntPoint::y).max().orElse(0);
        return new FloatRectangle(minX,  minY, maxX - minX, maxY - minY);
    }
}
