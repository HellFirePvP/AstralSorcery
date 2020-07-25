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
        this.setRequiredResearch(ResearchProgression.BASIC_CRAFT);

        this.addUsage(ctx -> ctx.uses(USE_RITUAL_CAPACITY));
        this.addModifier((value, originalValue, propertyLevel, context) ->
                context.withUse(USE_RITUAL_CAPACITY, value, () -> value * (1.0 + (0.4 * propertyLevel))));
        this.addUsage(ctx -> ctx.uses(USE_TOOL_EFFECTIVENESS));
        this.addUsage(ctx -> ctx.uses(USE_COLLECTOR_CRYSTAL));
        this.addUsage(ctx -> ctx.uses(USE_RITUAL_EFFECT));
        this.addModifier((value, originalValue, propertyLevel, context) -> {
            if (context.uses(USE_TOOL_EFFECTIVENESS)) {
                return value * (1.0 + (0.2 * Math.min(propertyLevel, 4)));
            }
            if (context.uses(USE_COLLECTOR_CRYSTAL) ||
                    context.uses(USE_RITUAL_EFFECT)) {
                return value * (1.0 + (0.25 * propertyLevel));
            }
            return value;
        });
    }
}
