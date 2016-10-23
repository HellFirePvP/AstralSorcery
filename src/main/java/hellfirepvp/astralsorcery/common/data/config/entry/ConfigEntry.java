package hellfirepvp.astralsorcery.common.data.config.entry;

import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfigEntry
 * Created by HellFirePvP
 * Date: 21.10.2016 / 12:56
 */
public abstract class ConfigEntry {

    private final Section section;
    private final String key;

    protected ConfigEntry(Section section, String key) {
        this.section = section;
        this.key = key;
    }

    public String getConfigurationSection() {
        return section.name().toLowerCase();
    }

    public String getKey() {
        return key;
    }

    public abstract void loadFromConfig(Configuration cfg);

    public static enum Section {

        WORLDGEN

    }

}
