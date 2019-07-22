/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.util.ResourceLocation;

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
        STYPE_MOUNTAIN = registerAS("struct_mountain", 768);
        STYPE_DESERT = registerAS("struct_desert", 1024);
        STYPE_SMALL = registerAS("struct_small", 512);

        PTYPE_RITUAL_PEDESTAL = registerAS("pattern_ritual_pedestal");
    }

    private static StructureType registerAS(String name) {
        return registerAS(name, -1);
    }

    private static StructureType registerAS(String name, int averageDistance) {
        return register(new ResourceLocation(AstralSorcery.MODID, name), averageDistance);
    }

    private static StructureType register(ResourceLocation name, int averageDistance) {
        StructureType type = new StructureType(name, averageDistance);
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

}
