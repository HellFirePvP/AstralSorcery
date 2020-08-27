/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure.feature;

import hellfirepvp.astralsorcery.common.lib.WorldGenerationAS;
import hellfirepvp.astralsorcery.common.world.config.StructurePlacementConfig;
import hellfirepvp.astralsorcery.common.world.structure.ConfiguredStructureStart;
import hellfirepvp.astralsorcery.common.world.structure.DesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.SmallShrineStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FeatureSmallShrineStructure
 * Created by HellFirePvP
 * Date: 07.05.2020 / 07:58
 */
public class FeatureSmallShrineStructure extends ConfiguredStructure {

    public FeatureSmallShrineStructure(StructurePlacementConfig placementConfig) {
        super(placementConfig);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return this.configuredStart(Start::new);
    }

    @Override
    public String getStructureName() {
        return WorldGenerationAS.KEY_SMALL_SHRINE.toString();
    }

    public static class Start extends ConfiguredStructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox structureBox, int referenceIn, long seed) {
            super(structure, chunkX, chunkZ, structureBox, referenceIn, seed);
        }

        @Override
        public void func_230364_a_(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig cfg) {
            BlockPos pos = new BlockPos(chunkX * 16 + rand.nextInt(16), 0, chunkZ * 16 + rand.nextInt(16));
            SmallShrineStructure shrine = new SmallShrineStructure(templateManager, pos.down(7));
            this.getComponents().add(shrine);
            this.recalculateStructureSize();
        }
    }
}
