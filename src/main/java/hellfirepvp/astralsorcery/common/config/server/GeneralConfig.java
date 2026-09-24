/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.server;

import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GeneralConfig
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GeneralConfig extends ConfigEntry {

    public static final GeneralConfig CONFIG = new GeneralConfig();

    public ModConfigSpec.IntValue dayLength;
    public ModConfigSpec.BooleanValue giveTomeOnJoin;

    private GeneralConfig() {
        super("general");
    }

    @Override
    public void createEntries(ModConfigSpec.Builder cfgBuilder) {
        dayLength = cfgBuilder
                .comment("Defines the length of a day (both daytime & nighttime obviously) for the mod's internal logic. NOTE: This does NOT CHANGE HOW LONG A DAY IN MC IS! It is only to provide potential compatibility for mods that do provide such functionality.")
                .translation(translationKey("dayLength"))
                .defineInRange("dayLength", 24000, 1000, 400_000);

        giveTomeOnJoin = cfgBuilder
                .comment("If set to 'true', the player will receive an AstralSorcery Tome when they join the server for the first time.")
                .translation(translationKey("giveTomeOnJoin"))
                .define("giveTomeOnJoin", true);
    }
}
