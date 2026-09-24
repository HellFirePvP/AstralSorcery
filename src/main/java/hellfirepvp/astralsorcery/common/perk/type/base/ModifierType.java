/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.Util;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ModifierType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum ModifierType {

    ADDITION,
    ADDED_MULTIPLY,
    STACKING_MULTIPLY;

    private static final DecimalFormat DISPLAY_NUMBER_FORMAT = new DecimalFormat("0.##");
    private final String nameIdPositive, nameIdNegative;

    ModifierType() {
        String namePart = String.format("perk.modifier_type.%s", this.name().toLowerCase(Locale.ROOT));
        this.nameIdPositive = Util.makeDescriptionId(namePart, AstralSorcery.key("positive"));
        this.nameIdNegative = Util.makeDescriptionId(namePart, AstralSorcery.key("negative"));
    }

    public static ModifierType fromVanillaAttributeOperation(AttributeModifier.Operation op) {
        return MiscUtil.getEnumEntry(ModifierType.class, op.id());
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

    public String getModifierOperationNameId(float number) {
        boolean positive;
        if (this == ADDITION) {
            positive = number >= 0; //0 would be kinda... weird as addition/subtraction modifier...
        } else {
            int nbr = Math.round(number * 100);
            positive = this == STACKING_MULTIPLY ? nbr > 100 : nbr > 0;
        }
        return positive ? this.nameIdPositive : this.nameIdNegative;
    }

    public boolean isNeutralVanillaValue(float number) {
        return Math.abs(number) <= 1E-4;
    }
}
