/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import hellfirepvp.astralsorcery.common.loot.global.LootModifierMagnetDrops;
import hellfirepvp.astralsorcery.common.loot.global.LootModifierPerkVoidTrash;
import hellfirepvp.astralsorcery.common.loot.global.LootModifierScorchingHeat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryLoot
 * Created by HellFirePvP
 * Date: 20.07.2019 / 21:48
 */
public class RegistryLoot {

    private RegistryLoot() {}

    public static void init() {
        registerGlobalModofier(new LootModifierScorchingHeat.Serializer(), AstralSorcery.key("scorching_heat"));
        registerGlobalModofier(new LootModifierPerkVoidTrash.Serializer(), AstralSorcery.key("perk_void_trash"));
        registerGlobalModofier(new LootModifierMagnetDrops.Serializer(), AstralSorcery.key("magnet_drops"));

        registerFunction(new LinearLuckBonus.Serializer(AstralSorcery.key("linear_luck_bonus")));
        registerFunction(new RandomCrystalProperty.Serializer(AstralSorcery.key("random_crystal_property")));
        registerFunction(new CopyCrystalProperties.Serializer(AstralSorcery.key("copy_crystal_properties")));
        registerFunction(new CopyConstellation.Serializer(AstralSorcery.key("copy_constellation")));
    }

    private static <T extends ILootFunction> void registerFunction(ILootFunction.Serializer<T> serializer) {
        LootFunctionManager.registerFunction(serializer);
    }

    private static <T extends IGlobalLootModifier> void registerGlobalModofier(GlobalLootModifierSerializer<T> modifier, ResourceLocation key) {
        modifier.setRegistryName(key);
        AstralSorcery.getProxy().getRegistryPrimer().register(modifier);
    }

}
