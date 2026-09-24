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
import hellfirepvp.astralsorcery.common.lib.types.ResearchNodeConditionTypesAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchFlag;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchFlag
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ConditionResearchFlag(ResearchFlag flag) implements ResearchNodeCondition {

    public static final MapCodec<ConditionResearchFlag> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            StringRepresentable.fromEnum(ResearchFlag::values).fieldOf("flag").forGetter(ConditionResearchFlag::flag)
    ).apply(inst, ConditionResearchFlag::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConditionResearchFlag> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(ResearchFlag.class),
            ConditionResearchFlag::flag,
            ConditionResearchFlag::new);

    @Override
    public ResearchNodeVisibility.Type getSuggestedVisibility(ResearchNode node, PlayerProgress progress) {
        return ResearchNodeVisibility.Type.HIDDEN;
    }

    @Override
    public boolean canSee(ResearchNode node, PlayerProgress progress) {
        return progress.isFlagSet(this.flag);
    }

    @Override
    public List<Component> getConditionDescription(ResearchNode node, PlayerProgress progress) {
        return List.of();
    }

    @Override
    public Type<?> getType() {
        return ResearchNodeConditionTypesAS.RESEARCH_FLAG_SET.get();
    }
}
