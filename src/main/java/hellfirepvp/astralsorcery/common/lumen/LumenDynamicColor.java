/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen;

import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenDynamicColor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenDynamicColor extends Lumen {

    private final Function<Long, ColorWrapper> colorSupplier;

    public LumenDynamicColor(Function<Long, ColorWrapper> colorSupplier) {
        super(ColorWrapper.WHITE);
        this.colorSupplier = colorSupplier;
    }

    @Override
    public ColorWrapper getColor(long tick) {
        return this.colorSupplier.apply(tick);
    }
}
