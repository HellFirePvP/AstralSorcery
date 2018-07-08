/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import net.minecraft.util.math.MathHelper;

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

    public PerkAttributeModifier(Mode mode, float value) {
        this.id = counter;
        counter++;
        this.mode = mode;
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public Mode getMode() {
        return mode;
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
