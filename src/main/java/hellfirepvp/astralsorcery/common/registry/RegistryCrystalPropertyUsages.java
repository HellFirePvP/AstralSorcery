/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertyUsage;

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Usages.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryCrystalPropertyUsages
 * Created by HellFirePvP
 * Date: 20.08.2019 / 21:51
 */
public class RegistryCrystalPropertyUsages {

    private RegistryCrystalPropertyUsages() {}

    public static void init() {
        USE_RITUAL_EFFECT = createUsage("ritual.effect");
        USE_RITUAL_RANGE = createUsage("ritual.range");
        USE_RITUAL_CAPACITY = createUsage("ritual.capacity");
        USE_COLLECTOR_CRYSTAL = createUsage("collector");
        USE_LENS_TRANSFER = createUsage("lens.transfer");
        USE_TOOL_DURABILITY = createUsage("tool.durability");
        USE_TOOL_EFFECTIVENESS = createUsage("tool.effectiveness");
    }

    private static <T extends PropertyUsage> T createUsage(String plainName) {
        return (T) registerUsage(new PropertyUsage(AstralSorcery.key(plainName)));
    }

    private static <T extends PropertyUsage> T registerUsage(T usage) {
        AstralSorcery.getProxy().getRegistryPrimer().register(usage);
        return usage;
    }

}
