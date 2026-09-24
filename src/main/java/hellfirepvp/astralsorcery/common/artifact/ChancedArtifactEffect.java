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
 * Class: ChancedArtifactEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ChancedArtifactEffect(ArtifactEffect effect, float chance) {

    public static final Codec<ChancedArtifactEffect> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ArtifactEffect.CODEC.fieldOf("effect").forGetter(ChancedArtifactEffect::effect),
            Codec.FLOAT.fieldOf("chance").forGetter(ChancedArtifactEffect::chance)
    ).apply(inst, ChancedArtifactEffect::new));

}
