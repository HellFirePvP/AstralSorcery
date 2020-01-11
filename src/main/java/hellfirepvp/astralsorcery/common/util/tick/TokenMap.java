/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tick;

import java.util.HashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TokenMap
 * Created by HellFirePvP
 * Date: 06.07.2019 / 21:46
 */
public class TokenMap<K, V extends TokenMap.MapToken<?>> extends HashMap<K, V> {

    public static interface MapToken<V> {

        public V getValue();

    }

}
