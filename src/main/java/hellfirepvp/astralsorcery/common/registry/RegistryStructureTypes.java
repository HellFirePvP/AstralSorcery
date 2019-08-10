/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.StructuresAS;
import hellfirepvp.astralsorcery.common.structure.StructureBlockArray;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.observerlib.common.block.BlockArray;
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
        STYPE_MOUNTAIN = registerAS("struct_mountain", 768, () -> StructuresAS.STRUCT_MOUNTAIN_SHRINE);
        STYPE_DESERT = registerAS("struct_desert", 1024, () -> StructuresAS.STRUCT_DESERT_SHRINE);
        STYPE_SMALL = registerAS("struct_small", 512, () -> StructuresAS.STRUCT_SMALL_SHRINE);

        PTYPE_RITUAL_PEDESTAL = registerAS("pattern_ritual_pedestal", () -> StructuresAS.STRUCT_RITUAL_PEDESTAL);
        PTYPE_ENHANCED_COLLECTOR_CRYSTAL = registerAS("pattern_enhanced_collector_crystal", () -> StructuresAS.STRUCT_ENHANCED_COLLECTOR_CRYSTAL);
    }

    private static StructureType registerAS(String name, Supplier<BlockArray> structureSupplier) {
        return registerAS(name, -1, structureSupplier);
    }

    private static StructureType registerAS(String name, int averageDistance, Supplier<BlockArray> structureSupplier) {
        return register(new ResourceLocation(AstralSorcery.MODID, name), averageDistance, structureSupplier);
    }

    private static StructureType register(ResourceLocation name, int averageDistance, Supplier<BlockArray> structureSupplier) {
        StructureType type = new StructureType(name, structureSupplier, averageDistance);
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

}
