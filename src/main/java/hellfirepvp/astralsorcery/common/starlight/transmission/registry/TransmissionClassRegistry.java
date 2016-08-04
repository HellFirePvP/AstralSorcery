package hellfirepvp.astralsorcery.common.starlight.transmission.registry;

import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimplePrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionSourceNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.SimpleCrystalPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.SimpleCrystalTransmissionNode;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionClassRegistry
 * Created by HellFirePvP
 * Date: 04.08.2016 / 19:51
 */
public class TransmissionClassRegistry {

    private static Map<String, TransmissionProvider> providerMap = new HashMap<>();

    @Nullable
    public static TransmissionProvider getProvider(String identifier) {
        return providerMap.get(identifier);
    }

    public static void register(TransmissionProvider provider) {
        if(providerMap.containsKey(provider.getIdentifier())) throw new RuntimeException("Already registered identifier TransmissionProvider: " + provider.getIdentifier());
        providerMap.put(provider.getIdentifier(), provider);
    }

    public static void setupRegistry() {
        register(new SimpleTransmissionNode.Provider());
        register(new SimplePrismTransmissionNode.Provider());
        register(new SimpleTransmissionSourceNode.Provider());
        register(new SimpleCrystalTransmissionNode.Provider());
        register(new SimpleCrystalPrismTransmissionNode.Provider());
    }

    public static interface TransmissionProvider {

        public IPrismTransmissionNode provideEmptyNode();

        public String getIdentifier();

    }
}
