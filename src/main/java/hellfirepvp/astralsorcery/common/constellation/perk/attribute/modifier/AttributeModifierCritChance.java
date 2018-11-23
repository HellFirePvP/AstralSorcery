/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.modifier;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierCritChance
 * Created by HellFirePvP
 * Date: 23.11.2018 / 21:26
 */
public class AttributeModifierCritChance extends PerkAttributeModifier {

    public AttributeModifierCritChance(String type, Mode mode, float value) {
        super(type, mode, value);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedAttributeValue() {
        String str = super.getLocalizedAttributeValue();
        if (getMode() == Mode.ADDITION) {
            str += "%";
        }
        return str;
    }
}
