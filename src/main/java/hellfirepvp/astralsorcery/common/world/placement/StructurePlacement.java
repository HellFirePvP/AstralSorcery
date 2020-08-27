/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.world.config.StructurePlacementConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructurePlacement
 * Created by HellFirePvP
 * Date: 23.07.2019 / 07:34
 */
public class StructurePlacement<DC extends StructurePlacementConfig> extends Placement<NoPlacementConfig> {

    private final DC config;

    public StructurePlacement(DC placementConfig) {
        super(NoPlacementConfig.field_236555_a_);
        this.config = placementConfig;

    }

    @Override
    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper decorationHelper, Random random, NoPlacementConfig config, BlockPos pos) {
        if (!this.config.canGenerateAtAll()) {
            return Stream.empty();
        }
        BlockPos generationPos = this.getStructurePosition(decorationHelper, random, this.config.getStructureSize(), new ChunkPos(pos));
        return generationPos != null ? Stream.of(generationPos) : Stream.empty();
    }

    @Nullable
    public BlockPos getStructurePosition(WorldDecoratingHelper decorationHelper, Random random, int structureSize, ChunkPos chunkPos) {
        BlockPos pos = chunkPos.asBlockPos().add(random.nextInt(16), 0, random.nextInt(16));
        int x = chunkPos.getXStart() + 8 + random.nextInt(16);
        int z = chunkPos.getZStart() + 8 + random.nextInt(16);
        int size = structureSize / 2;
        int sizeX = MathHelper.floor(structureSize / 2F);
        int sizeZ = MathHelper.ceil(structureSize / 2F);

        int lxlz = this.getHeight(decorationHelper, pos.add(-sizeX, 0, -sizeZ), Heightmap.Type.MOTION_BLOCKING);
        int lxhz = this.getHeight(decorationHelper, pos.add(-sizeX, 0,  sizeZ), Heightmap.Type.MOTION_BLOCKING);
        int hxlz = this.getHeight(decorationHelper, pos.add( sizeX, 0, -sizeZ), Heightmap.Type.MOTION_BLOCKING);
        int hxhz = this.getHeight(decorationHelper, pos.add( sizeX, 0,  sizeZ), Heightmap.Type.MOTION_BLOCKING);
        int y = (lxlz + lxhz + hxlz + hxhz) / 4;
        BlockPos at = new BlockPos(x, y, z);
        return this.hasSafeFoundation(decorationHelper, at, size) ? at : null;
    }

    protected boolean hasSafeFoundation(WorldDecoratingHelper decorationHelper, BlockPos pos, int structSize) {
        int y = pos.getY();
        int diff = structSize / 2;
        int offsetY = this.getHeight(decorationHelper, pos, Heightmap.Type.OCEAN_FLOOR_WG);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        offsetY = this.getHeight(decorationHelper, pos.add(structSize, 0, structSize), Heightmap.Type.MOTION_BLOCKING);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        offsetY = this.getHeight(decorationHelper, pos.add(-structSize, 0, structSize), Heightmap.Type.MOTION_BLOCKING);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        offsetY = this.getHeight(decorationHelper, pos.add(-structSize, 0, -structSize), Heightmap.Type.MOTION_BLOCKING);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        offsetY = this.getHeight(decorationHelper, pos.add(structSize, 0, -structSize), Heightmap.Type.MOTION_BLOCKING);
        if (Math.abs(offsetY - y) > diff) {
            return false;
        }
        return true;
    }

    private int getHeight(WorldDecoratingHelper decorationHelper, BlockPos pos, Heightmap.Type type) {
        return decorationHelper.func_242893_a(type, pos.getX(), pos.getZ());
    }
}
