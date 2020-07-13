/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.property;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.USE_COLLECTOR_CRYSTAL;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertyCollectionRate
 * Created by HellFirePvP
 * Date: 02.02.2019 / 21:58
 */
public class PropertyCollectionRate extends CrystalProperty {

    public PropertyCollectionRate() {
        super(AstralSorcery.key("collector.rate"));
        this.setRequiredResearch(ResearchProgression.ATTUNEMENT);

        this.addUsage(ctx -> ctx.uses(USE_COLLECTOR_CRYSTAL));
        this.addModifier((value, originalValue, propertyLevel, context) ->
                context.withUse(USE_COLLECTOR_CRYSTAL, value, () -> value * (1.0 + (0.15 * propertyLevel))));
    }
}
