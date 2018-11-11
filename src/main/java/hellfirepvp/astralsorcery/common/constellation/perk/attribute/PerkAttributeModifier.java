/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkConverter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeModifier
 * Created by HellFirePvP
 * Date: 08.07.2018 / 11:08
 */
public class PerkAttributeModifier {

    private static int counter = 0;

    private int id;
    private float value;
    private final Mode mode;
    private final String attributeType;

    //Cannot be converted to anything else.
    private boolean absolute = false;

    private Map<PerkConverter, Table<String, Mode, PerkAttributeModifier>> cachedConverters = new HashMap<>();

    public PerkAttributeModifier(String type, Mode mode, float value) {
        this.id = counter;
        counter++;
        this.attributeType = type;
        this.mode = mode;
        this.value = value;
    }

    private void setAbsolute() {
        this.absolute = true;
    }

    void multiplyValue(double multiplier) {
        this.value *= multiplier;
    }

    /**
     * Use this method for PerkConverters returning a new PerkAttributeModifier!
     */
    @Nonnull
    public final PerkAttributeModifier convertModifier(String attributeType, Mode mode, float value) {
        if (absolute) {
            return this;
        }
        PerkAttributeModifier mod;
        PerkAttributeType type = AttributeTypeRegistry.getType(attributeType);
        if (type != null) {
            mod = type.createModifier(value, mode);
        } else {
            mod = new PerkAttributeModifier(attributeType, mode, value);
        }
        mod.id = this.id;
        return mod;
    }

    /**
     * Use this method for creating extra Modifiers depending on a given modifier.
     */
    @Nullable
    public final PerkAttributeModifier gainAsExtraModifier(PerkConverter converter, String attributeType, Mode mode, float value) {
        Table<String, Mode, PerkAttributeModifier> cachedModifiers = cachedConverters.computeIfAbsent(converter, (c) -> HashBasedTable.create());
        PerkAttributeModifier modifier;
        PerkAttributeType attrType;
        if ((modifier = cachedModifiers.get(attributeType, mode)) == null &&
                (attrType = AttributeTypeRegistry.getType(attributeType)) != null) {
            modifier = attrType.createModifier(value, mode);
            modifier.setAbsolute();
            cachedModifiers.put(attributeType, mode, modifier);
        }
        return modifier;
    }

    public float getValue() {
        return value;
    }

    public float getValueForDisplay() {
        return value;
    }

    public Mode getMode() {
        return mode;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public String getUnlocalizedAttributeName() {
        return String.format("perk.attribute.%s.name", getAttributeType());
    }

    @SideOnly(Side.CLIENT)
    public boolean hasDisplayString() {
        return I18n.hasKey(getUnlocalizedAttributeName());
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedAttributeValue() {
        return getMode().stringifyValue(getValueForDisplay());
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedModifierName() {
        return I18n.format(getMode().getUnlocalizedModifierName(getValueForDisplay()));
    }

    @SideOnly(Side.CLIENT)
    public String getAttributeDisplayFormat() {
        return I18n.format("perk.modifier.format");
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public String getLocalizedDisplayString() {
        if (!hasDisplayString()) {
            return null;
        }
        return String.format(getAttributeDisplayFormat(),
                getLocalizedAttributeValue(),
                getLocalizedModifierName(),
                I18n.format(getUnlocalizedAttributeName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkAttributeModifier that = (PerkAttributeModifier) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    public static enum Mode {

        ADDITION,
        ADDED_MULTIPLY,
        STACKING_MULTIPLY;

        public static Mode fromVanillaAttributeOperation(int op) {
            return values()[MathHelper.clamp(op, 0, values().length - 1)];
        }

        public int getVanillaAttributeOperation() {
            return ordinal();
        }

        // We don't need the explicit + addition to positive percentages
        public String stringifyValue(float number) {
            if (this == ADDITION) {
                String str = Integer.toString(Math.round(number));
                if (number > 0) {
                    str = "+" + str;
                }
                return str;
            } else {
                int nbr = Math.round(number * 100);
                return Integer.toString(Math.abs(this == STACKING_MULTIPLY ? 100 - nbr : nbr));
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
            String base = positive ? "perk.modifier.%s.add" : "perk.modifier.%s.sub";
            return String.format(base, name().toLowerCase());
        }

    }

}
