/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataAdapter;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.FluidRarityEntry;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidRarityRegistry
 * Created by HellFirePvP
 * Date: 20.04.2019 / 10:57
 */
public class FluidRarityRegistry extends ConfigDataAdapter<FluidRarityEntry> {

    public static final FluidRarityRegistry INSTANCE = new FluidRarityRegistry();

    private FluidRarityRegistry() {}

    @Override
    public List<FluidRarityEntry> getDefaultValues() {
        return Lists.newArrayList(
                new FluidRarityEntry(Mods.MINECRAFT.key("water"), 14000, Integer.MAX_VALUE, Integer.MAX_VALUE),
                new FluidRarityEntry(Mods.MINECRAFT.key("lava"), 7500, 4_000_000, 1_000_000)
        );
    }

    @Nullable
    @Override
    public synchronized FluidRarityEntry getRandomValue(Random rand) {
        return MiscUtils.getWeightedRandomEntry(this.getConfiguredValues(), rand, FluidRarityEntry::getRarity);
    }

    @Override
    public String getSectionName() {
        return "fluid_rarities";
    }

    @Override
    public String getCommentDescription() {
        return "Defines fluid-rarities and amounts for the evershifting fountain's neromantic prime. The lower the relative rarity, the more rare the fluid. " +
                "Format: <FluidName>;<guaranteedMbAmount>;<additionalRandomMbAmount>;<rarity>";
    }

    @Override
    public String getTranslationKey() {
        return translationKey("data");
    }

    @Override
    public FluidRarityEntry deserialize(String string) throws IllegalArgumentException {
        return FluidRarityEntry.deserialize(string);
    }

    @Override
    public Predicate<Object> getValidator() {
        return obj -> obj instanceof String;
    }
}
