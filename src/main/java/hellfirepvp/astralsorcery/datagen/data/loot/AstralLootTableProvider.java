/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.loot;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralLootTableProvider
 * Created by HellFirePvP
 * Date: 06.03.2020 / 21:42
 */
public final class AstralLootTableProvider extends LootTableProvider {

    public AstralLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return Lists.newArrayList(
                Pair.of(BlockLootTableProvider::new, LootParameterSets.BLOCK),
                Pair.of(EntityLootTableProvider::new, LootParameterSets.ENTITY),
                Pair.of(ChestLootTableProvider::new, LootParameterSets.CHEST),
                Pair.of(GameplayLootTableProvider::new, LootParameterSets.GIFT)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> tables, ValidationTracker tracker) {
        tables.forEach((key, table) -> LootTableManager.validateLootTable(tracker, key, table));
    }
}
