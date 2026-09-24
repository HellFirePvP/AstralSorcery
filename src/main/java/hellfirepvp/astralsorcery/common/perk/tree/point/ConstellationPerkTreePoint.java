/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.point;

import hellfirepvp.astralsorcery.client.screen.tome.perk.render.ConstellationPerkRenderer;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderType;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderer;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;

import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationPerkTreePoint
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ConstellationPerkTreePoint<T extends AbstractPerk<?>> extends PerkTreePoint<T> {

    public static final int ROOT_SPRITE_SIZE = 45;
    public static final int MINOR_SPRITE_SIZE = 35;

    private final BaseConstellation constellation;
    private final float renderSize;

    public ConstellationPerkTreePoint(FloatPoint offset, T perk, BaseConstellation constellation, int renderSize) {
        super(offset, perk, 1F);
        this.constellation = constellation;
        this.renderSize = renderSize;
    }

    public BaseConstellation getConstellation() {
        return this.constellation;
    }

    @Override
    public float getRenderSize() {
        return this.renderSize * this.getRenderScale();
    }

    @Override
    public Supplier<PerkRenderer<?, T>> getRenderer() {
        return () -> MiscUtil.cast(ConstellationPerkRenderer.CONSTELLATION);
    }

    @Override
    public Supplier<List<PerkRenderType>> getFixedRenderTypes() {
        return PerkRenderType.Types::getMajorPerkTypes;
    }
}
