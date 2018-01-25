/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.ImmutableSet;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootTableUtil
 * Created by HellFirePvP
 * Date: 01.08.2016 / 19:29
 */
public class LootTableUtil {

    public static final ResourceLocation LOOT_TABLE_SHRINE = new ResourceLocation(AstralSorcery.MODID.toLowerCase(), "chest_shrine");

    private static final ImmutableSet<ResourceLocation> constellationPaperTables = ImmutableSet.of(LootTableList.CHESTS_STRONGHOLD_LIBRARY, LootTableList.CHESTS_ABANDONED_MINESHAFT, LootTableList.CHESTS_JUNGLE_TEMPLE, LootTableList.CHESTS_DESERT_PYRAMID, LootTableList.CHESTS_IGLOO_CHEST);

    public static void initLootTable() {
        LootTableList.register(LOOT_TABLE_SHRINE);
    }

    @SubscribeEvent
    public void onLootLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        if(constellationPaperTables.contains(name)) {
            event.getTable().getPool("main").addEntry(new LootEntryItem(ItemsAS.constellationPaper, Config.constellationPaperRarity, Config.constellationPaperQuality,
                    new LootFunction[0], new LootCondition[0], "astralsorcery:constellation_paper"));
        }
    }

}
