/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.registry;

import hellfirepvp.astralsorcery.common.event.StarlightNetworkEvent;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: SourceClassRegistry
* Created by HellFirePvP
* Date: 04.08.2016 / 16:33
*/
public class SourceClassRegistry {

    public static final SourceClassRegistry eventInstance = new SourceClassRegistry();

    private static Map<ResourceLocation, SourceProvider> providerMap = new HashMap<>();

    private SourceClassRegistry() {}

    public void registerProvider(SourceProvider provider) {
        register(provider);
    }

    @Nullable
    public static SourceProvider getProvider(ResourceLocation identifier) {
        return providerMap.get(identifier);
    }

    public static void register(SourceProvider provider) {
        if (providerMap.containsKey(provider.getIdentifier())) {
            throw new IllegalArgumentException("Already registered identifier SourceProvider: " + provider.getIdentifier());
        }
        providerMap.put(provider.getIdentifier(), provider);
    }

    public static void setupRegistry() {
        register(new IndependentCrystalSource.Provider());

        MinecraftForge.EVENT_BUS.post(new StarlightNetworkEvent.SourceProviderRegistry(eventInstance));
    }

    public static interface SourceProvider {

        public IIndependentStarlightSource provideEmptySource();

        @Nonnull
        public ResourceLocation getIdentifier();

    }

}
