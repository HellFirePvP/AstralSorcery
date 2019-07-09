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
        STYPE_MOUNTAIN = register(new ResourceLocation(AstralSorcery.MODID, "struct_mountain"), true);
        STYPE_DESERT = register(new ResourceLocation(AstralSorcery.MODID, "struct_desert"), true);
        STYPE_SMALL = register(new ResourceLocation(AstralSorcery.MODID, "struct_small"), true);
        STYPE_TREASURE = register(new ResourceLocation(AstralSorcery.MODID, "struct_treasure"), false);
    }

    private static StructureType register(ResourceLocation name, boolean requiresAvgDistance) {
        StructureType type = new StructureType(name, requiresAvgDistance);
        AstralSorcery.getProxy().getRegistryPrimer().register(type);
        return type;
    }

}
