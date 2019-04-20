/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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

    public void buildRegistries() {
        for (ConfigDataAdapter<?> dataRegistry : this.dataRegistries) {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            ForgeConfigSpec.ConfigValue<List<? extends String>> cfgList = builder
                    .comment(dataRegistry.getCommentDescription())
                    .translation(dataRegistry.getTranslationKey())
                    .defineList(dataRegistry.getFileName(),
                            dataRegistry.getDefaultValues()
                                    .stream()
                                    .map(ConfigDataSet::serialize)
                                    .collect(Collectors.toList()),
                            dataRegistry.getValidator());

            dataRegistry.configCreated(cfgList);

            BaseConfiguration.makeAndRegister(dataRegistry.getRegistryConfigType(),
                    builder.build(),
                    String.format("%s-%s", AstralSorcery.MODID, dataRegistry.getFileName()));
        }
    }

}
