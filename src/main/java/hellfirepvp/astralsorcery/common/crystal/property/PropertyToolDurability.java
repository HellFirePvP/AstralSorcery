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

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.USE_TOOL_DURABILITY;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertyToolDurability
 * Created by HellFirePvP
 * Date: 02.02.2019 / 21:53
 */
public class PropertyToolDurability extends CrystalProperty {

    public PropertyToolDurability() {
        super(AstralSorcery.key("tool.durability"));
        this.setRequiredResearch(ResearchProgression.BASIC_CRAFT);

        this.addUsage(ctx -> ctx.uses(USE_TOOL_DURABILITY));
        this.addModifier((value, originalValue, propertyLevel, context) ->
                context.withUse(USE_TOOL_DURABILITY, value, () -> value * (1.0 + (0.4 * propertyLevel))));
    }
}
