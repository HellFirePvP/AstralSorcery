/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchNodeCondition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface ResearchNodeCondition {

    Codec<ResearchNodeCondition> CODEC = RegistriesAS.REGISTRY_RESEARCH_NODE_CONDITION_TYPES.byNameCodec()
            .dispatch(ResearchNodeCondition::getType, ResearchNodeCondition.Type::codec);
    StreamCodec<RegistryFriendlyByteBuf, ResearchNodeCondition> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_RESEARCH_NODE_CONDITION_TYPES)
            .dispatch(ResearchNodeCondition::getType, ResearchNodeCondition.Type::syncCodec);

    default ResearchNodeVisibility.Type getSuggestedVisibility(ResearchNode node, PlayerProgress progress) {
        return ResearchNodeVisibility.Type.LOCKED;
    }

    boolean canSee(ResearchNode node, PlayerProgress progress);

    List<Component> getConditionDescription(ResearchNode node, PlayerProgress progress);

    Type<?> getType();

    record Type<T extends ResearchNodeCondition>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> syncCodec) {}

}
