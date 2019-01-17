/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.reader;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeReaderRegistry
 * Created by HellFirePvP
 * Date: 16.01.2019 / 15:38
 */
public class AttributeReaderRegistry {

    private static Map<String, AttributeReader> readerMap = Maps.newHashMap();

    public static void registerTypeReader(String typeString, AttributeReader reader) {
        if (AttributeTypeRegistry.getType(typeString) == null) {
            return;
        }

        readerMap.put(typeString, reader);
    }

    @Nullable
    public static AttributeReader getReader(String typeString) {
        return readerMap.get(typeString);
    }

}
