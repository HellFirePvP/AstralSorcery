/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalPropertyModifierFunction
 * Created by HellFirePvP
 * Date: 06.05.2020 / 20:30
 */
public interface CrystalPropertyModifierFunction {

    public double modify(double value, double originalValue, int propertyLevel, CalculationContext context);

}
