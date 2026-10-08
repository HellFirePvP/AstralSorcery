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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemAttunementTrigger
 * Created by HellFirePvP
 * Date: 08.10.2026 / 09:44
 */
public class ItemAttunementTrigger extends SimpleCriterionTrigger<ItemAttunementTrigger.Instance> {

    public void trigger(ServerPlayer sPlayer, BaseConstellation attunedTo, ItemStack attunedItem) {
        this.trigger(sPlayer, inst -> inst.matches(attunedTo, attunedItem));
    }

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public record Instance(Optional<ContextAwarePredicate> player,
                           Optional<BaseConstellation> constellation,
                           Optional<ItemPredicate> item) implements SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("constellation").forGetter(Instance::constellation),
                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Instance::item)
        ).apply(inst, Instance::new));

        public static Criterion<Instance> attuneAnyItem() {
            return AdvancementsAS.ITEM_ATTUNEMENT.get().createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<Instance> attuneItem(ItemPredicate item) {
            return AdvancementsAS.ITEM_ATTUNEMENT.get().createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.of(item)));
        }

        public static Criterion<Instance> attuneItemTo(BaseConstellation constellation) {
            return AdvancementsAS.ITEM_ATTUNEMENT.get().createCriterion(new Instance(Optional.empty(), Optional.of(constellation), Optional.empty()));
        }

        public static Criterion<Instance> attuneItemTo(BaseConstellation constellation, ItemPredicate item) {
            return AdvancementsAS.ITEM_ATTUNEMENT.get().createCriterion(new Instance(Optional.empty(), Optional.of(constellation), Optional.of(item)));
        }

        public boolean matches(BaseConstellation attunedTo, ItemStack attunedItem) {
            if (this.constellation.isPresent() && !this.constellation.get().equals(attunedTo)) {
                return false;
            }
            return this.item.isEmpty() || this.item.get().test(attunedItem);
        }
    }
}
