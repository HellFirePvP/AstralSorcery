/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky.constellation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SkyConstellationPosition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record SkyConstellationPosition(float yaw, float pitch) {

    public static final Codec<SkyConstellationPosition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("yaw").forGetter(SkyConstellationPosition::yaw),
            Codec.FLOAT.fieldOf("pitch").forGetter(SkyConstellationPosition::pitch)
    ).apply(inst, SkyConstellationPosition::new));

    public void validatePosition() {
        if (this.pitch < 10 || this.pitch > 80) {
            throw new IllegalArgumentException("Pitch must be between 10 and 80 degrees. Got: " + this.pitch);
        }
        if (this.yaw < 0 || this.yaw >= 360) {
            throw new IllegalArgumentException("Yaw must be between 0 and 360 degrees. Got: " + this.yaw);
        }
    }

    public FloatPoint asPoint() {
        return new FloatPoint(this.yaw, this.pitch);
    }
}
