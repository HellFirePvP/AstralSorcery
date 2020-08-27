/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.observerlib.common.util.RegistryUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FeaturePlacementConfig
 * Created by HellFirePvP
 * Date: 22.07.2019 / 09:03
 */
public class FeaturePlacementConfig extends ConfigEntry implements IPlacementConfig {

    public static final Codec<FeaturePlacementConfig> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            Codec.STRING.fieldOf("featureName").forGetter(cfg -> cfg.featureName),
            Codec.BOOL.fieldOf("whitelistBiomeSpecification").forGetter(cfg -> cfg.defaultWhitelistBiomeSpecification),
            Codec.BOOL.fieldOf("whitelistDimensionTypeSpecification").forGetter(cfg -> cfg.defaultWhitelistDimensionSpecification),
            Codec.STRING.listOf().fieldOf("applicableBiomeCategories").forGetter(cfg -> cfg.defaultApplicableBiomeCategories),
            ResourceLocation.RESOURCE_LOCATION_CODEC.listOf().fieldOf("applicableDimensionTypes").forGetter(cfg -> cfg.defaultApplicableDimensionTypes),
            Codec.INT.fieldOf("minY").forGetter(cfg -> cfg.defaultMinY),
            Codec.INT.fieldOf("maxY").forGetter(cfg -> cfg.defaultMaxY),
            Codec.INT.fieldOf("generationChance").forGetter(cfg -> cfg.defaultGenerationChance),
            Codec.INT.fieldOf("generationAmount").forGetter(cfg -> cfg.defaultGenerationAmount))
                .apply(codecBuilder, FeaturePlacementConfig::new));

    protected final String featureName;
    protected final boolean defaultWhitelistBiomeSpecification;
    protected final boolean defaultWhitelistDimensionSpecification;
    protected final List<String> defaultApplicableBiomeCategories;
    protected final List<ResourceLocation> defaultApplicableDimensionTypes;
    protected final int defaultMinY;
    protected final int defaultMaxY;
    protected final int defaultGenerationChance;
    protected final int defaultGenerationAmount;

    private ForgeConfigSpec.BooleanValue configEnabled;
    private ForgeConfigSpec.IntValue configMinY;
    private ForgeConfigSpec.IntValue configMaxY;
    private ForgeConfigSpec.IntValue configGenerationChance;
    private ForgeConfigSpec.IntValue configGenerationAmount;
    private ForgeConfigSpec.BooleanValue configWhitelistBiomeCfg;
    private ForgeConfigSpec.BooleanValue configWhitelistDimensionCfg;
    private ForgeConfigSpec.ConfigValue<List<String>> configBiomeCategories;
    private ForgeConfigSpec.ConfigValue<List<String>> configDimensionTypes;

    private List<Biome.Category> convertedBiomeCategories = null;
    private List<ResourceLocation> convertedDimensionTypes = null;

    public FeaturePlacementConfig(String featureName,
                                  boolean defaultWhitelistBiomeSpecification,
                                  boolean defaultWhitelistDimensionSpecification,
                                  List<String> defaultApplicableBiomeTypes,
                                  List<ResourceLocation> defaultApplicableDimensionTypes,
                                  int defaultMinY, int defaultMaxY,
                                  int defaultGenerationChance, int defaultGenerationAmount) {
        super(String.format("world.generation.%s", featureName.toLowerCase()));
        this.featureName = featureName.toLowerCase();
        this.defaultWhitelistBiomeSpecification = defaultWhitelistBiomeSpecification;
        this.defaultWhitelistDimensionSpecification = defaultWhitelistDimensionSpecification;
        this.defaultApplicableBiomeCategories = defaultApplicableBiomeTypes;
        this.defaultApplicableDimensionTypes = defaultApplicableDimensionTypes;
        this.defaultMinY = defaultMinY;
        this.defaultMaxY = defaultMaxY;
        this.defaultGenerationChance = defaultGenerationChance;
        this.defaultGenerationAmount = defaultGenerationAmount;
    }

    public boolean canGenerateAtAll() {
        return this.configEnabled.get();
    }

    public int getRandomY(Random rand) {
        int minY = Math.min(this.configMinY.get(), this.configMaxY.get());
        int maxY = Math.max(this.configMinY.get(), this.configMaxY.get());
        return minY + rand.nextInt(Math.max(maxY - minY, 1));
    }

    public boolean generatesInWorld(RegistryKey<DimensionType> dimTypeKey) {
        if (this.convertedDimensionTypes == null) {
            this.convertedDimensionTypes = this.convertDimensionTypeNames();
        }
        if (this.configWhitelistDimensionCfg.get()) {
            return this.convertedDimensionTypes.contains(dimTypeKey.func_240901_a_());
        } else {
            return !this.convertedDimensionTypes.contains(dimTypeKey.func_240901_a_());
        }
    }

    public boolean generatesInBiome(Biome biome) {
        if (this.convertedBiomeCategories == null) {
            this.convertedBiomeCategories = this.convertBiomeTypeNames();
        }
        Biome.Category biomeCategory = biome.getCategory();
        if (this.configWhitelistBiomeCfg.get()) {
            return this.convertedBiomeCategories.contains(biomeCategory);
        } else {
            return !this.convertedBiomeCategories.contains(biomeCategory);
        }
    }

    public boolean canPlace(ISeedReader iWorld, BlockPos pos, Random rand) {
        if (!this.canGenerateAtAll()) {
            return false;
        }
        RegistryKey<DimensionType> dimTypeKey = RegistryUtil.server().getRegistryKey(Registry.DIMENSION_TYPE_KEY, iWorld.func_230315_m_());
        if (!this.generatesInWorld(dimTypeKey)) {
            return false;
        }
        long hashedSeed = BiomeManager.func_235200_a_(iWorld.getSeed());
        IBiomeMagnifier biomeZoom = iWorld.func_230315_m_().getMagnifier();
        Biome biome = biomeZoom.getBiome(hashedSeed, pos.getX(), pos.getY(), pos.getZ(), iWorld::getNoiseBiomeRaw);
        if (!this.generatesInBiome(biome)) {
            return false;
        }
        int rMinY = this.configMinY.get();
        int rMaxY = this.configMaxY.get();
        return rMinY <= rMaxY && pos.getY() >= rMinY && pos.getY() <= rMaxY;
    }

    private List<Biome.Category> convertBiomeTypeNames() {
        return this.configBiomeCategories.get().stream()
                .filter(strKey -> Biome.Category.func_235103_a_(strKey) != null)
                .map(Biome.Category::func_235103_a_)
                .collect(Collectors.toList());
    }

    private List<ResourceLocation> convertDimensionTypeNames() {
        return this.configDimensionTypes.get().stream()
                .map(ResourceLocation::new)
                .collect(Collectors.toList());
    }

    public int getGenerationChance() {
        return this.configGenerationChance.get();
    }

    public int getGenerationAmount() {
        return this.configGenerationAmount.get();
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        this.configEnabled = cfgBuilder
                .comment("Set this to false to disable this feature from generation.")
                .translation(translationKey("enabled"))
                .define("enabled", true);

        this.configMinY = cfgBuilder
                .comment("Set this to the lowest possible Y-level this feature should be able to generate at. Should be lower than 'maxY'")
                .translation(translationKey("miny"))
                .defineInRange("minY", this.defaultMinY, 1, 216);
        this.configMaxY = cfgBuilder
                .comment("Set this to the highest possible Y-level this feature should be able to generate at. Should be higher than 'minY'")
                .translation(translationKey("maxy"))
                .defineInRange("maxY", this.defaultMaxY, 2, 217);
        this.configGenerationChance = cfgBuilder
                .comment("Set this to set the overall chance for this feature to generate. The higher, the rarer.")
                .translation(translationKey("generationchance"))
                .defineInRange("generationChance", this.defaultGenerationChance, 1, 200_000);
        this.configGenerationAmount = cfgBuilder
                .comment("Set the amount this feature tries to generate per chunk")
                .translation(translationKey("generationamount"))
                .defineInRange("generationAmount", this.defaultGenerationAmount, 1, 128);

        this.configWhitelistBiomeCfg = cfgBuilder
                .comment("Set this to true to make the biome-type restrictions a whitelist, false for blacklist")
                .translation(translationKey("whitelistbiomespecification"))
                .define("whitelistBiomeConfigurations", this.defaultWhitelistBiomeSpecification);
        this.configWhitelistDimensionCfg = cfgBuilder
                .comment("Set this to true to make the dimension-id restrictions a whitelist, false for blacklist")
                .translation("whitelistdimensionspecification")
                .define("whitelistDimensionConfigurations", this.defaultWhitelistDimensionSpecification);

        this.configDimensionTypes = cfgBuilder
                .comment("List all dimensionIds here that this feature should spawn in")
                .translation(translationKey("dimensions"))
                .define("dimensionids", this.defaultApplicableDimensionTypes.stream()
                        .map(ResourceLocation::toString)
                        .collect(Collectors.toList()));
        this.configBiomeCategories = cfgBuilder
                .comment("List all biome types here that this feature should be able to spawn in")
                .translation(translationKey("biometypes"))
                .define("biomeTypes", this.defaultApplicableBiomeCategories);

    }
}
