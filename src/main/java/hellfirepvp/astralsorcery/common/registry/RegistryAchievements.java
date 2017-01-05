/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.UniversalBucket;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryAchievements
 * Created by HellFirePvP
 * Date: 13.09.2016 / 16:07
 */
public class RegistryAchievements {

    public static Achievement achvRockCrystal;
    public static Achievement achvCelestialCrystal;
    public static Achievement achvBuildHandTelescope;
    public static Achievement achvDiscoverConstellation;
    public static Achievement achvBuildActTelescope;
    public static Achievement achvLiqStarlight;
    public static Achievement achvPlayerAttunement;

    public static AchievementPage achievementPageAstralSorcery;

    public static void init() {
        achvRockCrystal = new Achievement("achievement.as.minerockcrystal", "astralsorcery.minerockcrystal", -1, 3,
                ItemsAS.rockCrystal, null);
        achvCelestialCrystal = new Achievement("achievement.as.celestialcrystal", "astralsorcery.celestialcrystal", -2, 1,
                ItemsAS.celestialCrystal, achvRockCrystal);
        achvLiqStarlight = new Achievement("achievement.as.liquidstarlight", "astralsorcery.liquidstarlight", 0, 0,
                UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, BlocksAS.fluidLiquidStarlight), achvRockCrystal);
        achvBuildHandTelescope = new Achievement("achievement.as.buildtelescope.1", "astralsorcery.buildtelescope.1", 2, 1,
                //new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.TELESCOPE.getMeta()), null);
                ItemsAS.handTelescope, null);
        achvDiscoverConstellation = new Achievement("achievement.as.seeconstellation", "astralsorcery.seeconstellation", 2, -1,
                //new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.TELESCOPE.getMeta()), achvBuildHandTelescope);
                BlockMachine.MachineType.TELESCOPE.asStack(), achvBuildHandTelescope);
        achvBuildActTelescope = new Achievement("achievement.as.buildtelescope.2", "astralsorcery.buildtelescope.2", 4, -2,
                BlockMachine.MachineType.TELESCOPE.asStack(), achvDiscoverConstellation);
        achvPlayerAttunement = new Achievement("achievement.as.playerAttunement", "astralsorcery.playerAttunement", 1, -3,
                BlocksAS.attunementAltar, achvDiscoverConstellation);

        achvCelestialCrystal.setSpecial();
        achvLiqStarlight.setSpecial();
        achvDiscoverConstellation.setSpecial();
        achvPlayerAttunement.setSpecial();

        achvRockCrystal.registerStat();
        achvCelestialCrystal.registerStat();
        achvBuildHandTelescope.registerStat();
        achvDiscoverConstellation.registerStat();
        achvBuildActTelescope.registerStat();
        achvLiqStarlight.registerStat();
        achvPlayerAttunement.registerStat();

        achievementPageAstralSorcery = new AchievementPage("Astral Sorcery",
                achvRockCrystal, achvCelestialCrystal, achvBuildHandTelescope,
                achvDiscoverConstellation, achvBuildActTelescope, achvLiqStarlight,
                achvPlayerAttunement);

        AchievementPage.registerAchievementPage(achievementPageAstralSorcery);
    }

}
