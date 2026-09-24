/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.config.server.LootTableConfig;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.LootTableLoadEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LootEventHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LootEventHandler {

    public static void attachListeners(IEventBus bus) {
        bus.addListener(LootEventHandler::onLootLoad);
    }

    private static void onLootLoad(LootTableLoadEvent event) {
        LootTable table = event.getTable();
        if (LootTableConfig.CONFIG.canAddConstellationPaper(table)) {
            table.addPool(LootPool.lootPool()
                    .setRolls(BinomialDistributionGenerator.binomial(1, 0.33F))
                    .add(LootItem.lootTableItem(ItemsAS.CONSTELLATION_PAPER))
                    .name(AstralSorcery.key("configured_constellation_paper_pool").toString())
                    .build());
        }
    }
}
