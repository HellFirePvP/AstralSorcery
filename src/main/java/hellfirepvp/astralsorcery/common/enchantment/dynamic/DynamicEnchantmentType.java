/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.dynamic;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicEnchantmentType
 * Created by HellFirePvP
 * Date: 11.08.2019 / 19:46
 */
public enum DynamicEnchantmentType {

    ADD_TO_SPECIFIC,
    ADD_TO_EXISTING_SPECIFIC,
    ADD_TO_EXISTING_ALL(false);

    private final boolean specific;

    DynamicEnchantmentType() {
        this(true);
    }

    DynamicEnchantmentType(boolean specific) {
        this.specific = specific;
    }

    public boolean isEnchantmentSpecific() {
        return specific;
    }

    public String getDisplayName() {
        return String.format("astralsorcery.amulet.enchantment.%s.name", this.name().toLowerCase());
    }

}
