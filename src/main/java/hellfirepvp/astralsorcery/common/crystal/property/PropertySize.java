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
 * Class: PropertySize
 * Created by HellFirePvP
 * Date: 02.02.2019 / 15:50
 */
public class PropertySize extends CrystalProperty {

    public PropertySize() {
        super(new ResourceLocation(AstralSorcery.MODID, "size"));
        this.tierRequirement = ProgressionTier.BASIC_CRAFT;
    }

    @Override
    public double modify(double value, int thisTier, CalculationContext context) {
        if (context.uses(USE_COLLECTOR_CRYSTAL)) {
            value *= 1.0 + (0.3 * thisTier);
        }
        if (context.uses(USE_TOOL_DURABILITY)) {
            value *= 1.0 + (0.8 * thisTier);
        }
        return value;
    }

}
