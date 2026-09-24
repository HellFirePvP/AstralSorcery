/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SelectableArtifactCondition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record SelectableArtifactCondition(ArtifactPartIdSelector selector, ArtifactCondition condition) {

    public static final Codec<SelectableArtifactCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ArtifactPartIdSelector.CODEC.fieldOf("selector").forGetter(SelectableArtifactCondition::selector),
            ArtifactCondition.CODEC.fieldOf("condition").forGetter(SelectableArtifactCondition::condition)
    ).apply(inst, SelectableArtifactCondition::new));
}
