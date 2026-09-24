/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.lumen.data;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenDisplayPosition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record LumenDisplayPosition(int x, int y) {

    public static final Codec<LumenDisplayPosition> CODEC = IntPoint.CODEC
            .xmap(point -> new LumenDisplayPosition(point.x(), point.y()), LumenDisplayPosition::asPoint);

    public IntPoint asPoint() {
        return new IntPoint(this.x, this.y);
    }
}
