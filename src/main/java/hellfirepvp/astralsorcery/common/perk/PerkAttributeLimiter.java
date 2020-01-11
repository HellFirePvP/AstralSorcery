/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeLimiter
 * Created by HellFirePvP
 * Date: 25.08.2019 / 17:35
 */
public class PerkAttributeLimiter {

    private static final LimitRange ANY = new AcceptAll();
    private static final Map<PerkAttributeType, LimitRange> LIMITS = new HashMap<>();

    private PerkAttributeLimiter() {}

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EventPriority.HIGH, PerkAttributeLimiter::onModded);
        bus.addListener(EventPriority.HIGH, PerkAttributeLimiter::onVanilla);
    }

    public static void limit(PerkAttributeType type, Supplier<Double> min, Supplier<Double> max) {
        LIMITS.put(type, new LimitRange(min, max));
    }

    public static boolean hasLimit(PerkAttributeType type) {
        return LIMITS.containsKey(type);
    }

    @Nonnull
    public static Pair<Double, Double> getLimit(PerkAttributeType type) {
        return LIMITS.getOrDefault(type, ANY).asPair();
    }

    private static void onModded(AttributeEvent.PostProcessModded event) {
        if (hasLimit(event.getType())) {
            event.setValue(LIMITS.getOrDefault(event.getType(), ANY).limit(event.getValue()));
        }
    }

    private static void onVanilla(AttributeEvent.PostProcessVanilla event) {
        PerkAttributeType type = event.resolveAttributeType();
        if (type != null) {
            event.setValue(LIMITS.getOrDefault(type, ANY).limit(event.getValue()));
        }
    }

    private static class LimitRange {

        private final Supplier<Double> min;
        private final Supplier<Double> max;

        private LimitRange(Supplier<Double> min, Supplier<Double> max) {
            this.min = min;
            this.max = max;
        }

        protected double limit(double value) {
            return MathHelper.clamp(value, min.get(), max.get());
        }

        private Pair<Double, Double> asPair() {
            return Pair.of(min.get(), max.get());
        }

    }

    private static class AcceptAll extends LimitRange {

        private AcceptAll() {
            super(() -> Double.MIN_VALUE, () -> Double.MAX_VALUE);
        }

        @Override
        protected double limit(double value) {
            return value;
        }
    }

}
