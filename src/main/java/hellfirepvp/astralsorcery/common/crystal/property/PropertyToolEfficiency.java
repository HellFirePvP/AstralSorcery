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

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.USE_TOOL_EFFECTIVENESS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertyToolEfficiency
 * Created by HellFirePvP
 * Date: 02.02.2019 / 21:54
 */
public class PropertyToolEfficiency extends CrystalProperty {

    public PropertyToolEfficiency() {
        super(AstralSorcery.key("tool.efficiency"));
        this.setRequiredResearch(ResearchProgression.BASIC_CRAFT);

        this.addUsage(ctx -> ctx.uses(USE_TOOL_EFFECTIVENESS));
        this.addModifier((value, originalValue, propertyLevel, context) ->
                context.withUse(USE_TOOL_EFFECTIVENESS, value, () -> value * (1.0 + (0.3 * Math.min(propertyLevel, 4)))));
    }
}
