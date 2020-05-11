/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.StructuresAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

import static hellfirepvp.astralsorcery.common.lib.StructureTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryStructureTypes
 * Created by HellFirePvP
 * Date: 30.05.2019 / 15:12
 */
public class RegistryStructureTypes {

    private RegistryStructureTypes() {}

    public static void init() {
        EMPTY = registerAS("empty", () -> StructuresAS.EMPTY);

        PTYPE_ALTAR_ATTUNEMENT = registerAS("pattern_altar_attunement", () -> StructuresAS.STRUCT_ALTAR_ATTUNEMENT);
        PTYPE_ALTAR_CONSTELLATION = registerAS("pattern_altar_constellation", () -> StructuresAS.STRUCT_ALTAR_CONSTELLATION);
        PTYPE_ALTAR_TRAIT = registerAS("pattern_altar_trait", () -> StructuresAS.STRUCT_ALTAR_TRAIT);
        PTYPE_RITUAL_PEDESTAL = registerAS("pattern_ritual_pedestal", () -> StructuresAS.STRUCT_RITUAL_PEDESTAL);
        PTYPE_INFUSER = registerAS("pattern_infuser", () -> StructuresAS.STRUCT_INFUSER);
        PTYPE_ENHANCED_COLLECTOR_CRYSTAL = registerAS("pattern_enhanced_collector_crystal", () -> StructuresAS.STRUCT_ENHANCED_COLLECTOR_CRYSTAL);
        PTYPE_SPECTRAL_RELAY = registerAS("pattern_spectral_relay", () -> StructuresAS.STRUCT_SPECTRAL_RELAY);
        PTYPE_ATTUNEMENT_ALTAR = registerAS("pattern_attunement_altar", () -> StructuresAS.STRUCT_ATTUNEMENT_ALTAR);
    }

    private static StructureType registerAS(String name, Supplier<BlockArray> structureSupplier) {
        return register(AstralSorcery.key(name), structureSupplier);
    }

    private static StructureType register(ResourceLocation name, Supplier<BlockArray> structureSupplier) {
        StructureType type = new StructureType(name, structureSupplier);
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

}
