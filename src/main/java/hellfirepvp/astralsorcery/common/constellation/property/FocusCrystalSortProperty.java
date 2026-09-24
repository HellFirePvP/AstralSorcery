/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.property;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.focal.FocusCrystalVisualSortFunction;
import hellfirepvp.astralsorcery.common.focal.FocusCrystalVisualSortHelper;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalSortProperty
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalSortProperty extends ConstellationProperty<FocusCrystalSortProperty> {

    public static final Key<FocusCrystalSortProperty> KEY = new Key<>();
    public static final FocusCrystalVisualSortFunction DEFAULT = FocusCrystalVisualSortHelper::sortIntoContinuousPolygon;
    private final FocusCrystalVisualSortFunction function;

    protected FocusCrystalSortProperty(BaseConstellation constellation, FocusCrystalVisualSortFunction function) {
        super(KEY, constellation);
        this.function = function;
    }

    public static Function<BaseConstellation, FocusCrystalSortProperty> of(FocusCrystalVisualSortFunction function) {
        return cst -> new FocusCrystalSortProperty(cst, function);
    }

    public FocusCrystalVisualSortFunction getSortFunction() {
        return this.function;
    }
}
