/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import com.mojang.datafixers.Dynamic;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.world.placement.config.EvenStructurePlacementConfig;
import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EvenStructurePlacement
 * Created by HellFirePvP
 * Date: 22.07.2019 / 21:57
 */
public class EvenStructurePlacement extends StructurePlacement<EvenStructurePlacementConfig> {

    public EvenStructurePlacement(Function<Dynamic<?>, ? extends EvenStructurePlacementConfig> cfgFactory) {
        super(cfgFactory);
    }

    public EvenStructurePlacement(EvenStructurePlacementConfig config) {
        this(dyn -> config);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, EvenStructurePlacementConfig config, BlockPos pos) {
        BlockPos closest = DataAS.DOMAIN_AS.getData(worldIn, DataAS.KEY_STRUCTURE_GENERATION).getClosest(config.getType(), pos, 384);
        if (closest != null) {
            BlockArray structure = config.getType().getStructure();
            Vec3i min = closest.add(structure.getMinimumOffset());
            Vec3i max = closest.add(structure.getMaximumOffset());
            if (min.getX() < pos.getX() + 15 && max.getX() > pos.getX() && min.getZ() < pos.getZ() + 15 && max.getZ() > pos.getZ()) {
                return Stream.of(closest); //Continue generating another unfinished structure.
            }
        }

        if (!config.generatesInBiome(worldIn.getBiome(pos))) {
            return Stream.empty();
        }

        pos = pos.add(random.nextInt(16), 0, random.nextInt(16));
        pos = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos).down();

        if (!config.canPlace(worldIn, pos, random)) {
            return Stream.empty();
        }
        BlockPos at = this.getStructurePlacement(worldIn, pos, config.getStructureSize());
        if (at != null) {
            return Stream.of(at);
        }
        return Stream.empty();
    }
}
