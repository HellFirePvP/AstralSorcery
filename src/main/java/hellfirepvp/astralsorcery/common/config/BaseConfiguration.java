/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config;

import com.google.common.base.Splitter;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BaseConfiguration
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class BaseConfiguration {

    private static final Map<ModConfig.Type, BaseConfiguration> REGISTERED_CONFIGS = new HashMap<>();
    public static final Splitter DOT_SPLITTER = Splitter.on(".");

    private final List<ConfigEntry> configEntries = new ArrayList<>();
    private final ModConfig.Type configType;
    private final String fileName;

    private boolean built = false;

    protected BaseConfiguration(ModConfig.Type configType) {
        this(configType, AstralSorcery.MODID);
    }

    protected BaseConfiguration(ModConfig.Type configType, String fileName) {
        this.configType = configType;
        this.fileName = fileName;
    }

    public static BaseConfiguration simple(ModConfig.Type configType) {
        return new BaseConfiguration(configType) {};
    }

    protected void onReload() {}

    public <T extends ConfigEntry> T addConfigEntry(T configEntry) {
        configEntry.setConfigType(this.configType);
        configEntries.add(configEntry);
        return configEntry;
    }

    public final void build() {
        if (this.built) {
            throw new IllegalStateException("Trying to build the same configuration twice!");
        }

        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        this.createEntries(builder);
        this.make(builder.build());
        this.built = true;
    }

    protected void createEntries(ModConfigSpec.Builder builder) {
        this.configEntries.forEach(entry -> {
            List<String> splitPath = DOT_SPLITTER.splitToList(entry.getPath());
            builder.push(splitPath);
            entry.accept(builder);
            builder.pop(splitPath.size());
        });
    }

    private void make(ModConfigSpec spec) {
        String fileName = this.configType == ModConfig.Type.SERVER ?
                String.format("%s.toml", this.fileName) :
                String.format("%s-%s.toml", this.fileName, this.configType.extension());
        ModContainer ct = AstralSorcery.getModContainer();
        ct.registerConfig(this.configType, spec, fileName);

        REGISTERED_CONFIGS.put(this.configType, this);
    }

    public static void reloadConfigurations(ModConfigEvent.Reloading cfgLoadEvent) {
        ModConfig config = cfgLoadEvent.getConfig();
        if (config.getModId().equals(AstralSorcery.MODID)) {
            BaseConfiguration cfg = REGISTERED_CONFIGS.get(config.getType());
            if (cfg != null) {
                cfg.onReload();
                cfg.configEntries.forEach(entry -> entry.up(ConfigEntry::onReload));
            }
        }
    }
}
