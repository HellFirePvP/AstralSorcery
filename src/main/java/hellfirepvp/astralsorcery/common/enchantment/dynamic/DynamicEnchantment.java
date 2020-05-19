/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
 * Date: 10.08.2018 / 20:23
 */
public class DynamicEnchantment {

    protected final Type type;
    @Nullable
    protected final Enchantment enchantment;
    protected int levelAddition;

    public DynamicEnchantment(Type type, @Nonnull Enchantment enchantment, int levelAddition) {
        if(!type.hasEnchantmentTag()) {
            throw new IllegalArgumentException("Tried to create amulet enchantment that doesn't requires a std. enchantment together with an enchantment!");
        }
        this.type = type;
        this.enchantment = enchantment;
        this.levelAddition = levelAddition;
    }

    public DynamicEnchantment(Type type, int levelAddition) {
        if(type.hasEnchantmentTag()) {
            throw new IllegalArgumentException("Tried to create amulet enchantment that requires a std. enchantment without an enchantment!");
        }
        this.type = type;
        this.enchantment = null;
        this.levelAddition = levelAddition;
    }

    public Type getType() {
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
        if (this.getType().hasEnchantmentTag()) {
            return new DynamicEnchantment(this.getType(), this.getEnchantment(), level);
        } else {
            return new DynamicEnchantment(this.type, level);
        }
    }

    //The ordering in the enum defines the order of how the types of enchantments are applied/calculated!
    public enum Type {

        ADD_TO_SPECIFIC,
        ADD_TO_EXISTING_SPECIFIC,
        ADD_TO_EXISTING_ALL(false);

        private final boolean hasEnchantmentTag;

        Type() {
            this(true);
        }

        Type(boolean hasEnchantmentTag) {
            this.hasEnchantmentTag = hasEnchantmentTag;
        }

        public boolean hasEnchantmentTag() {
            return hasEnchantmentTag;
        }

    }

}
