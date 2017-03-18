package hellfirepvp.astralsorcery.common.constellation.starmap;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationMapEffectRegistry
 * Created by HellFirePvP
 * Date: 15.03.2017 / 17:30
 */
public class ConstellationMapEffectRegistry {

    private static Map<IConstellation, MapEffect> effectRegistry = new HashMap<>();

    @Nullable
    public static MapEffect getEffects(IConstellation c) {
        return effectRegistry.get(c);
    }

    public static MapEffect registerMapEffect(IConstellation c, Enchantment e, Potion p) {
        MapEffect me = new MapEffect(e, p);
        effectRegistry.put(c, me);
        return me;
    }

    public static class MapEffect {

        public final Enchantment ench;
        public final Potion potion;
        public int minEnchLevel, maxEnchLevel;
        public int minPotionAmplifier, maxPotionAmplifier;

        public MapEffect(Enchantment ench, Potion potion) {
            this.ench = ench;
            this.potion = potion;
            this.maxEnchLevel = ench.getMaxLevel();
            this.minEnchLevel = ench.getMinLevel();
            this.minPotionAmplifier = 0;
            this.maxPotionAmplifier = 2;
        }

        public MapEffect setMaxEnchLevel(int maxEnchLevel) {
            this.maxEnchLevel = maxEnchLevel;
            return this;
        }

        public MapEffect setMinEnchLevel(int minEnchLevel) {
            this.minEnchLevel = minEnchLevel;
            return this;
        }

        public MapEffect setMaxPotionAmplifier(int maxPotionAmplifier) {
            this.maxPotionAmplifier = maxPotionAmplifier;
            return this;
        }

        public MapEffect setMinPotionAmplifier(int minPotionAmplifier) {
            this.minPotionAmplifier = minPotionAmplifier;
            return this;
        }

    }

}
