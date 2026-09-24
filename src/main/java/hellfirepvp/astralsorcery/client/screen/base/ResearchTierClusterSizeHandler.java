/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchTierClusterSizeHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchTierClusterSizeHandler extends ScalingSizeHandler {

    private final ResearchTier tier;

    public ResearchTierClusterSizeHandler(ResearchTier tier) {
        this.tier = tier;
        this.setMaxScale(1.2F);
        this.setMinScale(0.1F);
        this.setScaleSpeed(0.9F / 20F);
    }

    @Override
    @Nullable
    public FloatRectangle buildRequiredRectangle() {
        List<FloatRectangle> tierBoxes = ResearchNodeLoader.getInstance().getNodes().stream()
                .filter(node -> node.getTier() == this.tier)
                .map(node -> new FloatRectangle(node.getPosX(), node.getPosY(), 1, 1))
                .toList();

        float minX = tierBoxes.stream().map(FloatRectangle::x).min(Float::compareTo).orElse(0F);
        float minY = tierBoxes.stream().map(FloatRectangle::y).min(Float::compareTo).orElse(0F);
        float maxX = tierBoxes.stream().map(FloatRectangle::maxX).max(Float::compareTo).orElse(0F);
        float maxY = tierBoxes.stream().map(FloatRectangle::maxY).max(Float::compareTo).orElse(0F);

        return new FloatRectangle(minX, minY, maxX - minX, maxY - minY);
    }
}
