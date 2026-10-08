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
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.AdvancementsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerAttunementTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:45
 */
public class PlayerAttunementTrigger extends SimpleCriterionTrigger<PlayerAttunementTrigger.Instance> {

    public void trigger(ServerPlayer sPlayer, BaseConstellation attunedTo) {
        this.trigger(sPlayer, inst -> inst.matches(attunedTo));
    }

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public record Instance(Optional<ContextAwarePredicate> player, Optional<BaseConstellation> constellation) implements SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("constellation").forGetter(Instance::constellation)
        ).apply(inst, Instance::new));

        public static Criterion<Instance> attuneAny() {
            return AdvancementsAS.PLAYER_ATTUNEMENT.get().createCriterion(new Instance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<Instance> attuneTo(BaseConstellation constellation) {
            return AdvancementsAS.PLAYER_ATTUNEMENT.get().createCriterion(new Instance(Optional.empty(), Optional.of(constellation)));
        }

        public boolean matches(BaseConstellation attunedTo) {
            return this.constellation.isEmpty() || this.constellation.get().equals(attunedTo);
        }
    }
}
