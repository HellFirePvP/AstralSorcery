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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FeatureAncientShrineStructure
 * Created by HellFirePvP
 * Date: 07.05.2020 / 07:58
 */
public class FeatureDesertShrineStructure extends ConfiguredStructureFeature {

    public FeatureDesertShrineStructure(StructurePlacementConfig placementConfig) {
        super(placementConfig);
    }

    @Override
    public IStartFactory getStartFactory() {
        return this.configuredStart(Start::new);
    }

    @Override
    public String getStructureName() {
        return WorldGenerationAS.KEY_DESERT_SHRINE.toString();
    }

    public static class Start extends ConfiguredStructureStart {

        public Start(Structure<?> structure, int chunkX, int chunkZ, MutableBoundingBox structureBox, int referenceIn, long seed) {
            super(structure, chunkX, chunkZ, structureBox, referenceIn, seed);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            BlockPos genPos = this.getRandomPlacement(generator, chunkX, chunkZ);
            if (genPos != null) {
                DesertShrineStructure shrine = new DesertShrineStructure(templateManagerIn, genPos.down(10));
                this.getComponents().add(shrine);
                this.recalculateStructureSize();
            }
        }
    }
}
