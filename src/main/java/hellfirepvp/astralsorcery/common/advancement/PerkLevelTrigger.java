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
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkLevelTrigger
 * Created by HellFirePvP
 * Date: 07.10.2026 / 23:29
 */
public class PerkLevelTrigger extends SimpleCriterionTrigger<PerkLevelTrigger.Instance> {

    public void trigger(ServerPlayer sPlayer, int levelReached) {
        this.trigger(sPlayer, inst -> inst.levelNeeded() <= levelReached);
    }

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public record Instance(Optional<ContextAwarePredicate> player, int levelNeeded) implements SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                Codec.INT.fieldOf("levelNeeded").forGetter(Instance::levelNeeded)
        ).apply(inst, Instance::new));

        public static Criterion<Instance> reachLevel(int levelNeeded) {
            return AdvancementsAS.PERK_LEVEL.get().createCriterion(new Instance(Optional.empty(), levelNeeded));
        }
    }
}
