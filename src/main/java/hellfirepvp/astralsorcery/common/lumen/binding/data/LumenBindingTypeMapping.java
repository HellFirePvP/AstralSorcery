/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.data;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingTypeMapping
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingTypeMapping {

    public static final Codec<LumenBindingTypeMapping> CODEC =
            Codec.unboundedMap(ResourceKey.codec(RegistriesAS.KEY_LUMEN), ResourceLocation.CODEC)
                    .xmap(LumenBindingTypeMapping::of, LumenBindingTypeMapping::getMapping);

    private final Map<ResourceKey<Lumen>, ResourceLocation> mapping = new HashMap<>();

    private LumenBindingTypeMapping(Map<ResourceKey<Lumen>, ResourceLocation> mapping) {
        this.mapping.putAll(mapping);
    }

    public Map<ResourceKey<Lumen>, ResourceLocation> getMapping() {
        return Collections.unmodifiableMap(this.mapping);
    }

    public static LumenBindingTypeMapping of(Map<ResourceKey<Lumen>, ResourceLocation> mapping) {
        return new LumenBindingTypeMapping(mapping);
    }
}
