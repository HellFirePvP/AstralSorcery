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

    public ForgeConfigSpec.BooleanValue chargedToolsRevert;
    public ForgeConfigSpec.IntValue chargedToolsRevertStart;
    public ForgeConfigSpec.IntValue chargedToolsRevertChance;

    private ToolsConfig() {
        super("tools");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        capeChaosResistance = cfgBuilder
                .comment("Sets the amount of damage reduction a player gets when being hit by a DE chaos-damage-related damagetype.")
                .translation(translationKey("capeChaosResistance"))
                .defineInRange("capeChaosResistance", 0.8, 0, 1);

        chargedToolsRevert = cfgBuilder
                .comment("If this is set to true, charged crystals tools can revert back to their inert state.")
                .translation(translationKey("chargedToolsRevert"))
                .define("chargedToolsRevert", true);

        chargedToolsRevertStart = cfgBuilder
                .comment("Defines the minimum uses a user at least gets before it's trying to revert to an inert crystal tool.")
                .translation(translationKey("chargedToolsRevertStart"))
                .defineInRange("chargedToolsRevertStart", 40, 0, Integer.MAX_VALUE - 1);

        chargedToolsRevertChance = cfgBuilder
                .comment("After 'chargedCrystalToolsRevertStart' uses, it will random.nextInt(chance) == 0 try and see if the tool gets reverted to its inert crystal tool.")
                .translation(translationKey("chargedToolsRevertChance"))
                .defineInRange("chargedToolsRevertChance", 80, 1, Integer.MAX_VALUE >> 1);
    }

}
