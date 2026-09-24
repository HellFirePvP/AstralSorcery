/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.CacheSupplier;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: NameUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class NameUtil {

    public static ResourceLocation prefixPath(ResourceLocation key, String prefix) {
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), prefix + key.getPath());
    }

    public static ResourceLocation suffixPath(ResourceLocation key, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + suffix);
    }

    public static <T> Supplier<String> cacheName(String type, Registry<T> registry, T value) {
        return new CacheSupplier<>(() -> Util.makeDescriptionId(type, registry.getKey(value)));
    }
    
    public static List<String> resolveLocalizedLines(String key) {
        List<String> ids = new ArrayList<>();
        Language lang = Language.getInstance();

        if (lang.has(String.format("%s.1", key))) {
            int count = 1;
            while (lang.has(String.format("%s.%s", key, count))) {
                ids.add(String.format("%s.%s", key, count));
                count++;
            }
        } else if (lang.has(key)) {
            ids.add(key);
        }
        return ids;
    }
}
