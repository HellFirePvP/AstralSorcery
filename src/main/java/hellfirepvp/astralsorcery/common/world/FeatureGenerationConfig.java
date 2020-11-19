package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FeatureGenerationConfig
 * Created by HellFirePvP
 * Date: 19.11.2020 / 21:37
 */
public class FeatureGenerationConfig extends ConfigEntry {

    private final List<Biome.Category> categories;
    private boolean defaultEverywhere = false;

    private ForgeConfigSpec.BooleanValue enabled;
    private ForgeConfigSpec.BooleanValue everywhere;
    private ForgeConfigSpec.ConfigValue<List<String>> categoryNames;

    public FeatureGenerationConfig(ResourceLocation featureName, List<Biome.Category> categories) {
        this(featureName.getPath(), categories);
    }

    public FeatureGenerationConfig(String featureName, List<Biome.Category> categories) {
        super(featureName);
        this.categories = categories;
    }

    public <T extends FeatureGenerationConfig> T setGenerateEverywhere() {
        this.defaultEverywhere = true;
        return (T) this;
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        this.enabled = cfgBuilder
                .comment("Set this to false to disable this worldgen feature")
                .translation(translationKey("enabled"))
                .define("enabled", true);
        this.everywhere = cfgBuilder
                .comment("Set this to true to let this feature generate everywhere, regardless of biome")
                .translation(translationKey("everywhere"))
                .define("everywhere", this.defaultEverywhere);

        String allCategories = Arrays.stream(Biome.Category.values())
                .map(Biome.Category::getString)
                .collect(Collectors.joining(","));
        List<String> defaultCategories = categories.stream()
                .map(Biome.Category::getString)
                .collect(Collectors.toList());
        this.categoryNames = cfgBuilder
                .comment("Sets the categories to generate this feature in. Available categories: " + allCategories)
                .translation(translationKey("categoryNames"))
                .define("categoryNames", defaultCategories);
    }

    public boolean isEnabled() {
        return this.enabled.get();
    }

    public boolean canGenerateIn(Biome.Category category) {
        if (this.everywhere.get()) {
            return true;
        }
        return this.categoryNames.get().contains(category.getString());
    }
}
