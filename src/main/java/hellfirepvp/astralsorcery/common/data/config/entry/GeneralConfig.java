/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import com.google.common.base.Predicates;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GeneralConfig
 * Created by HellFirePvP
 * Date: 20.04.2019 / 10:10
 */
public class GeneralConfig extends ConfigEntry {

    public static final GeneralConfig CONFIG = new GeneralConfig();

    public ForgeConfigSpec.IntValue dayLength;

    public ForgeConfigSpec.BooleanValue giveJournalOnJoin;
    public ForgeConfigSpec.BooleanValue mobSpawningDenyAllTypes;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> modidOreBlacklist;

    private GeneralConfig() {
        super("general");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        dayLength = cfgBuilder
                .comment("Defines the length of a day (both daytime & nighttime obviously) for the mod's internal logic. NOTE: This does NOT CHANGE HOW LONG A DAY IN MC IS! It is only to provide potential compatibility for mods that do provide such functionality.")
                .translation(translationKey("dayLength"))
                .defineInRange("dayLength", 24000, 1000, 400_000);

        giveJournalOnJoin = cfgBuilder
                .comment("If set to 'true', the player will receive an AstralSorcery Journal when they join the server for the first time.")
                .translation(translationKey("giveJournalOnJoin"))
                .define("giveJournalOnJoin", true);

        mobSpawningDenyAllTypes = cfgBuilder
                .comment("If set to 'true' anything that prevents mobspawning !by this mod!, will also prevent EVERY natural mobspawning of any mobtype. When set to 'false' it'll only stop monsters of type 'MONSTER' from spawning.")
                .translation(translationKey("mobSpawningDenyAllTypes"))
                .define("mobSpawningDenyAllTypes", false);

        modidOreBlacklist = cfgBuilder
                .comment("Features generating random ores in AstralSorcery will not spawn ores from mods listed here.")
                .translation(translationKey("modidOreBlacklist"))
                .defineList("modidOreBlacklist", Arrays.asList("techreborn", "gregtech"), Predicates.alwaysTrue());
    }

}
