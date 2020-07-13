/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaseConfiguration
 * Created by HellFirePvP
 * Date: 20.04.2019 / 10:22
 */
public class BaseConfiguration {

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final Splitter DOT_SPLITTER = Splitter.on(".");

    private List<ConfigEntry> configEntries = new ArrayList<>();

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

        makeAndRegister(this.configType, builder.build(), AstralSorcery.MODID);
    }

    static void makeAndRegister(ModConfig.Type type, ForgeConfigSpec spec, String file) {
        String fileName = type == ModConfig.Type.SERVER ?
                String.format("%s.toml", file) :
                String.format("%s-%s.toml", file, type.extension());
        ModContainer ct = AstralSorcery.getModContainer();
        ModConfig cfg = new ModConfig(type, spec, ct, fileName);
        ct.addConfig(cfg);
    }

}
