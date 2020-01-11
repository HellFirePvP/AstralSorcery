/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync;

import hellfirepvp.astralsorcery.common.data.sync.base.AbstractDataProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SyncDataRegistry
 * Created by HellFirePvP
 * Date: 27.08.2019 / 06:27
 */
public class SyncDataRegistry {

    private static final Map<ResourceLocation, AbstractDataProvider<?, ?>> REGISTRY = new HashMap<>();

    private SyncDataRegistry() {}

    static void register(AbstractDataProvider<?, ?> provider) {
        REGISTRY.put(provider.getKey(), provider);
    }

    public static Collection<ResourceLocation> getKnownKeys() {
        return REGISTRY.keySet();
    }

    @Nullable
    public static AbstractDataProvider<?, ?> getProvider(ResourceLocation key) {
        return REGISTRY.get(key);
    }

}
