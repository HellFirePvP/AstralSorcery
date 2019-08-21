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
 * Class: PropertyRitualRange
 * Created by HellFirePvP
 * Date: 02.02.2019 / 21:57
 */
public class PropertyRitualRange extends CrystalProperty {

    public PropertyRitualRange() {
        super(new ResourceLocation(AstralSorcery.MODID, "ritual.range"));
        this.tierRequirement = ProgressionTier.ATTUNEMENT;
    }

    @Override
    public int getMaxTier() {
        return 2;
    }

    @Override
    public double modify(double value, int thisTier, CalculationContext context) {
        if (context.uses(USE_RITUAL_RANGE)) {
            value *= 1.0 + (0.15 * thisTier);
        }
        return value;
    }
}
