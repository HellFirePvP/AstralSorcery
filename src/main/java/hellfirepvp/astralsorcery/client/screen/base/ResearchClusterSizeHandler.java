/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchClusterSizeHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchClusterSizeHandler extends ScalingSizeHandler {

    @Override
    @Nullable
    public FloatRectangle buildRequiredRectangle() {
        PlayerProgress progress = ResearchManager.getClientProgress();
        List<IntRectangle> tierBoxes = Stream.of(ResearchTier.values())
                .filter(tier -> progress.getTierReached().isThisLaterOrEqual(tier))
                .map(ResearchTier::getCloudArea)
                .toList();

        int minX = tierBoxes.stream().mapToInt(IntRectangle::x).min().orElse(0);
        int minY = tierBoxes.stream().mapToInt(IntRectangle::y).min().orElse(0);
        int maxX = tierBoxes.stream().mapToInt(IntRectangle::maxX).max().orElse(0);
        int maxY = tierBoxes.stream().mapToInt(IntRectangle::maxY).max().orElse(0);

        return new FloatRectangle(minX, minY, maxX - minX, maxY - minY);
    }
}
