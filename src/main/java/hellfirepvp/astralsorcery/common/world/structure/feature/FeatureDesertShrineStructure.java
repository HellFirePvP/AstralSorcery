/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure.feature;

import hellfirepvp.astralsorcery.common.world.TemplateStructureFeature;
import hellfirepvp.astralsorcery.common.world.structure.DesertShrineStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FeatureDesertShrineStructure
 * Created by HellFirePvP
 * Date: 18.11.2020 / 22:01
 */
public class FeatureDesertShrineStructure extends TemplateStructureFeature {

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> config, int chunkPosX, int chunkPosZ, MutableBoundingBox bounds, int ref, long seed) {
            super(config, chunkPosX, chunkPosZ, bounds, ref, seed);
        }

        @Override
        public void func_230364_a_(DynamicRegistries registries, ChunkGenerator gen, TemplateManager mgr, int chunkX, int chunkZ, Biome biome, NoFeatureConfig cfg) {
            int x = chunkX * 16 + rand.nextInt(16);
            int z = chunkZ * 16 + rand.nextInt(16);
            int y = gen.getHeight(x, z, Heightmap.Type.MOTION_BLOCKING);
            DesertShrineStructure structure = new DesertShrineStructure(mgr, new BlockPos(x, y, z));
            this.components.add(structure);
            this.recalculateStructureSize();
        }
    }
}
