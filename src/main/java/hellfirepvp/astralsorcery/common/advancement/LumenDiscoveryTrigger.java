/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.AdvancementsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenDiscoveryTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:40
 */
public class LumenDiscoveryTrigger extends SimpleCriterionTrigger<LumenDiscoveryTrigger.Instance> {

    public void trigger(ServerPlayer sPlayer, Lumen discovered) {
        this.trigger(sPlayer, inst -> inst.matches(discovered));
    }

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public record Instance(Optional<ContextAwarePredicate> player,
                           Optional<Lumen> lumen,
                           Optional<Boolean> elementary) implements SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                RegistriesAS.REGISTRY_LUMEN.byNameCodec().optionalFieldOf("lumen").forGetter(Instance::lumen),
                Codec.BOOL.optionalFieldOf("elementary").forGetter(Instance::elementary)
        ).apply(inst, Instance::new));

        public static Criterion<Instance> discoverAny() {
            return AdvancementsAS.LUMEN_DISCOVERY.get().createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<Instance> discover(Lumen lumen) {
            return AdvancementsAS.LUMEN_DISCOVERY.get().createCriterion(new Instance(Optional.empty(), Optional.of(lumen), Optional.empty()));
        }

        public static Criterion<Instance> discoverElementary() {
            return AdvancementsAS.LUMEN_DISCOVERY.get().createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.of(true)));
        }

        public static Criterion<Instance> discoverNonElementary() {
            return AdvancementsAS.LUMEN_DISCOVERY.get().createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.of(false)));
        }

        public boolean matches(Lumen discovered) {
            if (this.lumen.isPresent() && !this.lumen.get().equals(discovered)) {
                return false;
            }
            return this.elementary.isEmpty() || this.elementary.get() == discovered.isElementary();
        }
    }
}
