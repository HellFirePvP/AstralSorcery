/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.base;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfigRegistries
 * Created by HellFirePvP
 * Date: 20.04.2019 / 11:12
 */
public class ConfigRegistries {

    private static final ConfigRegistries INSTANCE = new ConfigRegistries();

    private List<ConfigDataAdapter<?>> dataRegistries = new ArrayList<>();

    private ConfigRegistries() {}

    public static ConfigRegistries getRegistries() {
        return INSTANCE;
    }

    public void addDataRegistry(ConfigDataAdapter<?> dataAdapter) {
        dataRegistries.add(dataAdapter);
    }

    public void buildDataRegistries(BaseConfiguration out) {
        out.addConfigEntry(new RegistrySection());
    }

    private class RegistrySection extends ConfigEntry {

        private RegistrySection() {
            super("registries");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            for (ConfigDataAdapter<?> dataRegistry : ConfigRegistries.this.dataRegistries) {

                ForgeConfigSpec.ConfigValue<List<? extends String>> cfgList = cfgBuilder
                        .comment(dataRegistry.getCommentDescription())
                        .translation(dataRegistry.getTranslationKey())
                        .defineList(registrySubSection(dataRegistry.getSectionName()),
                                dataRegistry.getDefaultValues()
                                        .stream()
                                        .map(ConfigDataSet::serialize)
                                        .collect(Collectors.toList()),
                                dataRegistry.getValidator());

                dataRegistry.configBuilt(cfgList);
            }
        }

        private String registrySubSection(String section) {
            return String.format("%s.%s", section.toLowerCase(), section.toLowerCase());
        }
    }

}
