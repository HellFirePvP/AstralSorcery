/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LootTablesAS;
import hellfirepvp.astralsorcery.common.loot.GenerateCrystalPropertiesFunction;
import hellfirepvp.astralsorcery.common.loot.GenerateRandomArtifactFunction;
import hellfirepvp.astralsorcery.common.loot.condition.PlayerNearbyCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralGameplayLootTableProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record AstralGameplayLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(LootTablesAS.SHOOTING_STAR, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1F, 1F))
                        .when(PlayerNearbyCondition.nearby(6 * 16, true))
                        .add(LootItem.lootTableItem(ItemsAS.ROCK_CRYSTAL)
                                .apply(GenerateCrystalPropertiesFunction.randomProperties())))
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1F, 1F))
                        .add(LootItem.lootTableItem(ItemsAS.ARTIFACT)
                                .apply(GenerateRandomArtifactFunction.randomArtifact())))
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(2F, 10F))
                        .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(1))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(3))
                        .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).setWeight(3))
                        .add(LootItem.lootTableItem(ItemsAS.AQUAMARINE).setWeight(4))));

        output.accept(LootTablesAS.SHRINE_CHEST_SMALL, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(3, 5))
                        .setBonusRolls(UniformGenerator.between(1, 2))
                        .add(LootItem.lootTableItem(ItemsAS.CONSTELLATION_PAPER).setWeight(4))
                        .add(LootItem.lootTableItem(ItemsAS.AQUAMARINE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                .setWeight(10))
                        .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .setWeight(10))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 7)))
                                .setWeight(8))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                .setWeight(5))
                        .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(2))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(1))
                        .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(1))
                ));
        output.accept(LootTablesAS.SHRINE_CHEST, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(4, 7))
                        .setBonusRolls(UniformGenerator.between(2, 3))
                        .add(LootItem.lootTableItem(ItemsAS.CONSTELLATION_PAPER).setWeight(4))
                        .add(LootItem.lootTableItem(ItemsAS.AQUAMARINE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                                .setWeight(10))
                        .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .setWeight(10))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 7)))
                                .setWeight(8))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                .setWeight(5))
                        .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(2))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(1))
                        .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(1))
                ));

        output.accept(LootTablesAS.DIG_SITE_ARCHAEOLOGY, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ItemsAS.CONSTELLATION_PAPER))
                        .add(LootItem.lootTableItem(Items.EMERALD))
                        .add(LootItem.lootTableItem(Items.DIAMOND))
                        .add(LootItem.lootTableItem(Items.ENDER_PEARL))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT))
                        .add(LootItem.lootTableItem(ItemsAS.AQUAMARINE)))
        );
    }
}
