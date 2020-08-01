/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkConfig
 * Created by HellFirePvP
 * Date: 02.06.2019 / 02:08
 */
public class PerkConfig extends ConfigEntry {

    public static final PerkConfig CONFIG = new PerkConfig();

    public ForgeConfigSpec.IntValue perkLevelCap;

    private PerkConfig() {
        super("perks");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        perkLevelCap = cfgBuilder
                .comment("Sets the max level for the perk tree levels.")
                .translation(translationKey("perkLevelCap"))
                .defineInRange("perkLevelCap", 40, 10, 100);
    }

}
