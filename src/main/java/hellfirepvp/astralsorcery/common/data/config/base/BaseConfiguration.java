/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.base;

import com.google.common.base.Splitter;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaseConfiguration
 * Created by HellFirePvP
 * Date: 20.04.2019 / 10:22
 */
public class BaseConfiguration {

    private static final Map<ModConfig.Type, BaseConfiguration> REGISTERED_CONFIGS = new HashMap<>();

    public static final Splitter DOT_SPLITTER = Splitter.on(".");

    private final List<ConfigEntry> configEntries = new ArrayList<>();

    private final ModConfig.Type configType;

    protected BaseConfiguration(ModConfig.Type configType) {
        this.configType = configType;
    }

    public <T extends ConfigEntry> T addConfigEntry(T configEntry) {
        configEntry.setConfigType(this.configType);
        configEntries.add(configEntry);
        return configEntry;
    }

    public void buildConfiguration() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        for (ConfigEntry entry : configEntries) {
            List<String> splitPath = DOT_SPLITTER.splitToList(entry.getPath());
            builder.push(splitPath);
            entry.accept(builder);
            builder.pop(splitPath.size());
        }

        makeAndRegister(builder.build(), AstralSorcery.MODID);
    }

    private void makeAndRegister(ForgeConfigSpec spec, String file) {
        String fileName = this.configType == ModConfig.Type.SERVER ?
                String.format("%s.toml", file) :
                String.format("%s-%s.toml", file, this.configType.extension());
        ModContainer ct = AstralSorcery.getModContainer();
        ModConfig cfg = new ModConfig(this.configType, spec, ct, fileName);
        ct.addConfig(cfg);

        REGISTERED_CONFIGS.put(this.configType, this);
    }

    public static void refreshConfiguration(ModConfig.Loading cfgLoadEvent) {
        ModConfig config = cfgLoadEvent.getConfig();
        if (config.getModId().equals(AstralSorcery.MODID)) {
            BaseConfiguration cfg = REGISTERED_CONFIGS.get(config.getType());
            if (cfg != null) {
                cfg.configEntries.forEach(ConfigEntry::reload);
            }
        }
    }
}
