/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenConfig
 * Created by HellFirePvP
 * Date: 20.04.2019 / 15:58
 */
public class WorldGenConfig extends ConfigEntry {

    public static final WorldGenConfig CONFIG = new WorldGenConfig();

    public ForgeConfigSpec.BooleanValue respectIdealDistances;
    public ForgeConfigSpec.BooleanValue flatWorldgen;
    public ForgeConfigSpec.BooleanValue enableRetrogen;
    public ForgeConfigSpec.ConfigValue<List<? extends Integer>> dimensionWorldgenWhitelist;

    private WorldGenConfig() {
        super("worldgen");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        respectIdealDistances = cfgBuilder
                .comment("If this is set to true, the world generator will try and spawn structures more evenly distributed by their 'ideal' distance set in their config entries. WARNING: might add additional worldgen time.")
                .translation(translationKey("respectIdealDistances"))
                .define("respectIdealDistances", true);

        flatWorldgen = cfgBuilder
                .comment("By default, Astral Sorcery does not generate structures or ores in Superflat worlds. If, for some reason, you wish to enable generation of structures and ores in a Super-Flat world, then set this value to true.")
                .translation(translationKey("flatWorldgen"))
                .define("flatWorldgen", false);

        enableRetrogen = cfgBuilder
                .comment("WARNING: Setting this to true, will check on every chunk load if the chunk has been generated depending on the current AstralSorcery version. If the chunk was then generated with an older version, the mod will try and do the worldgen that's needed from the last recorded version to the current version. DO NOT ENABLE THIS FEATURE UNLESS SPECIFICALLY REQUIRED. It might/will slow down chunk loading.")
                .translation(translationKey("enableRetrogen"))
                .define("enableRetrogen", false);

        dimensionWorldgenWhitelist = cfgBuilder
                .comment("Astral Sorcery worldgen will only run in Dimension ID's listed here.")
                .translation(translationKey("dimensionWorldgenWhitelist"))
                .defineList("dimensionWorldgenWhitelist", Lists.newArrayList(0), Predicates.alwaysTrue());
    }

}
