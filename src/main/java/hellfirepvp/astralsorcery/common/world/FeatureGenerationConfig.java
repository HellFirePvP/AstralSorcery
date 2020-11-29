/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.world.placement.config.WorldFilterConfig;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
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

    private List<Biome.Category> categories = new ArrayList<>();
    private List<RegistryKey<World>> worlds = new ArrayList<>();
    private boolean defaultEveryBiome = false, defaultEveryWorld = false;

    private ForgeConfigSpec.BooleanValue enabled;
    private ForgeConfigSpec.BooleanValue everyBiome;
    private ForgeConfigSpec.BooleanValue everyWorld;
    private ForgeConfigSpec.ConfigValue<List<String>> biomeCategoryNames;
    private ForgeConfigSpec.ConfigValue<List<String>> worldNames;

    public FeatureGenerationConfig(ResourceLocation featureName) {
        this(featureName.getPath());
    }

    public FeatureGenerationConfig(String featureName) {
        super(featureName);
    }

    public <T extends FeatureGenerationConfig> T generatesInBiomes(List<Biome.Category> biomeCategories) {
        this.categories = biomeCategories;
        return (T) this;
    }

    public <T extends FeatureGenerationConfig> T generatesInWorlds(List<RegistryKey<World>> worlds) {
        this.worlds = worlds;
        return (T) this;
    }

    public <T extends FeatureGenerationConfig> T setGenerateEveryBiome() {
        this.defaultEveryBiome = true;
        return (T) this;
    }

    public <T extends FeatureGenerationConfig> T setGenerateEveryWorld() {
        this.defaultEveryWorld = true;
        return (T) this;
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        this.enabled = cfgBuilder
                .comment("Set this to false to disable this worldgen feature.")
                .translation(translationKey("enabled"))
                .define("enabled", true);
        this.everyBiome = cfgBuilder
                .comment("Set this to true to let this feature generate in any biome.")
                .translation(translationKey("everyBiome"))
                .define("everyBiome", this.defaultEveryBiome);
        this.everyWorld = cfgBuilder
                .comment("Set this to true to let this feature generate in any world. (Does NOT work for structures!)")
                .translation(translationKey("everyWorld"))
                .define("everyWorld", this.defaultEveryWorld);

        String allCategories = Arrays.stream(Biome.Category.values())
                .map(Biome.Category::getString)
                .collect(Collectors.joining(","));
        List<String> defaultCategories = categories.stream()
                .map(Biome.Category::getString)
                .collect(Collectors.toList());
        this.biomeCategoryNames = cfgBuilder
                .comment("Sets the categories to generate this feature in. Available categories: " + allCategories)
                .translation(translationKey("biomeCategoryNames"))
                .define("biomeCategoryNames", defaultCategories);

        //TODO Structures..
        List<String> defaultWorlds = worlds.stream()
                .map(RegistryKey::getLocation)
                .map(ResourceLocation::getPath)
                .collect(Collectors.toList());
        this.worldNames = cfgBuilder
                .comment("Sets the worlds to generate this feature in. (Does NOT work for structures!)")
                .translation(translationKey("worldNames"))
                .define("worldNames", defaultWorlds);
    }

    public boolean isEnabled() {
        return this.enabled.get();
    }

    public boolean canGenerateIn(Biome.Category category) {
        if (this.everyBiome.get()) {
            return true;
        }
        return this.biomeCategoryNames.get().contains(category.getString());
    }

    public WorldFilterConfig worldFilterConfig() {
        return new WorldFilterConfig(this.everyWorld::get, () -> {
            return this.worldNames.get().stream()
                    .map(ResourceLocation::new)
                    .map(key -> RegistryKey.getOrCreateKey(Registry.WORLD_KEY, key))
                    .collect(Collectors.toList());
        });
    }
}
