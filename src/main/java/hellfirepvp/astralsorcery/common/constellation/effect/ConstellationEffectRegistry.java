/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectVicio;
import hellfirepvp.astralsorcery.common.data.config.ServerConfig;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectRegistry
 * Created by HellFirePvP
 * Date: 11.06.2019 / 19:32
 */
public class ConstellationEffectRegistry {

    public static final String LUCERNA_SKIP_ENTITY = "skip.spawn.deny";

    private static Map<IWeakConstellation, ConstellationEffect> clientEffectInstances = new HashMap<>();

    public static void addConfigEntries(ServerConfig config) {
        config.addConfigEntry(new CEffectVicio.VicioConfig());
    }

    @Nullable
    public static ConstellationEffect createInstance(ILocatable origin, IWeakConstellation constellation) {
        ConstellationEffectProvider effect = RegistriesAS.REGISTRY_CONSTELLATION_EFFECT.getValue(constellation.getRegistryName());
        if (effect != null) {
            return effect.createEffect(origin);
        }
        return null;
    }

    public static void createClientInstance(ConstellationEffectProvider provider) {
        clientEffectInstances.put(provider.getConstellation(), provider.createEffect(null));
    }

    @Nullable
    public static ConstellationEffect getClientEffect(IWeakConstellation cst) {
        return clientEffectInstances.get(cst);
    }
}
