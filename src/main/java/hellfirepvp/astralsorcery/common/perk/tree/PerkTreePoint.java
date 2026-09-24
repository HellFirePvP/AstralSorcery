/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderType;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderer;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkTreePoint
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkTreePoint<T extends AbstractPerk<?>> {

    public static final float PERK_RENDER_SIZE = 11F;

    private final FloatPoint offset;
    private final T perk;
    private final float renderScale;

    public PerkTreePoint(FloatPoint offset, T perk) {
        this(offset, perk, 1F);
    }

    protected PerkTreePoint(FloatPoint offset, T perk, float renderScale) {
        this.offset = offset;
        this.perk = perk;
        this.renderScale = renderScale;
    }

    public FloatPoint getOffset() {
        return this.offset;
    }

    public T getPerk() {
        return this.perk;
    }

    protected float getRenderScale() {
        return this.renderScale;
    }

    public float getRenderSize() {
        return PERK_RENDER_SIZE * this.getRenderScale();
    }

    public Supplier<List<PerkRenderType>> getFixedRenderTypes() {
        return PerkRenderType.Types::getDefaultPerkTypes;
    }

    public Supplier<PerkRenderer<?, T>> getRenderer() {
        return () -> MiscUtil.cast(PerkRenderer.DEFAULT);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PerkTreePoint<?> that = (PerkTreePoint<?>) o;
        return Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(offset);
    }
}
