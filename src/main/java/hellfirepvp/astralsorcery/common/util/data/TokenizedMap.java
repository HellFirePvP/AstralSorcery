/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.HashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TokenizedMap
 * Created by HellFirePvP
 * Date: 07.11.2016 / 11:24
 */
public class TokenizedMap<K, V extends TokenizedMap.MapToken<?>> extends HashMap<K, V> {

    public static interface MapToken<V> {

        public V getValue();

    }

}
