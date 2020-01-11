/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.modifier;

import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierCritChance
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:18
 */
public class AttributeModifierCritChance extends PerkAttributeModifier {

    public AttributeModifierCritChance(PerkAttributeType type, ModifierType mode, float value) {
        super(type, mode, value);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getLocalizedAttributeValue() {
        String str = super.getLocalizedAttributeValue();
        if (getMode() == ModifierType.ADDITION) {
            str += "%";
        }
        return str;
    }
}
