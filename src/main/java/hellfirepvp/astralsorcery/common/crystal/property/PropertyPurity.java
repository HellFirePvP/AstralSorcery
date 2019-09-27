/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
 * Class: PropertyPurity
 * Created by HellFirePvP
 * Date: 02.02.2019 / 16:30
 */
public class PropertyPurity extends CrystalProperty {

    public PropertyPurity() {
        super(AstralSorcery.key("purity"));
        this.tierRequirement = ProgressionTier.BASIC_CRAFT;
    }

    @Override
    public int getMaxTier() {
        return 2;
    }

    @Override
    public double modify(double value, int thisTier, CalculationContext context) {
        if (context.uses(USE_LENS_TRANSFER)) {
            value *= 1.0 + ((1.0 / 6.0) * thisTier);
        }
        if (context.uses(USE_COLLECTOR_CRYSTAL) ||
                context.uses(USE_RITUAL_EFFECT) ||
                context.uses(USE_RITUAL_RANGE)) {
            value *= 1.0 + (0.25 * thisTier);
        }
        return value;
    }
}
