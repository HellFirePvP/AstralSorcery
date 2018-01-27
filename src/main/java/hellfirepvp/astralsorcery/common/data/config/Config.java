/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncConfig;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Config
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:14
 */
public class Config {

    //TODO remember to do a configurable itemSword-classname blacklist for sharpening.

    private static Configuration latestConfig;
    public static List<PktSyncConfig.SyncTuple> savedSyncTuples = new LinkedList<>();

    private static File dirConfigurationRegistries;

    //public static boolean stopOnIllegalState = true;
    public static boolean spawnRockCrystalOres = true;
    public static boolean respectIdealDistances = true;
    public static int crystalDensity = 15;
    public static int aquamarineAmount = 64;
    public static int marbleAmount = 4, marbleVeinSize = 20;
    public static int constellationPaperRarity = 10, constellationPaperQuality = 2;

    public static boolean lightProximityAltarRecipe = true;
    public static boolean lightProximityResonatingWandRecipe = true;

    public static boolean clientPreloadTextures = true;
    public static boolean giveJournalFirst = true;
    public static boolean doesMobSpawnDenyDenyEverything = false;
    public static boolean rockCrystalOreSilkTouchHarvestable = false;

    @Sync public static float capeChaosResistance = 0.8F;

    //Attuned wands configs
    @Sync public static float evorsioEffectChance = 0.25F;
    @Sync public static int discidiaStackCap = 10;
    @Sync public static float discidiaStackMultiplier = 1F;

    @Sync public static boolean craftingLiqCrystalGrowth = true;
    @Sync public static boolean craftingLiqCrystalToolGrowth = true;
    @Sync public static boolean craftingLiqCelestialCrystalForm = true;
    @Sync public static boolean canCrystalGrowthYieldDuplicates = true;

    public static boolean liquidStarlightAquamarine = true;
    public static boolean liquidStarlightSand = true;
    public static boolean liquidStarlightIce = true;

    public static boolean enableRetroGen = false;

    //Also has a squared field to provide slightly faster rendering.
    public static int maxEffectRenderDistance = 64, maxEffectRenderDistanceSq;

    public static int particleAmount = 2;

    public static int ambientFlareChance = 20;
    public static boolean flareKillsBats = true;

    public static boolean shouldChargedToolsRevert = true;
    public static int revertStart = 40;
    public static int revertChance = 80;

    @Sync public static double swordSharpMultiplier = 0.1;

    @Sync public static float illuminationWandUseCost = 0.5F;
    @Sync public static float grappleWandUseCost = 0.7F;
    @Sync public static float architectWandUseCost = 0.07F;
    @Sync public static float exchangeWandUseCost = 0.08F;

    public static float exchangeWandMaxHardness = -1;

    public static List<Integer> constellationSkyDimWhitelist = Lists.newArrayList();
    public static List<Integer> weakSkyRendersWhitelist = Lists.newArrayList();
    public static List<String> modidOreGenBlacklist = Lists.newArrayList();
    public static List<Integer> worldGenDimWhitelist = Lists.newArrayList();
    public static boolean performNetworkIntegrityCheck = false;

    private static List<ConfigEntry> dynamicConfigEntries = new LinkedList<>();
    private static List<ConfigDataAdapter<?>> dataAdapters = new LinkedList<>();

    private Config() {}

    public static void load(File file) {
        latestConfig = new Configuration(file);
        latestConfig.load();
        loadData();
        latestConfig.save();
    }

    public static void addDynamicEntry(ConfigEntry entry) {
        if(latestConfig != null) {
            throw new IllegalStateException("Too late to add dynamic configuration entries");
        }
        dynamicConfigEntries.add(entry);
    }

    public static void addDataRegistry(ConfigDataAdapter<?> dataAdapter) {
        for (ConfigDataAdapter<?> cfg : dataAdapters) {
            if(cfg.getDataFileName().equalsIgnoreCase(dataAdapter.getDataFileName())) {
                throw new IllegalArgumentException("Duplicate DataRegistry names! " + cfg.getDataFileName() + " - " + dataAdapter.getDataFileName());
            }
        }
        dataAdapters.add(dataAdapter);
    }

    public static void rebuildClientConfig() {
        try {
            for (PktSyncConfig.SyncTuple tuple : savedSyncTuples) {
                Field field = Config.class.getField(tuple.key);
                field.set(null, tuple.value);
            }
            savedSyncTuples.clear();
        } catch (Throwable exc) {
            AstralSorcery.log.error("[AstralSorcery] Failed to reapply saved client config!");
            throw new RuntimeException(exc);
        }
    }

    public static void loadDataRegistries(File cfgDirectory) {
        File dirAS = new File(cfgDirectory, AstralSorcery.MODID);
        if(!dirAS.exists()) {
            dirAS.mkdirs();
        }
        dirConfigurationRegistries = dirAS;
    }

