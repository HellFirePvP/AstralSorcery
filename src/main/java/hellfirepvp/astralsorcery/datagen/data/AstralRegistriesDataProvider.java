/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data;

import hellfirepvp.astralsorcery.common.lib.DamageTypesAS;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import hellfirepvp.astralsorcery.datagen.data.world.AstralWorldGenProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralRegistriesDataProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralRegistriesDataProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, AstralRegistriesDataProvider::generateDamageTypes)
            .add(Registries.ENCHANTMENT, AstralRegistriesDataProvider::generateEnchantments)
            .add(Registries.CONFIGURED_FEATURE, AstralWorldGenProvider::generateConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, AstralWorldGenProvider::generatePlacedFeatures)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AstralWorldGenProvider::generateBiomeModifiers)
            .add(Registries.STRUCTURE, AstralWorldGenProvider::generateStructures)
            .add(Registries.STRUCTURE_SET, AstralWorldGenProvider::generateStructureSets)
            .add(Registries.TEMPLATE_POOL, AstralWorldGenProvider::generateStructurePools)
            .add(Registries.PROCESSOR_LIST, AstralWorldGenProvider::generateStructureProcessorLists);

    public static RegistrySetBuilder getRegistryBuilder() {
        return BUILDER;
    }

    private static void generateDamageTypes(BootstrapContext<DamageType> context) {
        DamageTypesAS.STELLAR.register(context);
    }

    private static void generateEnchantments(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        HolderSet<Enchantment> silkTouchSet = HolderSet.direct(enchantments.getOrThrow(Enchantments.SILK_TOUCH));
        EnchantmentsAS.SCORCHING_HEAT.register(context, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
                1,
                1,
                Enchantment.dynamicCost(30, 10),
                Enchantment.dynamicCost(60, 10),
                10,
                EquipmentSlotGroup.MAINHAND
        )).exclusiveWith(silkTouchSet));
    }
}
