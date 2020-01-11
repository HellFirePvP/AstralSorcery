/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.base;

import hellfirepvp.astralsorcery.common.data.config.CommonConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfigEntry
 * Created by HellFirePvP
 * Date: 19.04.2019 / 23:12
 */
public abstract class ConfigEntry implements Consumer<ForgeConfigSpec.Builder> {

    private Set<ConfigEntry> subSections = new HashSet<>();
    private final String path;

    private String subPath = "";
    private ModConfig.Type configType;

    public ConfigEntry(String section) {
        this.path = section.toLowerCase();
    }

    public ConfigEntry newSubSection(ConfigEntry entry) {
        entry.subPath = this.getPath();
        entry.setConfigType(this.configType);
        this.subSections.add(entry);
        return entry;
    }

    final void setConfigType(ModConfig.Type type) {
        this.configType = type;
        this.subSections.forEach(section -> section.setConfigType(type));
    }

    @Override
    public final void accept(ForgeConfigSpec.Builder builder) {
        this.createEntries(builder);

        for (ConfigEntry section : subSections) {
            List<String> splitPath = CommonConfig.DOT_SPLITTER.splitToList(section.getPath());
            builder.push(splitPath);
            section.accept(builder);
            builder.pop(splitPath.size());
        }
    }

    protected String translationKey(String key) {
        return String.format("config.%s.%s.%s", this.configType.extension(), this.getFullPath(), key);
    }

    public abstract void createEntries(ForgeConfigSpec.Builder cfgBuilder);

    public String getPath() {
        return this.path;
    }

    public String getFullPath() {
        return this.subPath.isEmpty() ? getPath() : String.format("%s.%s", this.subPath, getPath());
    }

}