    public static void loadConfigRegistries(ConfigDataAdapter.LoadPhase phase) {
        for (ConfigDataAdapter<?> cfg : dataAdapters) {
            if(cfg.getLoadPhase() != phase) {
                continue;
            }
            attemptLoad(cfg, new File(dirConfigurationRegistries, cfg.getDataFileName() + ".cfg"));
        }
    }

    private static void attemptLoad(ConfigDataAdapter<?> cfg, File file) {
        String[] out = cfg.serializeDataSet();

        Configuration config = new Configuration(file);
        config.load();
        config.addCustomCategoryComment("data", cfg.getDescription());
        out = config.getStringList("data", "data", out, "");
        for (String str : out) {
            if(cfg.appendDataSet(str) == null) {
                AstralSorcery.log.warn("[AstralSorcery] Skipped Entry '" + str + "' for registry " + cfg.getDataFileName() + "! Invalid format!");
            }
        }
        config.save();
    }

    private static void loadData() {
        giveJournalFirst = latestConfig.getBoolean("giveJournalAtFirstJoin", "general", true, "If set to 'true', the player will receive an AstralSorcery Journal if he joins the server for the first time.");
        doesMobSpawnDenyDenyEverything = latestConfig.getBoolean("doesMobSpawnDenyAllTypes", "general", false, "If set to 'true' anything that prevents mobspawning by this mod, will also prevent EVERY natural mobspawning of any mobtype. When set to 'false' it'll only stop monsters from spawning.");
        swordSharpMultiplier = latestConfig.getFloat("swordSharpenedMultiplier", "general", 0.1F, 0.0F, 10000.0F, "Defines how much the 'sharpened' modifier increases the damage of the sword if applied. Config value is in percent.");
        rockCrystalOreSilkTouchHarvestable = latestConfig.getBoolean("isRockCrystalOreSilkHarvestable", "general", rockCrystalOreSilkTouchHarvestable, "If this is set to true, Rock-Crystal-Ore may be silk-touch harvested by a player.");
        String[] dimWhitelist = latestConfig.getStringList("skySupportedDimensions", "general", new String[] { "0" }, "Whitelist of dimension ID's that will have special sky rendering");
        String[] weakSkyRenders = latestConfig.getStringList("weakSkyRenders", "general", new String[] {}, "IF a dimensionId is listed in 'skySupportedDimensions' you can add it here to keep its sky render, but AS will try to render only constellations on top of its existing sky render.");
        String[] oreModidBlacklist = latestConfig.getStringList("oreGenBlacklist", "general", new String[] { "techreborn" }, "List any number of modid's here and the aevitas perk & mineralis ritual will not spawn ores that originate from any of the mods listed here.");
        modidOreGenBlacklist = Lists.newArrayList(oreModidBlacklist);

        ambientFlareChance = latestConfig.getInt("EntityFlare.ambientspawn", "entities", 20, 0, 200_000, "Defines how common ***ambient*** flares are. the lower the more common. 0 = ambient ones don't appear/disabled.");
        flareKillsBats = latestConfig.getBoolean("EntityFlare.killbats", "entities", true, "If this is set to true, occasionally, a spawned flare will (attempt to) kill bats close to it.");

        lightProximityAltarRecipe = latestConfig.getBoolean("LightProximity-Altar", "recipes", lightProximityAltarRecipe, "If this is set to false, the luminous crafting table recipe that'd require 'light shining at a crafting table' is disabled.");
        lightProximityResonatingWandRecipe = latestConfig.getBoolean("LightProximity-ResonatingWand", "recipes", lightProximityResonatingWandRecipe, "If this is set to false, the resonating wand recipe that'd require 'light shining at a crafting table' is disabled.");

        illuminationWandUseCost = latestConfig.getFloat("wandCost_illumination", "tools", 0.5F, 0.0F, 1.0F, "Sets the quick-charge cost for one usage of the illumination wand");
        architectWandUseCost = latestConfig.getFloat("wandCost_architect", "tools", 0.03F, 0.0F, 1.0F, "Sets the quick-charge cost for one usage of the architect wand");
        exchangeWandUseCost = latestConfig.getFloat("wandCost_exchange", "tools", 0.002F, 0.0F, 1.0F, "Sets the quick-charge cost for one usage of the exchange wand");
        grappleWandUseCost = latestConfig.getFloat("wandCost_grapple", "tools", grappleWandUseCost, 0.0F, 1.0F, "Sets the quick-charge cost for one usage of the grapple wand");

        exchangeWandMaxHardness = latestConfig.getFloat("exchange_wand_max_hardness", "tools", -1, -1, 50000, "Sets the max. hardness the exchange wand can swap !from!. If the block you're trying to \"mine\" with the conversion wand is higher than this number, it won't work. (-1 to disable this check)");

        capeChaosResistance = latestConfig.getFloat("cape_chaosresistance", "tools", capeChaosResistance, 0.0F, 1.0F, "Sets the amount of damage reduction a player gets when being hit by a DE chaos-damage-related damagetype.");

        shouldChargedToolsRevert = latestConfig.getBoolean("chargedCrystalToolsRevert", "tools", shouldChargedToolsRevert, "If this is set to true, charged crystals tools can revert back to their inert state.");
        revertStart = latestConfig.getInt("chargedCrystalToolsRevertStart", "tools", revertStart, 0, Integer.MAX_VALUE - 1, "Defines the minimum uses a user at least gets before it's trying to revert to an inert crystal tool.");
        revertChance = latestConfig.getInt("chargedCrystalToolsRevertChance", "tools", revertChance, 1, Integer.MAX_VALUE, "After 'chargedCrystalToolsRevertStart' uses, it will random.nextInt(chance) == 0 try and see if the tool gets reverted to its inert crystal tool.");

        evorsioEffectChance = latestConfig.getFloat("evorsioAttunedWandEffectChance", "tools", evorsioEffectChance, 0F, 1F, "Defines the chance per mined block that the effect for holding an evorsio attuned resonating wand will fire.");
        discidiaStackCap = latestConfig.getInt("discidiaDamageStackCap", "tools", discidiaStackCap, 1, 200, "Defines the amount of stacks you have to get against the same mob until you reach 100% of the damage multiplier.");
        discidiaStackMultiplier = latestConfig.getFloat("discidiaDamageStackMultipler", "tools", discidiaStackMultiplier, 0F, 200F, "Defines the additional damage multiplier gradually increased by gaining attack-stacks against a mob. (Applied multiplier = damage * 1 + (thisConfigOption * (currentStacks / maxStacks)) )");

        craftingLiqCrystalGrowth = latestConfig.getBoolean("liquidStarlightCrystalGrowth", "crafting", true, "Set this to false to disable Rock/Celestial Crystal growing in liquid starlight.");
        craftingLiqCelestialCrystalForm = latestConfig.getBoolean("liquidStarlightCelestialCrystalCluster", "crafting", true, "Set this to false to disable crystal + stardust -> Celestial Crystal cluster forming");
        craftingLiqCrystalToolGrowth = latestConfig.getBoolean("liquidStarlightCrystalToolGrowth", "crafting", craftingLiqCrystalToolGrowth, "Set this to false to disable Crystal Tool growth in liquid starlight");
        canCrystalGrowthYieldDuplicates = latestConfig.getBoolean("canCrystalGrowthYieldDuplicates", "crafting", canCrystalGrowthYieldDuplicates, "Set this to false to disable the chance to get a 2nd crystal when growing a max-sized one in liquid starlight.");

        liquidStarlightAquamarine = latestConfig.getBoolean("liquidStarlightAquamarine", "crafting", liquidStarlightAquamarine, "Set this to false to disable that liquid starlight + lava occasionally/rarely produces aquamarine shale instead of sand.");
        liquidStarlightSand = latestConfig.getBoolean("liquidStarlightSand", "crafting", liquidStarlightSand, "Set this to false to disable that liquid starlight + lava produces sand.");
        liquidStarlightIce = latestConfig.getBoolean("liquidStarlightIce", "crafting", liquidStarlightIce, "Set this to false to disable that liquid starlight + water produces ice.");

        latestConfig.addCustomCategoryComment("lightnetwork", "Maintenance options for the Starlight network. Use the integrity check when you did a bigger rollback or MC-Edited stuff out of the world. Note that it will only affect worlds that get loaded. So if you edited out something on, for example, dimension -76, be sure to go into that dimension with the maintenance options enabled to properly perform maintenance there.");
        performNetworkIntegrityCheck = latestConfig.getBoolean("performNetworkIntegrityCheck", "lightnetwork", false, "NOTE: ONLY run this once and set it to false again afterwards, nothing will be gained by setting this to true permanently, just longer loading times. When set to true and the server started, this will perform an integrity check over all nodes of the starlight network whenever a world gets loaded, removing invalid ones in the process. This might, depending on network sizes, take a while. It'll leave a message in the console when it's done. After this check has been run, you might need to tear down and rebuild your starlight network in case something doesn't work anymore.");

        maxEffectRenderDistance = latestConfig.getInt("maxEffectRenderDistance", "rendering", 64, 1, 512, "Defines how close to the position of a particle/floating texture you have to be in order for it to render.");
        maxEffectRenderDistanceSq = maxEffectRenderDistance * maxEffectRenderDistance;
        clientPreloadTextures = latestConfig.getBoolean("preloadTextures", "rendering", true, "If set to 'true' the mod will preload most of the bigger textures during postInit. This provides a more fluent gameplay experience (as it doesn't need to load the textures when they're first needed), but increases loadtime.");
        particleAmount = latestConfig.getInt("particleAmount", "rendering", 2, 0, 2, "Sets the amount of particles/effects: 0 = minimal (only necessary particles will appear), 1 = lowered (most unnecessary particles will be filtered), 2 = all particles are visible");

        spawnRockCrystalOres = latestConfig.getBoolean("rockCrystalsEnabled", "worldgen", true, "Set this to false to disable rock crystal oregen entirely.");
        crystalDensity = latestConfig.getInt("crystalDensity", "worldgen", 15, 0, 40, "Defines how frequently rock-crystals will spawn underground. The lower the number, the more frequent crystals will spawn. (onWorldGen: random.nextInt(crystalDensity) == 0 -> gen 1 ore in that chunk)");
        marbleAmount = latestConfig.getInt("generateMarbleAmount", "worldgen", 4, 0, 32, "Defines how many marble veins are generated per chunk. 0 = disabled");
        marbleVeinSize = latestConfig.getInt("generateMarbleVeinSize", "worldgen", 20, 1, 32, "Defines how big generated marble veins are.");
        aquamarineAmount = latestConfig.getInt("generateAquamarineAmount", "worldgen", 64, 0, 2048, "Defines how many aquamarine ores it'll attempt to generate in per chunk. 0 = disabled");
        constellationPaperRarity = latestConfig.getInt("constellationPaperRarity", "worldgen", 10, 1, 128, "Defines the rarity of the constellation paper item in loot chests.");
        constellationPaperQuality = latestConfig.getInt("constellationPaperQuality", "worldgen", 2, 1, 128, "Defines the quality of the constellation paper item in loot chests.");
        respectIdealDistances = latestConfig.getBoolean("respectIdealStructureDistances", "worldgen", respectIdealDistances, "If this is set to true, the world generator will try and spawn structures more evenly distributed by their 'ideal' distance set in their config entries. WARNING: might add additional worldgen time.");
        String[] dimGenWhitelist = latestConfig.getStringList("worldGenWhitelist", "worldgen", new String[] { "0" }, "the Astral Sorcery-specific worldgen will only run in Dimension ID's listed here.");

        enableRetroGen = latestConfig.getBoolean("enableRetroGen", "retrogen", false, "WARNING: Setting this to true, will check on every chunk load if the chunk has been generated depending on the current AstralSorcery version. If the chunk was then generated with an older version, the mod will try and do the worldgen that's needed from the last recorded version to the current version. DO NOT ENABLE THIS FEATURE UNLESS SPECIFICALLY REQUIRED. It might/will slow down chunk loading.");

        fillWhitelistIDs(dimWhitelist);
        fillWeakSkyRenders(weakSkyRenders);
        fillDimGenWhitelist(dimGenWhitelist);

        for (ConfigEntry ce : dynamicConfigEntries) {
            ce.loadFromConfig(latestConfig);
        }
    }

