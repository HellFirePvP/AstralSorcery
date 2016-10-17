package hellfirepvp.astralsorcery.common.data.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Config
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:14
 */
public class Config {

    private static Configuration latestConfig;

    //public static boolean stopOnIllegalState = true;
    public static int crystalDensity = 15;
    public static int aquamarineAmount = 15;
    public static int marbleAmount = 4, marbleVeinSize = 20;
    public static int shrineGenerationChance = 150;
    public static boolean generateShrines = true;
    public static int constellationPaperRarity = 10, constellationPaperQuality = 2;

    public static boolean clientPreloadTextures = true;

    //Also has a squared field to provide slightly faster rendering.
    public static int maxEffectRenderDistance = 64, maxEffectRenderDistanceSq;

    private Config() {}

    public static void load(File file) {
        latestConfig = new Configuration(file);
        latestConfig.load();
        loadData();
        latestConfig.save();
    }

    private static void loadData() {
        //stopOnIllegalState = latestConfig.getBoolean("stopOnIllegalState", "general", Boolean.TRUE, "If this is set to 'true' the server or client will exit the game with a crash in case it encounters a state that might lead to severe issues but doesn't actually crash the server/client. If this is set to 'false' it will only print a warning in the console.");

        maxEffectRenderDistance = latestConfig.getInt("maxEffectRenderDistance", "rendering", 64, 1, 512, "Defines how close to the position of a particle/floating texture you have to be in order for it to render.");
        maxEffectRenderDistanceSq = maxEffectRenderDistance * maxEffectRenderDistance;
        clientPreloadTextures = latestConfig.getBoolean("preloadTextures", "rendering", true, "If set to 'true' the mod will preload most of the bigger textures during postInit. This provides a more fluent gameplay experience (as it doesn't need to load the textures when they're first needed), but increases loadtime.");

        //rand(crystalDensity) == 0 chance per chunk.
        crystalDensity = latestConfig.getInt("crystalDensity", "worldgen", 15, 0, 40, "Defines how frequently rock-crystals will spawn underground. The lower the number, the more frequent crystals will spawn. (onWorldGen: random.nextInt(crystalDensity) == 0 -> gen 1 ore in that chunk)");
        marbleAmount = latestConfig.getInt("generateMarbleAmount", "worldgen", 4, 0, 32, "Defines how many marble veins are generated per chunk.");
        marbleVeinSize = latestConfig.getInt("generateMarbleVeinSize", "worldgen", 20, 1, 32, "Defines how big generated marble veins are.");
        aquamarineAmount = latestConfig.getInt("generateAquamarineAmount", "worldgen", 15, 0, 32, "Defines how many aquamarine ores it'll attempt to generate in per chunk");
        generateShrines = latestConfig.getBoolean("generateShrines", "worldgen", true, "Set to true to enable shrine generation.");
        shrineGenerationChance = latestConfig.getInt("shrineGenerationChance", "worldgen", 150, 1, 200000, "Defines the chance if the worldgenerator will check a position in a chunk to spawn the shrine in. (onWorldGen: random.nextInt(shrineGenerationChance) == 0 -> check if generation is possible at 1 random pos in chunk.)");
        constellationPaperRarity = latestConfig.getInt("constellationPaperRarity", "worldgen", 10, 1, 128, "Defines the rarity of the constellation paper item in loot chests.");
        constellationPaperQuality = latestConfig.getInt("constellationPaperQuality", "worldgen", 2, 1, 128, "Defines the quality of the constellation paper item in loot chests.");
    }

}
