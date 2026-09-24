/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CombinedEnchantmentModifiers
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CombinedEnchantmentModifiers {

    private final Map<Holder<Enchantment>, Integer> addedToSpecific = new HashMap<>();
    private final Map<Holder<Enchantment>, Integer> addedToExistingSpecific = new HashMap<>();
    private int addedToAll = 0;

    private CombinedEnchantmentModifiers(Collection<EnchantmentModifier> modifiers) {
        modifiers.forEach(this::addModifier);
    }

    public static CombinedEnchantmentModifiers of() {
        return new CombinedEnchantmentModifiers(Collections.emptyList());
    }

    public static CombinedEnchantmentModifiers of(EnchantmentModifier... modifiers) {
        return new CombinedEnchantmentModifiers(Arrays.asList(modifiers));
    }

    public static CombinedEnchantmentModifiers of(Collection<EnchantmentModifier> modifiers) {
        return new CombinedEnchantmentModifiers(modifiers);
    }

    public CombinedEnchantmentModifiers combine(CombinedEnchantmentModifiers other) {
        CombinedEnchantmentModifiers combined = CombinedEnchantmentModifiers.of();
        combined.merge(this);
        combined.merge(other);
        return combined;
    }

    private void merge(CombinedEnchantmentModifiers other) {
        other.addedToSpecific.forEach((ench, mod) ->
                this.addedToSpecific.merge(ench, mod, Integer::sum)
        );
        other.addedToExistingSpecific.forEach((ench, mod) ->
                this.addedToExistingSpecific.merge(ench, mod, Integer::sum)
        );
        this.addedToAll += other.addedToAll;
    }

    private void addModifier(EnchantmentModifier modifier) {
        switch (modifier.getType()) {
            case ADD_TO_SPECIFIC -> {
                modifier.getEnchantment().ifPresent(ench -> {
                    this.addedToSpecific.merge(ench, modifier.getModifier(), Integer::sum);
                });
            }
            case ADD_TO_EXISTING_SPECIFIC -> {
                modifier.getEnchantment().ifPresent(ench -> {
                    this.addedToExistingSpecific.merge(ench, modifier.getModifier(), Integer::sum);
                });
            }
            case ADD_TO_EXISTING_ALL -> {
                this.addedToAll += modifier.getModifier();
            }
        }
    }
    public Map<Holder<Enchantment>, Integer> getAddedToSpecific() {
        return Collections.unmodifiableMap(this.addedToSpecific);
    }

    public Map<Holder<Enchantment>, Integer> getAddedToExistingSpecific() {
        return Collections.unmodifiableMap(this.addedToExistingSpecific);
    }

    public int getAddedToAll() {
        return this.addedToAll;
    }

    public Mutable mutable() {
        return new Mutable(this);
    }

    public static class Mutable {

        private final List<EnchantmentModifier> modifiers = new ArrayList<>();
        private int addToAll;

        private Mutable(CombinedEnchantmentModifiers combined) {
            combined.addedToSpecific.forEach((ench, mod) -> {
                this.modifiers.add(EnchantmentModifier.addLevel(ench, mod));
            });
            combined.addedToExistingSpecific.forEach((ench, mod) -> {
                this.modifiers.add(EnchantmentModifier.addToExistingLevel(ench, mod));
            });
            this.addToAll = combined.addedToAll;
        }

        public List<EnchantmentModifier> getModifiers() {
            return Collections.unmodifiableList(this.modifiers);
        }

        public void addModifier(EnchantmentModifier modifier) {
            switch (modifier.getType()) {
                case ADD_TO_SPECIFIC, ADD_TO_EXISTING_SPECIFIC -> {
                    EnchantmentModifier existingModifier = this.removeModifier(modifier).orElse(null);

                    modifier.getEnchantment().ifPresent(enchantment -> {
                        if (existingModifier != null) {
                            int combinedValue = existingModifier.getModifier() + modifier.getModifier();
                            if (combinedValue > 0) {
                                this.modifiers.add(EnchantmentModifier.addEnchantmentLevel(modifier.getType(), enchantment, combinedValue));
                            }
                        } else if (modifier.getModifier() > 0) {
                            this.modifiers.add(modifier);
                        }
                    });
                }
                case ADD_TO_EXISTING_ALL -> {
                    this.addToAll += modifier.getModifier();
                }
            }
        }

        public Optional<EnchantmentModifier> removeModifier(EnchantmentModifier modifier) {
            return modifier.getEnchantment().flatMap(ench -> {
                return this.removeModifier(ench, modifier.getType());
            });
        }

        public Optional<EnchantmentModifier> removeModifier(Holder<Enchantment> enchantment, EnchantmentModifier.Type type) {
            switch (type) {
                case ADD_TO_SPECIFIC, ADD_TO_EXISTING_SPECIFIC -> {
                    return this.getModifier(enchantment, type)
                            .stream()
                            .filter(this.modifiers::remove)
                            .findFirst();
                }
                case ADD_TO_EXISTING_ALL -> {
                    int prevAll = this.addToAll;
                    this.addToAll = 0;
                    return Optional.of(EnchantmentModifier.addToAll(prevAll));
                }
            }
            return Optional.empty();
        }

        public Optional<EnchantmentModifier> getModifier(Holder<Enchantment> enchantment, EnchantmentModifier.Type type) {
            return this.getModifiers().stream()
                    .filter(mod -> mod.getType() == type)
                    .filter(mod -> mod.getEnchantment().map(e -> e.equals(enchantment)).orElse(false))
                    .findFirst();
        }

        public int getAddToAllEnchantments() {
            return this.addToAll;
        }

        public void setAddToAllEnchantments(int addToAll) {
            this.addToAll = Math.max(0, addToAll);
        }

        public CombinedEnchantmentModifiers build() {
            if (this.addToAll > 0) {
                this.modifiers.add(EnchantmentModifier.addToAll(addToAll));
            }
            return CombinedEnchantmentModifiers.of(this.modifiers);
        }
    }
}
