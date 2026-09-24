/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.point;

import hellfirepvp.astralsorcery.client.screen.tome.perk.render.MajorPerkRenderer;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderType;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderer;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;

import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MajorPerkTreePoint
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MajorPerkTreePoint<T extends AbstractPerk<?>> extends PerkTreePoint<T> {

    public MajorPerkTreePoint(FloatPoint offset, T perk) {
        this(offset, perk, 1F);
    }

    protected MajorPerkTreePoint(FloatPoint offset, T perk, float renderScale) {
        super(offset, perk, renderScale * 1.4F);
    }

    @Override
    public Supplier<List<PerkRenderType>> getFixedRenderTypes() {
        return PerkRenderType.Types::getMajorPerkTypes;
    }

    @Override
    public Supplier<PerkRenderer<?, T>> getRenderer() {
        return () -> MiscUtil.cast(MajorPerkRenderer.MAJOR);
    }
}
