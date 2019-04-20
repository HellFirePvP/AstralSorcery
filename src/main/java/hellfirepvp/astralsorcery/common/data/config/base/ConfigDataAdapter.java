/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.base;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfigDataAdapter
 * Created by HellFirePvP
 * Date: 20.04.2019 / 07:44
 */
public abstract class ConfigDataAdapter<T extends ConfigDataSet> {

    private ForgeConfigSpec.ConfigValue<List<? extends String>> registryStore = null;
    private final ModConfig.Type registryConfigType;

    public ConfigDataAdapter() {
        this(ModConfig.Type.SERVER);
    }

    public ConfigDataAdapter(ModConfig.Type type) {
        this.registryConfigType = type;
    }

    protected String translationKey(String key) {
        return String.format("config.registry.%s.%s", this.getFileName(), key);
    }

    public final void configCreated(ForgeConfigSpec.ConfigValue<List<? extends String>> createdValue) {
        this.registryStore = createdValue;
    }

    public List<T> getConfiguredValues() {
        List<T> created = new ArrayList<>();
        for (String str : registryStore.get()) {
            T val = deserialize(str);
            if (val != null) {
                created.add(val);
            }
        }
        return created;
    }

    public abstract List<T> getDefaultValues();

    public abstract String getFileName();

    public abstract String getCommentDescription();

    public abstract String getTranslationKey();

    public abstract Predicate<Object> getValidator();

    public ModConfig.Type getRegistryConfigType() {
        return registryConfigType;
    }

    @Nullable
    public abstract T deserialize(String string);

}
