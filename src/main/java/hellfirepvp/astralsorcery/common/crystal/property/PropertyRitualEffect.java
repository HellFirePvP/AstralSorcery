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

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.USE_RITUAL_CAPACITY;
import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.USE_RITUAL_EFFECT;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertyRitualEffect
 * Created by HellFirePvP
 * Date: 02.02.2019 / 21:56
 */
public class PropertyRitualEffect extends CrystalProperty {

    public PropertyRitualEffect() {
        super(AstralSorcery.key("ritual.effect"));
        this.setRequiredResearch(ResearchProgression.ATTUNEMENT);

        this.addUsage(ctx -> ctx.uses(USE_RITUAL_EFFECT));
        this.addUsage(ctx -> ctx.uses(USE_RITUAL_CAPACITY));
        this.addModifier((value, originalValue, propertyLevel, context) -> {
            if (context.uses(USE_RITUAL_EFFECT) || context.uses(USE_RITUAL_CAPACITY)) {
                return value * (1.0 + (0.25 * propertyLevel));
            }
            return value;
        });
    }
}
