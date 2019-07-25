/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement.config;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.placement.IPlacementConfig;
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
    private final List<Integer> defaultApplicableDimensions;
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
    private ForgeConfigSpec.ConfigValue<List<Integer>> configDimensionIds;

    private List<BiomeDictionary.Type> convertedBiomeTypes = null;

    public FeaturePlacementConfig(String featureName,
                                  boolean defaultWhitelistBiomeSpecification,
                                  boolean defaultWhitelistDimensionSpecification,
                                  List<BiomeDictionary.Type> defaultApplicableBiomeTypes,
                                  List<Integer> defaultApplicableDimensions,
                                  int defaultMinY, int defaultMaxY,
                                  int defaultGenerationChance, int defaultGenerationAmount) {
        super(String.format("world.generation.%s", featureName.toLowerCase()));
        this.defaultWhitelistBiomeSpecification = defaultWhitelistBiomeSpecification;
        this.defaultWhitelistDimensionSpecification = defaultWhitelistDimensionSpecification;
        this.defaultApplicableBiomeTypes = defaultApplicableBiomeTypes;
        this.defaultApplicableDimensions = defaultApplicableDimensions;
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

    public boolean generatesInWorld(Dimension dim) {
        int id = dim.getType().getId();
        if (this.configWhitelistDimensionCfg.get()) {
            return this.configDimensionIds.get().contains(id);
        } else {
            return !this.configDimensionIds.get().contains(id);
        }
    }

    public boolean generatesInBiome(Biome biome) {
        if (this.convertedBiomeTypes == null) {
            this.convertedBiomeTypes = this.convertNames();
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

    public boolean canPlace(IWorld iWorld, BlockPos pos, Random rand) {
        if (!this.generatesInWorld(iWorld.getDimension())) {
            return false;
        }
        int rMinY = this.configMinY.get();
        int rMaxY = this.configMaxY.get();
        return rMinY <= rMaxY && pos.getY() >= rMinY && pos.getY() <= rMaxY;
    }

    private List<BiomeDictionary.Type> convertNames() {
        return this.configBiomeTypes.get().stream()
                .filter(name -> BiomeDictionary.Type.getType(name) != null)
                .map(name -> BiomeDictionary.Type.getType(name))
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
                .translation("config.world.generation.miny")
                .defineInRange("minY", this.defaultMinY, 1, 186);
        this.configMaxY = cfgBuilder
                .comment("Set this to the highest possible Y-level this feature should be able to generate at. Should be higher than 'minY'")
                .translation("config.world.generation.maxy")
                .defineInRange("maxY", this.defaultMaxY, 2, 187);
        this.configGenerationChance = cfgBuilder
                .comment("Set this to set the overall chance for this feature to generate. The higher, the rarer.")
                .translation("config.world.generation.generationchance")
                .defineInRange("generationChance", this.defaultGenerationChance, 5, 200_000);
        this.configGenerationAmount = cfgBuilder
                .comment("Set the amount this feature tries to generate per chunk")
                .translation("config.world.generation.generationamount")
                .defineInRange("generationAmount", this.defaultGenerationAmount, 1, 128);

        this.configWhitelistBiomeCfg = cfgBuilder
                .comment("Set this to true to make the biome-type restrictions a whitelist, false for blacklist")
                .translation("config.world.generation.whitelistbiomespecification")
                .define("whitelistBiomeConfigurations", this.defaultWhitelistBiomeSpecification);
        this.configWhitelistDimensionCfg = cfgBuilder
                .comment("Set this to true to make the dimension-id restrictions a whitelist, false for blacklist")
                .translation("config.world.generation.whitelistdimensionspecification")
                .define("whitelistDimensionConfigurations", this.defaultWhitelistDimensionSpecification);

        this.configDimensionIds = cfgBuilder
                .comment("List all dimensionIds here that this feature should spawn in")
                .translation("config.world.generation.dimensions")
                .define("dimensionids", this.defaultApplicableDimensions);
        this.configBiomeTypes = cfgBuilder
                .comment("List all biome types here that this feature should be able to spawn in")
                .translation("config.world.generation.biometypes")
                .define("biomeTypes", this.defaultApplicableBiomeTypes.stream()
                        .map(BiomeDictionary.Type::getName)
                        .collect(Collectors.toList()));

    }
}
