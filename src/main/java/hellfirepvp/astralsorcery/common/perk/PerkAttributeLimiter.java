/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAttributeLimiter
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkAttributeLimiter {

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(EventPriority.HIGH, PerkAttributeLimiter::onModded);
        bus.addListener(EventPriority.HIGH, PerkAttributeLimiter::onVanilla);
    }

    public static Optional<Limit> getLimit(PerkAttributeType type) {
        return RegistriesAS.REGISTRY_PERK_ATTRIBUTE_LIMITS.stream()
                .filter(limit -> limit.getType().equals(type))
                .findFirst();
    }

    private static void onModded(AttributeEvent.PostProcessModded event) {
        getLimit(event.getType()).ifPresent(limit -> {
            event.setValue(limit.limitValue(event.getValue()));
        });
    }

    private static void onVanilla(AttributeEvent.PostProcessVanilla event) {
        event.resolveAttributeType()
                .flatMap(PerkAttributeLimiter::getLimit)
                .ifPresent(limit -> {
                    event.setValue(limit.limitValue(event.getValue()));
                });
    }

    public static class Limit {

        private final Supplier<? extends PerkAttributeType> type;
        private final Supplier<Double> min;
        private final Supplier<Double> max;

        public Limit(Supplier<? extends PerkAttributeType> type, Supplier<Double> min, Supplier<Double> max) {
            this.type = type;
            this.min = min;
            this.max = max;
        }

        public double limitValue(double value) {
            return Mth.clamp(value, this.min.get(), this.max.get());
        }

        public Supplier<Double> getMin() {
            return this.min;
        }

        public Supplier<Double> getMax() {
            return this.max;
        }

        public PerkAttributeType getType() {
            return this.type.get();
        }
    }
}
