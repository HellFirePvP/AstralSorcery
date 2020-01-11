/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.property;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crystal.CalculationContext;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import net.minecraft.util.ResourceLocation;

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertyShape
 * Created by HellFirePvP
 * Date: 02.02.2019 / 17:18
 */
public class PropertyShape extends CrystalProperty {

    public PropertyShape() {
        super(AstralSorcery.key("shape"));
        this.tierRequirement = ProgressionTier.BASIC_CRAFT;
    }

    @Override
    public double modify(double value, int thisTier, CalculationContext context) {
        if (context.uses(USE_TOOL_EFFECTIVENESS) ||
                context.uses(USE_COLLECTOR_CRYSTAL) ||
                context.uses(USE_RITUAL_EFFECT)) {
            value *= 1.0 + (0.35 * thisTier);
        }
        if (context.uses(USE_RITUAL_CAPACITY)) {
            value *= 1.0 + (0.4 * thisTier);
        }
        return value;
    }
}
