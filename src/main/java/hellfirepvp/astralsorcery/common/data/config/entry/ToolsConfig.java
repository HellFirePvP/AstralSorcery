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
 * Class: ToolsConfig
 * Created by HellFirePvP
 * Date: 20.04.2019 / 13:00
 */
public class ToolsConfig extends ConfigEntry {

    public static final ToolsConfig CONFIG = new ToolsConfig();

    public ForgeConfigSpec.DoubleValue capeChaosResistance;

    private ToolsConfig() {
        super("tools");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        capeChaosResistance = cfgBuilder
                .comment("Sets the amount of damage reduction a player gets when being hit by a DE chaos-damage-related damagetype.")
                .translation(translationKey("capeChaosResistance"))
                .defineInRange("capeChaosResistance", 0.8, 0, 1);
    }

}
