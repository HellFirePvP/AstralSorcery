/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.perk;

import hellfirepvp.astralsorcery.client.screen.base.ScalingSizeHandler;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkTreeSizeHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkTreeSizeHandler extends ScalingSizeHandler {

    public PerkTreeSizeHandler() {
        this.setSpaceBetweenNodes(10F);
        this.setWidthHeightNodes(10F);
    }

    @Nullable
    @Override
    public FloatRectangle buildRequiredRectangle() {
        Collection<FloatPoint> points = PerkTree.getInstance().getPerkPoints(LogicalSide.CLIENT)
                .stream().map(PerkTreePoint::getOffset).toList();

        double minX = points.stream().mapToDouble(FloatPoint::x).min().orElse(0);
        double minY = points.stream().mapToDouble(FloatPoint::y).min().orElse(0);
        double maxX = points.stream().mapToDouble(FloatPoint::x).max().orElse(0);
        double maxY = points.stream().mapToDouble(FloatPoint::y).max().orElse(0);
        return new FloatRectangle((float) minX, (float) minY, (float) (maxX - minX), (float) (maxY - minY));
    }
}
