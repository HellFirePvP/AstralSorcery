package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectFertilitas;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectFornax;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectHorologium;
import hellfirepvp.astralsorcery.common.lib.Constellations;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectRegistry
 * Created by HellFirePvP
 * Date: 01.10.2016 / 15:49
 */
public class ConstellationEffectRegistry {

    private static Map<Constellation, ConstellationEffectProvider> providerMap = new HashMap<>();
    private static Map<Constellation, ConstellationEffect> singleRenderInstances = new HashMap<>();

    public static void init() {
        register(Constellations.fertilitas, CEffectFertilitas::new);
        register(Constellations.fornax, CEffectFornax::new);
        register(Constellations.horologium, CEffectHorologium::new);
    }

    private static void register(Constellation c, ConstellationEffectProvider provider) {
        providerMap.put(c, provider);
        singleRenderInstances.put(c, provider.provideEffectInstance());
    }

    @Nullable
    public static ConstellationEffect clientRenderInstance(Constellation c) {
        return singleRenderInstances.get(c);
    }

    @Nullable
    public static ConstellationEffect getEffect(Constellation c) {
        ConstellationEffectProvider p = providerMap.get(c);
        if(p != null) {
            return p.provideEffectInstance();
        }
        return null;
    }

    public static interface ConstellationEffectProvider {

        public ConstellationEffect provideEffectInstance();

    }

}
