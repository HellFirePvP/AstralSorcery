/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SizeLimitMap
 * Created by HellFirePvP
 * Date: 19.12.2020 / 17:20
 */
public class SizeLimitMap<K, V> extends LinkedHashMap<K, V> {

    private final int maxSize;

    public SizeLimitMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
