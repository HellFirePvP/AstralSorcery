/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.loot.CopyConstellation;
import hellfirepvp.astralsorcery.common.loot.CopyCrystalProperties;
import hellfirepvp.astralsorcery.common.loot.LinearLuckBonus;
import hellfirepvp.astralsorcery.common.loot.RandomCrystalProperty;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryLootFunctions
 * Created by HellFirePvP
 * Date: 20.07.2019 / 21:48
 */
public class RegistryLootFunctions {

    private RegistryLootFunctions() {}

    public static void registerLootFunctions() {
        register(new LinearLuckBonus.Serializer(AstralSorcery.key("linear_luck_bonus")));
        register(new RandomCrystalProperty.Serializer(AstralSorcery.key("random_crystal_property")));
        register(new CopyCrystalProperties.Serializer(AstralSorcery.key("copy_crystal_properties")));
        register(new CopyConstellation.Serializer(AstralSorcery.key("copy_constellation")));
    }

    private static <T extends ILootFunction> void register(ILootFunction.Serializer<T> serializer) {
        LootFunctionManager.registerFunction(serializer);
    }

}