    private static void fillDimGenWhitelist(String[] dimGenWhitelist) {
        List<Integer> out = new ArrayList<>();
        for (String s : dimGenWhitelist) {
            if(s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.log.warn("[AstralSorcery] Error while reading config entry 'worldGenWhitelist': " + s + " is not a number!");
            }
        }
        worldGenDimWhitelist = new ArrayList<>(out.size());
        worldGenDimWhitelist.addAll(out);
        Collections.sort(worldGenDimWhitelist);
    }

    private static void fillWeakSkyRenders(String[] weakSkyRenders) {
        List<Integer> out = new ArrayList<>();
        for (String s : weakSkyRenders) {
            if(s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.log.warn("[AstralSorcery] Error while reading config entry 'weakSkyRenders': " + s + " is not a number!");
            }
        }
        weakSkyRendersWhitelist = new ArrayList<>(out.size());
        weakSkyRendersWhitelist.addAll(out);
        Collections.sort(weakSkyRendersWhitelist);
    }

    private static void fillWhitelistIDs(String[] dimWhitelist) {
        List<Integer> out = new ArrayList<>();
        for (String s : dimWhitelist) {
            if(s.isEmpty()) continue;
            try {
                out.add(Integer.parseInt(s));
            } catch (NumberFormatException exc) {
                AstralSorcery.log.warn("[AstralSorcery] Error while reading config entry 'skySupportedDimensions': " + s + " is not a number!");
            }
        }
        constellationSkyDimWhitelist = Lists.newArrayList(out);
        Collections.sort(constellationSkyDimWhitelist);
    }

}
