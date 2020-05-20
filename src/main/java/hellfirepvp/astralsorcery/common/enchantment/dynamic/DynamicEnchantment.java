/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.dynamic;

import net.minecraft.enchantment.Enchantment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicEnchantment
 * Created by HellFirePvP
 * Date: 11.08.2019 / 19:45
 */
public class DynamicEnchantment {

    protected final DynamicEnchantmentType type;
    @Nullable
    protected final Enchantment enchantment;
    protected int levelAddition;

    public DynamicEnchantment(DynamicEnchantmentType type, @Nonnull Enchantment enchantment, int levelAddition) {
        if (!type.isEnchantmentSpecific()) {
            throw new IllegalArgumentException("Tried to create dynamic enchantment with a type that doesn\'t require an enchantment, but supplied an enchantment!");
        }
        this.type = type;
        this.enchantment = enchantment;
        this.levelAddition = levelAddition;
    }

    public DynamicEnchantment(DynamicEnchantmentType type, int levelAddition) {
        if (type.isEnchantmentSpecific()) {
            throw new IllegalArgumentException("Tried to create dynamic enchantment with a type that requires an enchantment without specifying such an enchantment!");
        }
        this.type = type;
        this.enchantment = null;
        this.levelAddition = levelAddition;
    }

    public DynamicEnchantmentType getType() {
        return type;
    }

    @Nullable
    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getLevelAddition() {
        return levelAddition;
    }

    public void setLevelAddition(int levelAddition) {
        this.levelAddition = levelAddition;
    }

    @Nonnull
    public DynamicEnchantment copy() {
        return this.copy(this.getLevelAddition());
    }

    @Nonnull
    public DynamicEnchantment copy(int level) {
        if (this.getType().isEnchantmentSpecific()) {
            return new DynamicEnchantment(this.getType(), this.getEnchantment(), level);
        } else {
            return new DynamicEnchantment(this.type, level);
        }
    }
}
