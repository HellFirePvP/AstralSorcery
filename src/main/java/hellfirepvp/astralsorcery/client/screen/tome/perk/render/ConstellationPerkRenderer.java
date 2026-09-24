/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.perk.render;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderConstellationUtil;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.point.ConstellationPerkTreePoint;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationStatus;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.gui.GuiGraphics;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationPerkRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ConstellationPerkRenderer<T extends ConstellationPerkTreePoint<A>, A extends AbstractPerk<?>> extends MajorPerkRenderer<T, A> {

    public static final ConstellationPerkRenderer<?, ?> CONSTELLATION = new ConstellationPerkRenderer<>();

    protected ConstellationPerkRenderer() {}

    @Override
    protected float getStarSize(T point, float renderScale) {
        return super.getStarSize(point, renderScale) * 0.33F;
    }

    @Override
    public boolean needsImmediateRender(PerkTreePoint<?> point) {
        return point instanceof ConstellationPerkTreePoint<?> cstPoint && cstPoint.getConstellation() != null;
    }

    @Override
    public void renderImmediate(GuiGraphics graphics, T point, PerkAllocationStatus status, float pTicks, float x, float y, float scale) {
        super.renderImmediate(graphics, point, status, pTicks, x, y, scale);

        PlayerProgress progress = ResearchManager.getClientProgress();
        if (!progress.hasDiscoveredConstellation(point.getConstellation())) {
            return;
        }
        ColorWrapper color = ColorWrapper.WHITE;
        if (status.isAllocated()) {
            color = point.getConstellation().getConstellationColor();
        }

        float size = point.getRenderSize() * scale * 0.7F;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderConstellationUtil.drawConstellationUI(color, point.getConstellation(), graphics.pose(),
                x - size, y - size, size * 2, size * 2,
                2.5F * scale, () -> 1F, true, false);
        RenderSystem.disableBlend();
    }
}
