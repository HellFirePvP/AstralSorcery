/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.server;

import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LootTableConfig
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LootTableConfig extends ConfigEntry {

    public static final LootTableConfig CONFIG = new LootTableConfig();

    public ModConfigSpec.ConfigValue<List<? extends String>> constellationPaperLootTables;

    public ModConfigSpec.ConfigValue<List<? extends String>> artifactShardLootTables;

    private LootTableConfig() {
        super("loot");
    }

    @Override
    public void createEntries(ModConfigSpec.Builder cfgBuilder) {
        this.constellationPaperLootTables = cfgBuilder
                .comment("Loot tables that get constellation paper added to them on load")
                .translation(translationKey("constellationPaperLootTables"))
                .defineList("constellationPaperLootTables", this.getDefaultList(), () -> "minecraft:loot_table", s -> s instanceof String str && ResourceLocation.tryParse(str) != null);
        this.artifactShardLootTables = cfgBuilder
                .comment("Loot tables that the artifact shard loot table item can generate. Temporary use until a proper feature is added")
                .translation(translationKey("artifactShardLootTables"))
                .defineList("artifactShardLootTables", this.getDefaultArtifactShardList(), () -> "minecraft:loot_table", s -> s instanceof String str && ResourceLocation.tryParse(str) != null);
    }

    private List<String> getDefaultList() {
        return Stream.of(
                BuiltInLootTables.STRONGHOLD_LIBRARY,
                BuiltInLootTables.STRONGHOLD_CORRIDOR,
                BuiltInLootTables.DESERT_PYRAMID,
                BuiltInLootTables.JUNGLE_TEMPLE,
                BuiltInLootTables.WOODLAND_MANSION,
                BuiltInLootTables.ABANDONED_MINESHAFT,
                BuiltInLootTables.BURIED_TREASURE,
                BuiltInLootTables.SHIPWRECK_MAP,
                BuiltInLootTables.END_CITY_TREASURE,
                BuiltInLootTables.SIMPLE_DUNGEON
        ).map(ResourceKey::location).map(ResourceLocation::toString).toList();
    }

    private List<String> getDefaultArtifactShardList() {
        return Stream.of(
                BuiltInLootTables.DESERT_PYRAMID,
                BuiltInLootTables.JUNGLE_TEMPLE,
                BuiltInLootTables.WOODLAND_MANSION,
                BuiltInLootTables.ABANDONED_MINESHAFT,
                BuiltInLootTables.BURIED_TREASURE,
                BuiltInLootTables.SHIPWRECK_SUPPLY
        ).map(ResourceKey::location).map(ResourceLocation::toString).toList();
    }

    public boolean canAddConstellationPaper(LootTable table) {
        String id = table.getLootTableId().toString();
        return this.constellationPaperLootTables.get().contains(id);
    }

    public ResourceLocation getRandomArtifactShardLootTable(RandomSource rand) {
        List<? extends String> list = this.artifactShardLootTables.get();
        if (list.isEmpty()) {
            return BuiltInLootTables.DESERT_PYRAMID.location();
        }
        return MiscUtil.getRandomEntry(list, rand).map(ResourceLocation::parse).orElse(BuiltInLootTables.DESERT_PYRAMID.location());
    }
}
