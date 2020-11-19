package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.ImmutableMap;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.world.FeatureGenerationConfig;
import hellfirepvp.astralsorcery.common.world.StructureGenerationConfig;
import hellfirepvp.astralsorcery.common.world.TemplateStructureFeature;
import hellfirepvp.astralsorcery.common.world.feature.FeatureAncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.feature.FeatureDesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.feature.FeatureSmallShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.AncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.DesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.SmallShrineStructure;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Config.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Features.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Structures.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryWorldGeneration
 * Created by HellFirePvP
 * Date: 18.11.2020 / 21:17
 */
public class RegistryWorldGeneration {

    private static final Map<TemplateStructureFeature, StructureGenerationConfig> STRUCTURES = new HashMap<>();
    private static final Map<ConfiguredFeature<?, ?>, FeatureGenerationConfig> FEATURES = new HashMap<>();
    private static final Map<ConfiguredFeature<?, ?>, GenerationStage.Decoration> FEATURE_STAGE = new HashMap<>();

    public static void init() {
        ANCIENT_SHRINE_PIECE = registerStructurePiece(KEY_ANCIENT_SHRINE, AncientShrineStructure::new);
        DESERT_SHRINE_PIECE  = registerStructurePiece(KEY_DESERT_SHRINE,  DesertShrineStructure::new);
        SMALL_SHRINE_PIECE   = registerStructurePiece(KEY_SMALL_SHRINE,   SmallShrineStructure::new);

        STRUCTURE_ANCIENT_SHRINE = registerStructure(KEY_ANCIENT_SHRINE, CFG_ANCIENT_SHRINE, new FeatureAncientShrineStructure());
        STRUCTURE_DESERT_SHRINE  = registerStructure(KEY_DESERT_SHRINE, CFG_DESERT_SHRINE, new FeatureDesertShrineStructure());
        STRUCTURE_SMALL_SHRINE   = registerStructure(KEY_SMALL_SHRINE, CFG_SMALL_SHRINE, new FeatureSmallShrineStructure());

        MARBLE = registerFeature(KEY_MARBLE, GenerationStage.Decoration.UNDERGROUND_ORES, CFG_MARBLE,
                Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, BlocksAS.MARBLE_RAW.getDefaultState(), 30))
                    .range(128).square().func_242731_b(8));
    }

    public static void registerStructureGeneration() {
        Map<Structure<?>, StructureSeparationSettings> defaultStructureSettings = DimensionSettings.field_242740_q.getStructures().func_236195_a_();
        ImmutableMap.Builder<Structure<?>, StructureSeparationSettings> builder = ImmutableMap.builder();
        builder.putAll(DimensionStructuresSettings.field_236191_b_);
        STRUCTURES.forEach((structure, cfg) -> {
            if (cfg.isEnabled()) {
                StructureSeparationSettings settings = cfg.createSettings();
                builder.put(structure, settings);
                defaultStructureSettings.put(structure, settings);
            }
        });
        DimensionStructuresSettings.field_236191_b_ = builder.build();
    }

    public static void loadBiomeFeatures(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder gen = event.getGeneration();
        STRUCTURES.forEach((structure, cfg) -> {
            if (cfg.isEnabled() && cfg.canGenerateIn(event.getCategory())) {
                gen.withStructure(structure.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
            }
        });
        FEATURES.forEach((feature, cfg) -> {
            if (cfg.isEnabled() && cfg.canGenerateIn(event.getCategory())) {
                GenerationStage.Decoration stage = FEATURE_STAGE.get(feature);
                if (stage == null) {
                    ResourceLocation key = WorldGenRegistries.CONFIGURED_FEATURE.getOptionalKey(feature)
                            .map(RegistryKey::getLocation)
                            .orElse(new ResourceLocation("not_registered"));
                    throw new IllegalArgumentException("Unknown generation stage for feature " + key + "!");
                }
                gen.withFeature(stage, feature);
            }
        });
    }

    public static void addConfigEntries(Consumer<ConfigEntry> registrar) {
        registrar.accept(CFG_ANCIENT_SHRINE);
        registrar.accept(CFG_DESERT_SHRINE);
        registrar.accept(CFG_SMALL_SHRINE);

        registrar.accept(CFG_MARBLE);
    }

    private static ConfiguredFeature<?, ?> registerFeature(ResourceLocation key, GenerationStage.Decoration stage, FeatureGenerationConfig cfg, ConfiguredFeature<?, ?> feature) {
        FEATURE_STAGE.put(feature, stage);
        FEATURES.put(feature, cfg);
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, feature);
    }

    private static <T extends IStructurePieceType> T registerStructurePiece(ResourceLocation key, T type) {
        return Registry.register(Registry.STRUCTURE_PIECE, key, type);
    }

    private static <S extends TemplateStructureFeature> S registerStructure(ResourceLocation key, StructureGenerationConfig cfg, S structure) {
        AstralSorcery.getProxy().getRegistryPrimer().register(structure.setRegistryName(key));
        Structure.NAME_STRUCTURE_BIMAP.put(structure.getStructureName(), structure);
        STRUCTURES.put(structure, cfg);
        return structure;
    }
}
