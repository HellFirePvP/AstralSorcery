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
 * Class: CraftingConfig
 * Created by HellFirePvP
 * Date: 20.04.2019 / 14:02
 */
public class CraftingConfig extends ConfigEntry {

    public static final CraftingConfig CONFIG = new CraftingConfig();

    public ForgeConfigSpec.BooleanValue grindstoneDustRecipes;

    public ForgeConfigSpec.BooleanValue liquidStarlightCrystalGrowth;
    public ForgeConfigSpec.BooleanValue liquidStarlightFormCelestialCrystalCluster;
    public ForgeConfigSpec.BooleanValue liquidStarlightCrystalToolGrowth;
    public ForgeConfigSpec.BooleanValue liquidStarlightCrystalDuplication;

    public ForgeConfigSpec.BooleanValue liquidStarlightInteractionAquamarine;
    public ForgeConfigSpec.BooleanValue liquidStarlightInteractionSand;
    public ForgeConfigSpec.BooleanValue liquidStarlightInteractionIce;

    public ForgeConfigSpec.BooleanValue liquidStarlightDropInfusedWood;

    private CraftingConfig() {
        super("crafting");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        grindstoneDustRecipes = cfgBuilder
                .comment("Set this to false to prevent the lookup and registration of oreblock -> ore dust recipes on the grindstone.")
                .translation(translationKey("grindstoneDustRecipes"))
                .define("grindstoneDustRecipes", true);

        liquidStarlightCrystalGrowth = cfgBuilder
                .comment("Set this to false to disable Rock/Celestial Crystal growing in liquid starlight.")
                .translation(translationKey("liquidStarlightCrystalGrowth"))
                .define("liquidStarlightCrystalGrowth", true);

        liquidStarlightFormCelestialCrystalCluster = cfgBuilder
                .comment("Set this to false to disable crystal + stardust -> Celestial Crystal cluster forming")
                .translation(translationKey("liquidStarlightFormCelestialCrystalCluster"))
                .define("liquidStarlightFormCelestialCrystalCluster", true);

        liquidStarlightCrystalToolGrowth = cfgBuilder
                .comment("Set this to false to disable Crystal Tool growth in liquid starlight")
                .translation(translationKey("liquidStarlightCrystalToolGrowth"))
                .define("liquidStarlightCrystalToolGrowth", true);

        liquidStarlightCrystalDuplication = cfgBuilder
                .comment("Set this to false to disable the chance to get a 2nd crystal when growing a max-sized one in liquid starlight.")
                .translation(translationKey("liquidStarlightCrystalDuplication"))
                .define("liquidStarlightCrystalDuplication", true);

        liquidStarlightInteractionAquamarine = cfgBuilder
                .comment("Set this to false to disable that liquid starlight + lava occasionally/rarely produces aquamarine shale instead of sand.")
                .translation(translationKey("liquidStarlightInteractionAquamarine"))
                .define("liquidStarlightInteractionAquamarine", true);

        liquidStarlightInteractionSand = cfgBuilder
                .comment("Set this to false to disable that liquid starlight + lava produces sand.")
                .translation(translationKey("liquidStarlightInteractionSand"))
                .define("liquidStarlightInteractionSand", true);

        liquidStarlightInteractionIce = cfgBuilder
                .comment("Set this to false to disable that liquid starlight + water produces ice.")
                .translation(translationKey("liquidStarlightInteractionIce"))
                .define("liquidStarlightInteractionIce", true);

        liquidStarlightDropInfusedWood = cfgBuilder
                .comment("Set this to false to disable the functionality that wood logs will be converted to infused wood when thrown into liquid starlight.")
                .translation(translationKey("liquidStarlightDropInfusedWood"))
                .define("liquidStarlightDropInfusedWood", true);
    }
}
