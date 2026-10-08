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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GemSocketTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:33
 */
public class GemSocketTrigger extends SimpleCriterionTrigger<GemSocketTrigger.Instance> {

    public void trigger(ServerPlayer sPlayer, ItemStack socketedItem) {
        this.trigger(sPlayer, inst -> inst.matches(socketedItem));
    }

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Instance::item)
        ).apply(inst, Instance::new));

        public static Criterion<Instance> socketAny() {
            return AdvancementsAS.GEM_SOCKET.get().createCriterion(new Instance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<Instance> socket(ItemPredicate item) {
            return AdvancementsAS.GEM_SOCKET.get().createCriterion(new Instance(Optional.empty(), Optional.of(item)));
        }

        public boolean matches(ItemStack socketedItem) {
            return this.item.isEmpty() || this.item.get().test(socketedItem);
        }
    }
}
