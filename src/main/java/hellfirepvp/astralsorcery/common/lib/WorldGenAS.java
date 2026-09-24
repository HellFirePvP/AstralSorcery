/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.worldgen.feature.RockCrystalOreFeature;
import hellfirepvp.astralsorcery.common.worldgen.feature.RockCrystalOreFeatureConfiguration;
import hellfirepvp.astralsorcery.common.worldgen.placement.RiverbedPlacement;
import hellfirepvp.astralsorcery.common.worldgen.structure.FocalPointStructure;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.FocalPointRegisterProcessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WorldGenAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WorldGenAS {

    public static final DeferredRegister<Feature<?>> FEATURE_REGISTER =
            DeferredRegister.create(BuiltInRegistries.FEATURE, AstralSorcery.MODID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_REGISTER =
            DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, AstralSorcery.MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_REGISTER =
            DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, AstralSorcery.MODID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_REGISTER =
            DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR, AstralSorcery.MODID);

    public static final DeferredHolder<Feature<?>, RockCrystalOreFeature> ROCK_CRYSTAL_ORE =
            FEATURE_REGISTER.register("rock_crystal_ore", () -> new RockCrystalOreFeature(RockCrystalOreFeatureConfiguration.CODEC));

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<RiverbedPlacement>> RIVERBED_PLACEMENT =
            PLACEMENT_REGISTER.register("riverbed_placement", () -> () -> RiverbedPlacement.CODEC);

    public static final DeferredHolder<StructureType<?>, StructureType<FocalPointStructure>> FOCAL_POINT_STRUCTURE =
            STRUCTURE_REGISTER.register("focal_point_structure", () -> () -> FocalPointStructure.CODEC);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<FocalPointRegisterProcessor>> FOCAL_POINT_REGISTER_PROCESSOR =
            STRUCTURE_PROCESSOR_REGISTER.register("focal_point_register_processor", () -> () -> FocalPointRegisterProcessor.CODEC);
}
