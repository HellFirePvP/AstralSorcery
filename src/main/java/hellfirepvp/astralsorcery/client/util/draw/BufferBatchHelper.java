/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.draw;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BufferBatchHelper
 * Created by HellFirePvP
 * Date: 08.07.2019 / 20:39
 */
public class BufferBatchHelper {

    public static BufferContext make() {
        return make(0x200_000);
    }

    public static BufferContext make(int size) {
        return new BufferContext(size);
    }

}
