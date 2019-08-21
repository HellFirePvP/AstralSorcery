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
 * Class: PropertyCollectionRate
 * Created by HellFirePvP
 * Date: 02.02.2019 / 21:58
 */
public class PropertyCollectionRate extends CrystalProperty {

    public PropertyCollectionRate() {
        super(new ResourceLocation(AstralSorcery.MODID, "collector.rate"));
        this.tierRequirement = ProgressionTier.ATTUNEMENT;
    }

    @Override
    public double modify(double value, int thisTier, CalculationContext context) {
        if (context.uses(USE_COLLECTOR_CRYSTAL)) {
            value *= 1.0 + (0.15 * thisTier);
        }
        return value;
    }
}
