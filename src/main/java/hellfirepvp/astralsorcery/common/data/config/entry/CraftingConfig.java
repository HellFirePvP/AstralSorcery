/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.util.block.BlockStateHelper;
import net.minecraft.block.BlockState;
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

    public ForgeConfigSpec.BooleanValue liquidStarlightCrystalGrowth;
    public ForgeConfigSpec.BooleanValue liquidStarlightFormCelestialCrystalCluster;
    public ForgeConfigSpec.BooleanValue liquidStarlightFormGemCrystalCluster;
    public ForgeConfigSpec.BooleanValue liquidStarlightDropInfusedWood;
    public ForgeConfigSpec.BooleanValue liquidStarlightMergeCrystals;

    public ForgeConfigSpec.BooleanValue liquidStarlightInteractionAquamarine;
    public ForgeConfigSpec.BooleanValue liquidStarlightInteractionSand;
    public ForgeConfigSpec.BooleanValue liquidStarlightInteractionIce;

    public ForgeConfigSpec.ConfigValue<String> starmetalRevertState;

    private CraftingConfig() {
        super("crafting");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        liquidStarlightCrystalGrowth = cfgBuilder
                .comment("Set this to false to disable Rock/Celestial Crystal growing in liquid starlight.")
                .translation(translationKey("liquidStarlightCrystalGrowth"))
                .define("liquidStarlightCrystalGrowth", true);

        liquidStarlightFormCelestialCrystalCluster = cfgBuilder
                .comment("Set this to false to disable crystal + stardust -> Celestial Crystal cluster forming")
                .translation(translationKey("liquidStarlightFormCelestialCrystalCluster"))
                .define("liquidStarlightFormCelestialCrystalCluster", true);

        liquidStarlightFormGemCrystalCluster = cfgBuilder
                .comment("Set this to false to disable crystal + illumination powder -> Gem Crystal cluster forming")
                .translation(translationKey("liquidStarlightFormGemCrystalCluster"))
                .define("liquidStarlightFormGemCrystalCluster", true);

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

        liquidStarlightMergeCrystals = cfgBuilder
                .comment("Set this to false to disable the functionality that two crystals can merge and combine stats when thrown into liquid starlight.")
                .translation(translationKey("liquidStarlightMergeCrystals"))
                .define("liquidStarlightMergeCrystals", true);

        starmetalRevertState = cfgBuilder
                .comment("Defines the state the starmetal ore will revert into when used up by a celestial crystal cluster. Obtain a valid state-string via '/astralsorcery serialize look' and look at the block you want to get. (Chat-Message can be copied)")
                .translation(translationKey("starmetalRevertState"))
                .define("starmetalRevertState", "minecraft:iron_ore");
    }

    public BlockState getStarmetalRevertBlockState() {
        return BlockStateHelper.deserialize(starmetalRevertState.get());
    }
}
