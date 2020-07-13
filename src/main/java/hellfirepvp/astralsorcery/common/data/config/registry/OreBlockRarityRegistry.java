/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.OreBlockRarityEntry;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreBlockRarityRegistry
 * Created by HellFirePvP
 * Date: 01.09.2019 / 00:11
 */
public class OreBlockRarityRegistry extends ConfigDataAdapter<OreBlockRarityEntry> {

    public static final OreBlockRarityRegistry STONE_ENRICHMENT = new OreBlockRarityRegistry("perk_stone_enrichment_ore");
    public static final OreBlockRarityRegistry MINERALIS_RITUAL = new OreBlockRarityRegistry("mineralis_ritual_ore");

    private final String fileName;

    private OreBlockRarityRegistry(String fileName) {
        this.fileName = fileName;
    }

    @Nullable
    public Block getRandomBlock(Random rand) {
        List<OreBlockRarityEntry> entries = this.getConfiguredValues();
        Set<OreBlockRarityEntry> visitedEntires = new HashSet<>();

        while (visitedEntires.size() < entries.size()) {
            OreBlockRarityEntry entry = MiscUtils.getWeightedRandomEntry(entries.stream()
                    .filter(visitedEntires::contains)
                    .collect(Collectors.toList()), rand, OreBlockRarityEntry::getWeight);

            if (entry != null) {
                visitedEntires.add(entry);
                Block b = entry.getRandomBlock(rand);
                if (b != null) {
                    return b;
                }
            } else {
                return null; //Invalid state?
            }
        }
        return null;
    }

    @Override
    public List<OreBlockRarityEntry> getDefaultValues() {
        return Lists.newArrayList(
                new OreBlockRarityEntry(Tags.Blocks.ORES_COAL,     5200),
                new OreBlockRarityEntry(Tags.Blocks.ORES_IRON,     2500),
                new OreBlockRarityEntry(Tags.Blocks.ORES_GOLD,      550),
                new OreBlockRarityEntry(Tags.Blocks.ORES_LAPIS,     360),
                new OreBlockRarityEntry(Tags.Blocks.ORES_REDSTONE,  700),
                new OreBlockRarityEntry(Tags.Blocks.ORES_DIAMOND,   120),
                new OreBlockRarityEntry(Tags.Blocks.ORES_EMERALD,   100)
        );
    }

    @Override
    public String getSectionName() {
        return this.fileName;
    }

    @Override
    public String getCommentDescription() {
        return "Format: '<tagName>;<integerWeight>' Defines random-weighted ore-selection data. Define block-tags to select from here with associated weight. Specific mods can be blacklisted in the general AstralSorcery config in 'modidOreBlacklist'.";
    }

    @Override
    public String getTranslationKey() {
        return translationKey("data");
    }

    @Override
    public Predicate<Object> getValidator() {
        return obj -> obj instanceof String;
    }

    @Nullable
    @Override
    public OreBlockRarityEntry deserialize(String string) throws IllegalArgumentException {
        return OreBlockRarityEntry.deserialize(string);
    }
}
