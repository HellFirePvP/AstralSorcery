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
 * Class: WandsConfig
 * Created by HellFirePvP
 * Date: 11.08.2019 / 21:11
 */
public class WandsConfig extends ConfigEntry {

    public static final WandsConfig CONFIG = new WandsConfig();

    public ForgeConfigSpec.DoubleValue illuminationWandCost;
    public ForgeConfigSpec.DoubleValue architectWandCost;
    public ForgeConfigSpec.DoubleValue exchangeWandCost;
    public ForgeConfigSpec.DoubleValue grappleWandCost;

    public ForgeConfigSpec.IntValue exchangeWandMaxHardness;

    private WandsConfig() {
        super("wands");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        illuminationWandCost = cfgBuilder
                .comment("Sets the quick-charge cost for one usage of the illumination wand")
                .translation(translationKey("illuminationWandCost"))
                .defineInRange("illuminationWandCost", 0.5, 0, 1.0);

        architectWandCost = cfgBuilder
                .comment("Sets the quick-charge cost for one usage of the architect wand")
                .translation(translationKey("architectWandCost"))
                .defineInRange("architectWandCost", 0.03, 0.0, 1.0);

        exchangeWandCost = cfgBuilder
                .comment("Sets the quick-charge cost for one usage of the exchange wand")
                .translation(translationKey("exchangeWandCost"))
                .defineInRange("exchangeWandCost", 0.002, 0.0, 1.0);

        grappleWandCost = cfgBuilder
                .comment("Sets the quick-charge cost for one usage of the grapple wand")
                .translation(translationKey("grappleWandCost"))
                .defineInRange("grappleWandCost", 0.7, 0.0, 1.0);

        exchangeWandMaxHardness = cfgBuilder
                .comment("Sets the max. hardness the exchange wand can swap !from!. If the block you're trying to \"mine\" with the conversion wand is higher than this number, it won't work. (-1 to disable this check)")
                .translation(translationKey("exchangeWandMaxHardness"))
                .defineInRange("exchangeWandMaxHardness", -1, -1, 50_000);
    }

}
