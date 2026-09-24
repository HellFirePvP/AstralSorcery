/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointStructure extends Structure {

    public static final MapCodec<FocalPointStructure> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            settingsCodec(inst),
            StructureTemplatePool.CODEC.fieldOf("structure").forGetter(FocalPointStructure::getStructure)
    ).apply(inst, FocalPointStructure::new));

    private final Holder<StructureTemplatePool> structure;

    public FocalPointStructure(StructureSettings settings, Holder<StructureTemplatePool> structure) {
        super(settings);
        this.structure = structure;
    }

    protected Holder<StructureTemplatePool> getStructure() {
        return this.structure;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        RandomSource rand = context.random();
        int x = chunkpos.getMinBlockX() + rand.nextInt(16);
        int z = chunkpos.getMinBlockZ() + rand.nextInt(16);
        int y = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        BlockPos chunkCenter = new BlockPos(x, y + 8, z);

        if (context.heightAccessor().isOutsideBuildHeight(chunkCenter.above(16))) {
            return Optional.empty();
        }

        return Optional.of(new GenerationStub(chunkCenter, builder -> {
            StructurePoolElement poolElement = structure.value().getRandomTemplate(context.random());
            BoundingBox structureBox = poolElement.getBoundingBox(context.structureTemplateManager(), chunkCenter, Rotation.NONE);
            structureBox = new BoundingBox(
                    structureBox.minX() - 7, structureBox.minY() - 7, structureBox.minZ() - 7,
                    structureBox.maxX() + 7, context.heightAccessor().getMaxBuildHeight(), structureBox.maxZ() + 7
            );

            builder.addPiece(new PoolElementStructurePiece(
                    context.structureTemplateManager(),
                    poolElement,
                    chunkCenter,
                    poolElement.getGroundLevelDelta(),
                    Rotation.NONE,
                    structureBox,
                    LiquidSettings.IGNORE_WATERLOGGING
            ));
        }));
    }

    @Override
    public StructureType<?> type() {
        return WorldGenAS.FOCAL_POINT_STRUCTURE.get();
    }
}
