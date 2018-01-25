/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.starmap;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationMapEffectRegistry
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:38
 */
public class ConstellationMapEffectRegistry {

    private static Map<IConstellation, MapEffect> effectRegistry = new HashMap<>();

    @Nullable
    static MapEffect getEffects(IConstellation c) {
        return effectRegistry.get(c);
    }

    public static MapEffect registerMapEffect(IConstellation c, Collection<EnchantmentMapEffect> enchantmentEffects, Collection<PotionMapEffect> potionEffects) {
        MapEffect me = new MapEffect(enchantmentEffects, potionEffects);
        effectRegistry.put(c, me);
        return me;
    }

    public static class MapEffect {

        public final Collection<EnchantmentMapEffect> enchantmentEffects;
        public final Collection<PotionMapEffect> potionEffects;

        private MapEffect(Collection<EnchantmentMapEffect> enchantments, Collection<PotionMapEffect> potions) {
            this.enchantmentEffects = Collections.unmodifiableCollection(enchantments);
            this.potionEffects = Collections.unmodifiableCollection(potions);
        }

    }

    public static class PotionMapEffect {

        public final Potion potion;
        public final int minPotionAmplifier, maxPotionAmplifier;

        public PotionMapEffect(Potion potion) {
            this.potion = potion;
            this.minPotionAmplifier = 0;
            this.maxPotionAmplifier = 2;
        }

        public PotionMapEffect(Potion potion, int min, int max) {
            this.potion = potion;
            this.minPotionAmplifier = min;
            this.maxPotionAmplifier = max;
        }

    }

    public static class EnchantmentMapEffect {

        public final Enchantment ench;
        public final int minEnchLevel, maxEnchLevel;
        public boolean ignoreCompaibility = false;

        public EnchantmentMapEffect(Enchantment ench) {
            this.ench = ench;
            this.maxEnchLevel = ench.getMaxLevel();
            this.minEnchLevel = ench.getMinLevel();
        }

        public EnchantmentMapEffect(Enchantment ench, int min, int max) {
            this.ench = ench;
            this.maxEnchLevel = max;
            this.minEnchLevel = min;
        }

        public EnchantmentMapEffect setIgnoreCompaibility() {
            this.ignoreCompaibility = true;
            return this;
        }
    }

}
