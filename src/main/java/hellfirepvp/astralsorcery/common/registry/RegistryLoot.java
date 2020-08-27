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
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;

import static hellfirepvp.astralsorcery.common.lib.LootAS.*;

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

        Functions.LINEAR_LUCK_BONUS = registerFunction(new LinearLuckBonus.Serializer(), AstralSorcery.key("linear_luck_bonus"));
        Functions.RANDOM_CRYSTAL_PROPERTIES = registerFunction(new RandomCrystalProperty.Serializer(), AstralSorcery.key("random_crystal_property"));
        Functions.COPY_CRYSTAL_PROPERTIES = registerFunction(new CopyCrystalProperties.Serializer(), AstralSorcery.key("copy_crystal_properties"));
        Functions.COPY_CONSTELLATION = registerFunction(new CopyConstellation.Serializer(), AstralSorcery.key("copy_constellation"));
    }

    private static <T extends LootFunction> LootFunctionType registerFunction(LootFunction.Serializer<T> serializer, ResourceLocation key) {
        return LootFunctionManager.func_237451_a_(key.toString(), serializer);
    }

    private static <T extends IGlobalLootModifier> void registerGlobalModofier(GlobalLootModifierSerializer<T> modifier, ResourceLocation key) {
        modifier.setRegistryName(key);
        AstralSorcery.getProxy().getRegistryPrimer().register(modifier);
    }

}
