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
    public static Achievement achvBuildTelescope;
    public static Achievement achvDiscoverConstellation;

    public static AchievementPage achievementPageAstralSorcery;

    public static void init() {
        achvRockCrystal = new Achievement("achievement.as.minerockcrystal", "astralsorcery.minerockcrystal", 0, 3,
                ItemsAS.rockCrystal, null);
        achvBuildTelescope = new Achievement("achievement.as.buildtelescope", "astralsorcery.buildtelescope", 1, 1,
                new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.TELESCOPE.getMeta()), achvRockCrystal);
        achvDiscoverConstellation = new Achievement("achievement.as.seeconstellation", "astralsorcery.seeconstellation", 1, -1,
                new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.TELESCOPE.getMeta()), achvBuildTelescope);

        achvRockCrystal.setSpecial();
        achvDiscoverConstellation.setSpecial();

        achvRockCrystal.registerStat();
        achvBuildTelescope.registerStat();
        achvDiscoverConstellation.registerStat();

        achievementPageAstralSorcery = new AchievementPage("Astral Sorcery",
                achvRockCrystal, achvBuildTelescope, achvDiscoverConstellation);

        AchievementPage.registerAchievementPage(achievementPageAstralSorcery);
    }

}
