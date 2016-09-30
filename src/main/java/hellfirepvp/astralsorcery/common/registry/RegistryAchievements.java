package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

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
    public static Achievement achvBuildTelescope;
    public static Achievement achvDiscoverConstellation;

    public static AchievementPage achievementPageAstralSorcery;

    public static void init() {
        achvRockCrystal = new Achievement("achievement.as.minerockcrystal", "astralsorcery.minerockcrystal", -1, 3,
                ItemsAS.rockCrystal, null);
        achvCelestialCrystal = new Achievement("achievement.as.celestialcrystal", "astralsorcery.celestialcrystal", -2, 1,
                ItemsAS.celestialCrystal, achvRockCrystal);

        achvBuildTelescope = new Achievement("achievement.as.buildtelescope", "astralsorcery.buildtelescope", 1, 1,
                new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.TELESCOPE.getMeta()), null);
        achvDiscoverConstellation = new Achievement("achievement.as.seeconstellation", "astralsorcery.seeconstellation", 1, -1,
                new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.TELESCOPE.getMeta()), achvBuildTelescope);

        achvCelestialCrystal.setSpecial();
        achvDiscoverConstellation.setSpecial();

        achvRockCrystal.registerStat();
        achvCelestialCrystal.registerStat();
        achvBuildTelescope.registerStat();
        achvDiscoverConstellation.registerStat();

        achievementPageAstralSorcery = new AchievementPage("Astral Sorcery",
                achvRockCrystal, achvCelestialCrystal, achvBuildTelescope, achvDiscoverConstellation);

        AchievementPage.registerAchievementPage(achievementPageAstralSorcery);
    }

}
