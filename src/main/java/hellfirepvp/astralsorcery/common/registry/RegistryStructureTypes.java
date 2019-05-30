/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.structure.StructureType;
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
        MOUNTAIN = new StructureType(new ResourceLocation(AstralSorcery.MODID, "struct_mountain"), true);
        DESERT = new StructureType(new ResourceLocation(AstralSorcery.MODID, "struct_desert"), true);
        SMALL = new StructureType(new ResourceLocation(AstralSorcery.MODID, "struct_small"), true);
        TREASURE = new StructureType(new ResourceLocation(AstralSorcery.MODID, "struct_treasure"), false);
    }

}
