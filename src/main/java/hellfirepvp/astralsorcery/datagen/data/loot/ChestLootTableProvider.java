/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LootTablesAS;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.functions.SetCount;

import java.util.function.BiConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChestLootTableProvider
 * Created by HellFirePvP
 * Date: 02.05.2020 / 15:37
 */
public class ChestLootTableProvider extends ChestLootTables {

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> registrar) {
        registrar.accept(LootTablesAS.SHRINE_CHEST,
                LootTable.builder()
                    .addLootPool(LootPool.builder()
                            .rolls(RandomValueRange.of(3, 5))
                            .bonusRolls(1, 2)
                            .addEntry(ItemLootEntry.builder(ItemsAS.CONSTELLATION_PAPER).weight(18))
                            .addEntry(ItemLootEntry.builder(ItemsAS.AQUAMARINE).weight(12).acceptFunction(SetCount.builder(RandomValueRange.of(1, 3))))
                            .addEntry(ItemLootEntry.builder(Items.BONE).weight(10).acceptFunction(SetCount.builder(RandomValueRange.of(1, 3))))
                            .addEntry(ItemLootEntry.builder(Items.GOLD_INGOT).weight(5).acceptFunction(SetCount.builder(RandomValueRange.of(1, 2))))
                            .addEntry(ItemLootEntry.builder(Items.IRON_INGOT).weight(15).acceptFunction(SetCount.builder(RandomValueRange.of(1, 3))))
                            .addEntry(ItemLootEntry.builder(Items.DIAMOND).weight(2))
                            .addEntry(ItemLootEntry.builder(Items.GLOWSTONE_DUST).weight(8).acceptFunction(SetCount.builder(RandomValueRange.of(1, 3))))
                            .addEntry(ItemLootEntry.builder(Items.EMERALD).weight(1))
                            .addEntry(ItemLootEntry.builder(Items.ENDER_PEARL).weight(2))
                    )
        );
    }
}
