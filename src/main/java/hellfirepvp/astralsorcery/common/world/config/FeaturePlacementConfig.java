/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.config;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.BiomeDictionary;
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

    private final boolean defaultWhitelistBiomeSpecification;
    private final boolean defaultWhitelistDimensionSpecification;
    private final List<BiomeDictionary.Type> defaultApplicableBiomeTypes;
    private final List<DimensionType> defaultApplicableDimensionTypes;
    private final int defaultMinY;
    private final int defaultMaxY;
    private final int defaultGenerationChance;
    private final int defaultGenerationAmount;

    private ForgeConfigSpec.IntValue configMinY;
    private ForgeConfigSpec.IntValue configMaxY;
    private ForgeConfigSpec.IntValue configGenerationChance;
    private ForgeConfigSpec.IntValue configGenerationAmount;
    private ForgeConfigSpec.BooleanValue configWhitelistBiomeCfg;
    private ForgeConfigSpec.BooleanValue configWhitelistDimensionCfg;
    private ForgeConfigSpec.ConfigValue<List<String>> configBiomeTypes;
    private ForgeConfigSpec.ConfigValue<List<String>> configDimensionTypes;

    private List<BiomeDictionary.Type> convertedBiomeTypes = null;
    private List<DimensionType> convertedDimensionTypes = null;

    public FeaturePlacementConfig(String featureName,
                                  boolean defaultWhitelistBiomeSpecification,
                                  boolean defaultWhitelistDimensionSpecification,
                                  List<BiomeDictionary.Type> defaultApplicableBiomeTypes,
                                  List<DimensionType> defaultApplicableDimensionTypes,
                                  int defaultMinY, int defaultMaxY,
                                  int defaultGenerationChance, int defaultGenerationAmount) {
        super(String.format("world.generation.%s", featureName.toLowerCase()));
        this.defaultWhitelistBiomeSpecification = defaultWhitelistBiomeSpecification;
        this.defaultWhitelistDimensionSpecification = defaultWhitelistDimensionSpecification;
        this.defaultApplicableBiomeTypes = defaultApplicableBiomeTypes;
        this.defaultApplicableDimensionTypes = defaultApplicableDimensionTypes;
        this.defaultMinY = defaultMinY;
        this.defaultMaxY = defaultMaxY;
        this.defaultGenerationChance = defaultGenerationChance;
        this.defaultGenerationAmount = defaultGenerationAmount;
    }

    public int getRandomY(Random rand) {
        int minY = Math.min(this.configMinY.get(), this.configMaxY.get());
        int maxY = Math.max(this.configMinY.get(), this.configMaxY.get());
        return minY + rand.nextInt(Math.max(maxY - minY, 1));
    }

    public boolean generatesInWorld(DimensionType dimType) {
        if (this.convertedDimensionTypes == null) {
            this.convertedDimensionTypes = this.convertDimensionTypeNames();
        }
        if (this.configWhitelistDimensionCfg.get()) {
            return this.convertedDimensionTypes.contains(dimType);
        } else {
            return !this.convertedDimensionTypes.contains(dimType);
        }
    }

    public boolean generatesInBiome(Biome biome) {
        if (this.convertedBiomeTypes == null) {
            this.convertedBiomeTypes = this.convertBiomeTypeNames();
        }
        if (this.configWhitelistBiomeCfg.get()) {
            for (BiomeDictionary.Type type : this.convertedBiomeTypes) {
                if (BiomeDictionary.hasType(biome, type)) {
                    return true;
                }
            }
            return false;
        } else {
            for (BiomeDictionary.Type type : this.convertedBiomeTypes) {
                if (BiomeDictionary.hasType(biome, type)) {
                    return false;
                }
            }
            return true;
        }
    }

    public boolean canPlace(IWorld iWorld, BiomeProvider biomeProvider, BlockPos pos, Random rand) {
        if (!this.generatesInWorld(iWorld.getDimension().getType())) {
            return false;
        }
        long hashedSeed = WorldInfo.byHashing(iWorld.getSeed());
        IBiomeMagnifier biomeZoom = iWorld.getDimension().getType().getMagnifier();
        Biome biome = biomeZoom.getBiome(hashedSeed, pos.getX(), pos.getY(), pos.getZ(), iWorld::getNoiseBiomeRaw);
        if (!this.generatesInBiome(biome)) {
            return false;
        }
        int rMinY = this.configMinY.get();
        int rMaxY = this.configMaxY.get();
        return rMinY <= rMaxY && pos.getY() >= rMinY && pos.getY() <= rMaxY;
    }

    private List<BiomeDictionary.Type> convertBiomeTypeNames() {
        return this.configBiomeTypes.get().stream()
                .filter(name -> BiomeDictionary.Type.getType(name) != null)
                .map(name -> BiomeDictionary.Type.getType(name))
                .collect(Collectors.toList());
    }

    private List<DimensionType> convertDimensionTypeNames() {
        return this.configDimensionTypes.get().stream()
                .filter(name -> DimensionType.byName(new ResourceLocation(name)) != null)
                .map(name -> DimensionType.byName(new ResourceLocation(name)))
                .collect(Collectors.toList());
    }

    public int getGenerationChance() {
        return this.configGenerationChance.get();
    }

    public int getGenerationAmount() {
        return this.configGenerationAmount.get();
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
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
                        .map(dimType -> dimType.getRegistryName().toString())
                        .collect(Collectors.toList()));
        this.configBiomeTypes = cfgBuilder
                .comment("List all biome types here that this feature should be able to spawn in")
                .translation(translationKey("biometypes"))
                .define("biomeTypes", this.defaultApplicableBiomeTypes.stream()
                        .map(BiomeDictionary.Type::getName)
                        .collect(Collectors.toList()));

    }
}
