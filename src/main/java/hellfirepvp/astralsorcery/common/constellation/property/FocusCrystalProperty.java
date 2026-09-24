/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.property;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.focal.FocusCrystalFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalProperty
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalProperty extends ConstellationProperty<FocusCrystalProperty> {

    public static final Key<FocusCrystalProperty> KEY = new Key<>();
    private final FocusCrystalFunction function;

    protected FocusCrystalProperty(BaseConstellation constellation, FocusCrystalFunction function) {
        super(KEY, constellation);
        this.function = function;
    }

    public static Function<BaseConstellation, FocusCrystalProperty> of(FocusCrystalFunction fn) {
        return cst -> new FocusCrystalProperty(cst, fn);
    }

    public boolean isValidLayer(Level level, BlockPos pos, Set<BlockPos> positions) {
        return this.function.test(level, pos, positions);
    }
}
