/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.config.ServerConfig;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectRegistry
 * Created by HellFirePvP
 * Date: 17.02.2020 / 20:32
 */
public class MantleEffectRegistry {

    public static void addConfigEntries(ServerConfig config) {

    }

    @Nullable
    public static MantleEffect createInstance(CompoundNBT data, IWeakConstellation constellation) {
        MantleEffectProvider effect = RegistriesAS.REGISTRY_MANTLE_EFFECT.getValue(constellation.getRegistryName());
        if (effect != null) {
            return effect.createEffect(data);
        }
        return null;
    }

}
