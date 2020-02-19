/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffectProvider;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryMantleEffects
 * Created by HellFirePvP
 * Date: 17.02.2020 / 20:35
 */
public class RegistryMantleEffects {

    private RegistryMantleEffects() {}

    public static void init() {

    }

    private static MantleEffectProvider makeProvider(IWeakConstellation cst, Function<CompoundNBT, ? extends MantleEffect> effectProvider) {
        return new MantleEffectProvider(cst) {
            @Override
            public MantleEffect createEffect(@Nullable CompoundNBT data) {
                return effectProvider.apply(data);
            }
        };
    }

    private static <T extends MantleEffectProvider> T register(T effectProvider) {
        AstralSorcery.getProxy().getRegistryPrimer().register(effectProvider);
        return effectProvider;
    }
}
