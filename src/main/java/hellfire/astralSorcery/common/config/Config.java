package hellfire.astralSorcery.common.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:44
 */
public class Config {

    private static Configuration latestConfig;

    private Config() {}

    public static void load(File file) {
        latestConfig = new Configuration(file);
        latestConfig.load();
        loadData();
        latestConfig.save();
    }

    private static void loadData() {

    }

    public static void save() {
        latestConfig.save();
    }

}
