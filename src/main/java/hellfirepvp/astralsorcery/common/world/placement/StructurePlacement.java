/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import com.mojang.datafixers.Dynamic;
import hellfirepvp.astralsorcery.common.world.config.StructurePlacementConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructurePlacement
 * Created by HellFirePvP
 * Date: 23.07.2019 / 07:34
 */
public class StructurePlacement<DC extends StructurePlacementConfig> extends Placement<DC> {

    public StructurePlacement(Function<Dynamic<?>, ? extends DC> cfgFactory) {
        super(cfgFactory);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, DC configIn, BlockPos pos) {
        BlockPos generationPos = this.getStructurePosition(generatorIn, random, configIn.getStructureSize(), new ChunkPos(pos));
        return generationPos != null ? Stream.of(generationPos) : Stream.empty();
    }

    @Nullable
    public BlockPos getStructurePosition(ChunkGenerator<?> chunkGen, Random random, int structureSize, ChunkPos chunkPos) {
        BlockPos pos = chunkPos.asBlockPos().add(random.nextInt(16), 0, random.nextInt(16));
        int x = chunkPos.getXStart() + 8 + random.nextInt(16);
        int z = chunkPos.getZStart() + 8 + random.nextInt(16);
        int size = structureSize / 2;
        int sizeX = MathHelper.floor(structureSize / 2F);
        int sizeZ = MathHelper.ceil(structureSize / 2F);

        int lxlz = this.getHeight(chunkGen, pos.add(-sizeX, 0, -sizeZ), Heightmap.Type.OCEAN_FLOOR_WG);
        int lxhz = this.getHeight(chunkGen, pos.add(-sizeX, 0,  sizeZ), Heightmap.Type.OCEAN_FLOOR_WG);
        int hxlz = this.getHeight(chunkGen, pos.add( sizeX, 0, -sizeZ), Heightmap.Type.OCEAN_FLOOR_WG);
        int hxhz = this.getHeight(chunkGen, pos.add( sizeX, 0,  sizeZ), Heightmap.Type.OCEAN_FLOOR_WG);
        int y = (lxlz + lxhz + hxlz + hxhz) / 4;
        BlockPos at = new BlockPos(x, y, z);
        return this.hasSafeFoundation(chunkGen, at, size) ? at : null;
    }

    protected boolean hasSafeFoundation(ChunkGenerator<?> chunkGen, BlockPos pos, int structSize) {
        int y = pos.getY();
        int diff = structSize / 2;
        int offsetY = this.getHeight(chunkGen, pos, Heightmap.Type.OCEAN_FLOOR_WG);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        offsetY = this.getHeight(chunkGen, pos.add(structSize, 0, structSize), Heightmap.Type.OCEAN_FLOOR_WG);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        offsetY = this.getHeight(chunkGen, pos.add(-structSize, 0, structSize), Heightmap.Type.OCEAN_FLOOR_WG);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        offsetY = this.getHeight(chunkGen, pos.add(-structSize, 0, -structSize), Heightmap.Type.OCEAN_FLOOR_WG);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        offsetY = this.getHeight(chunkGen, pos.add(structSize, 0, -structSize), Heightmap.Type.OCEAN_FLOOR_WG);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        return true;
    }

    private int getHeight(ChunkGenerator<?> chunkGen, BlockPos pos, Heightmap.Type type) {
        return chunkGen.func_222531_c(pos.getX(), pos.getZ(), type);
    }
}
