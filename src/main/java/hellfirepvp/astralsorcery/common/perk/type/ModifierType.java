/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.text.DecimalFormat;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModifierType
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:27
 */
public enum ModifierType {

    ADDITION,
    ADDED_MULTIPLY,
    STACKING_MULTIPLY;

    private static final DecimalFormat DISPLAY_NUMBER_FORMAT = new DecimalFormat("0.##");

    public static ModifierType fromVanillaAttributeOperation(AttributeModifier.Operation op) {
        return MiscUtils.getEnumEntry(ModifierType.class, op.getId());
    }

    public AttributeModifier.Operation getVanillaAttributeOperation() {
        return AttributeModifier.Operation.values()[ordinal()];
    }

    // We don't need the explicit + addition to positive percentages
    public String stringifyValue(float number) {
        if (this == ADDITION) {
            String str = DISPLAY_NUMBER_FORMAT.format(number);
            if (number > 0) {
                str = "+" + str;
            }
            return str;
        } else {
            int nbr = Math.round(number * 100);
            return DISPLAY_NUMBER_FORMAT.format(Math.abs(this == STACKING_MULTIPLY ? 100 - nbr : nbr));
        }
    }

    public String getUnlocalizedModifierName(float number) {
        boolean positive;
        if (this == ADDITION) {
            positive = number > 0; //0 would be kinda... weird as addition/subtraction modifier...
        } else {
            int nbr = Math.round(number * 100);
            positive = this == STACKING_MULTIPLY ? nbr > 100 : nbr > 0;
        }
        return getUnlocalizedModifierName(positive);
    }

    public String getUnlocalizedModifierName(boolean positive) {
        String base = positive ? "perk.modifier.astralsorcery.%s.add" : "perk.modifier.astralsorcery.%s.sub";
        return String.format(base, name().toLowerCase());
    }
}
