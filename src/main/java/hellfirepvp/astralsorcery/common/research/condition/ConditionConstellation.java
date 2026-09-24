/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.types.ResearchNodeConditionTypesAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConditionConstellation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ConditionConstellation implements ResearchNodeCondition {

    public static final MapCodec<ConditionConstellation> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().listOf().fieldOf("any_constellations").forGetter(condition -> condition.anyConstellations)
    ).apply(inst, ConditionConstellation::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConditionConstellation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS).apply(ByteBufCodecs.list()),
            condition -> condition.anyConstellations,
            ConditionConstellation::new);

    private final List<BaseConstellation> anyConstellations;

    private ConditionConstellation(List<BaseConstellation> anyConstellations) {
        this.anyConstellations = anyConstellations;
    }

    @Override
    public boolean canSee(ResearchNode node, PlayerProgress progress) {
        if (this.anyConstellations.isEmpty()) {
            return !progress.getKnownConstellations().isEmpty();
        }
        return this.anyConstellations.stream().anyMatch(progress::hasDiscoveredConstellation);
    }

    @Override
    public List<Component> getConditionDescription(ResearchNode node, PlayerProgress progress) {
        if (this.anyConstellations.isEmpty()) {
            return List.of(Component.translatable("tome.research.node.condition.constellation.any"));
        }
        String messageKey = "tome.research.node.condition.constellation." + (this.anyConstellations.size() == 1 ? "one" : "many");
        MutableComponent jointConstellations = Component.literal("");
        for (int i = 0; i < this.anyConstellations.size(); i++) {
            if (i > 0) {
                jointConstellations.append(Component.literal(", "));
            }
            jointConstellations.append(this.anyConstellations.get(i).getName());
        }
        return List.of(Component.translatable(messageKey, jointConstellations));
    }

    @Override
    public Type<?> getType() {
        return ResearchNodeConditionTypesAS.CONSTELLATION_DISCOVERED.get();
    }
}
