/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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

    public ForgeConfigSpec.DoubleValue illuminationWandCost;
    public ForgeConfigSpec.DoubleValue architectWandCost;
    public ForgeConfigSpec.DoubleValue exchangeWandCost;
    public ForgeConfigSpec.DoubleValue grappleWandCost;
    public ForgeConfigSpec.IntValue exchangeWandMaxHardness;

    public ForgeConfigSpec.DoubleValue evorsioWandEffectChance;
    public ForgeConfigSpec.IntValue discidiaWandStackCap;
    public ForgeConfigSpec.DoubleValue discidiaWandStackMultiplier;

    public ForgeConfigSpec.DoubleValue swordSharpenMultiplier;
    public ForgeConfigSpec.BooleanValue rockCrystalOreSilktouch;
    public ForgeConfigSpec.DoubleValue capeChaosResistance;

    public ForgeConfigSpec.BooleanValue chargedToolsRevert;
    public ForgeConfigSpec.IntValue chargedToolsRevertStart;
    public ForgeConfigSpec.IntValue chargedToolsRevertChance;

    public ForgeConfigSpec.IntValue sextantSearchRadius;

    private ToolsConfig() {
        super("tools");

        newSubSection(new WandsConfig());
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        swordSharpenMultiplier = cfgBuilder
                .comment("Defines how much the 'sharpened' modifier increases the damage of the sword if applied. Config value is in percent.")
                .translation(translationKey("swordSharpenMultiplier"))
                .defineInRange("swordSharpenMultiplier", 0.1, 0.0, 10_000);

        rockCrystalOreSilktouch = cfgBuilder
                .comment("If this is set to true, RockCrystal ore may be silk-touch harvested by a player.")
                .translation(translationKey("rockCrystalOreSilktouch"))
                .define("rockCrystalOreSilktouch", false);

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

        sextantSearchRadius = cfgBuilder
                .comment("Defines the maximum radius the sextant will look for its targets")
                .translation(translationKey("sextantSearchRadius"))
                .defineInRange("sextantSearchRadius", 2048, 128, 65536);
    }

    private class WandsConfig extends ConfigEntry {

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

            evorsioWandEffectChance = cfgBuilder
                    .comment("Defines the chance per mined block that the effect for holding an evorsio attuned resonating wand will fire.")
                    .translation(translationKey("evorsioWandEffectChance"))
                    .defineInRange("evorsioWandEffectChance", 0.8, 0, 1);

            discidiaWandStackCap = cfgBuilder
                    .comment("Defines the amount of stacks you have to get against the same mob until you reach 100% of the damage multiplier.")
                    .translation(translationKey("discidiaWandStackCap"))
                    .defineInRange("discidiaWandStackCap", 10, 1, 200);

            discidiaWandStackMultiplier = cfgBuilder
                    .comment("Defines the additional damage multiplier gradually increased by gaining attack-stacks against a mob. (Applied multiplier = damage * 1 + (thisConfigOption * (currentStacks / maxStacks)) )")
                    .translation(translationKey("discidiaWandStackMultiplier"))
                    .defineInRange("discidiaWandStackMultiplier", 1.0, 0, 200);
        }

    }

}
