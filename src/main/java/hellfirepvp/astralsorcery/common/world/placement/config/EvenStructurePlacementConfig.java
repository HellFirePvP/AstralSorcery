/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement.config;

import hellfirepvp.astralsorcery.common.data.world.StructureGenerationBuffer;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EvenStructurePlacementConfig
 * Created by HellFirePvP
 * Date: 23.07.2019 / 20:11
 */
public class EvenStructurePlacementConfig extends FeaturePlacementConfig {

    private final int defaultStructureSize;
    private final StructureType type;

    private ForgeConfigSpec.IntValue configStructureSize;

    public EvenStructurePlacementConfig(StructureType type, int structureSize,
                                        List<BiomeDictionary.Type> applicableBiomeTypes,
                                        List<Integer> applicableDimensions,
                                        int minY, int maxY, int generationChance) {
        super(type.getRegistryName().getPath(), true, true, applicableBiomeTypes, applicableDimensions, minY, maxY, generationChance, 1);
        this.type = type;
        this.defaultStructureSize = structureSize;
    }

    @Override
    public boolean canPlace(IWorld iWorld, BlockPos pos, Random rand) {
        if (!super.canPlace(iWorld, pos, rand)) {
            return false;
        }
        if (!this.getType().isAverageDistanceRequired()) {
            return true;
        }

        float chance = 0F;
        StructureGenerationBuffer buf = DataAS.DOMAIN_AS.getData(iWorld.getWorld(), DataAS.KEY_STRUCTURE_GENERATION);
        float dist = this.getType().getAverageDistance();

        double dst = buf.getDstToClosest(this.getType(), dist, iWorld.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos));
        if (dst != -1) {
            if (dst < 32) {
                return false;
            }
            chance = (float) (dist / dst);
        }

        return rand.nextInt(Math.max(Math.round(this.getGenerationChance() * chance), 1)) == 0;
    }

    public int getStructureSize() {
        return this.configStructureSize.get();
    }

    public StructureType getType() {
        return type;
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        super.createEntries(cfgBuilder);

        this.configStructureSize = cfgBuilder
                .comment("Set this to the estimated structure size to be generated, divided by 2")
                .translation("config.world.generation.structuresize")
                .defineInRange("structureSize", this.defaultStructureSize, 1, 10);
    }
}
