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
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeModifier
 * Created by HellFirePvP
 * Date: 08.07.2018 / 11:08
 */
public class PerkAttributeModifier {

    private static int counter = 0;

    private final int id;
    private float value;
    private final Mode mode;
    private final String attributeType;

    private Map<PerkConverter, Table<String, Mode, PerkAttributeModifier>> cachedConverters = new HashMap<>();

    private PerkAttributeModifier(int id, String type, Mode mode, float value) {
        this.id = id;
        this.attributeType = type;
        this.value = value;
        this.mode = mode;
    }

    public PerkAttributeModifier(String type, Mode mode, float value) {
        this.id = counter;
        counter++;
        this.attributeType = type;
        this.mode = mode;
        this.value = value;
    }

    /**
     * Use this method for PerkConverters returning a new PerkAttributeModifier!
     */
    public PerkAttributeModifier convertModifier(String attributeType, Mode mode, float value) {
        return new PerkAttributeModifier(this.id, attributeType, mode, value);
    }

    /**
     * Use this method for creating extra Modifiers depending on a given modifier.
     */
    public PerkAttributeModifier gainAsExtraModifier(PerkConverter converter, String attributeType, Mode mode, float value) {
        Table<String, Mode, PerkAttributeModifier> cachedModifiers = cachedConverters.computeIfAbsent(converter, (c) -> HashBasedTable.create());
        PerkAttributeModifier modifier;
        if ((modifier = cachedModifiers.get(attributeType, mode)) == null) {
            modifier = new PerkAttributeModifier(attributeType, mode, value);
            cachedModifiers.put(attributeType, mode, modifier);
        }
        return modifier;
    }

    public float getValue() {
        return value;
    }

    public Mode getMode() {
        return mode;
    }

    public String getAttributeType() {
        return attributeType;
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

    }

}
