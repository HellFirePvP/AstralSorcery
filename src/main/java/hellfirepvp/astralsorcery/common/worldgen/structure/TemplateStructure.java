/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
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
 * Class: TemplateStructure
 * Created by HellFirePvP
 * Date: 06.10.2026 / 22:08
 */
public class TemplateStructure extends Structure {

    public static final MapCodec<TemplateStructure> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            settingsCodec(inst),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(TemplateStructure::getStartPool),
            Heightmap.Types.CODEC.optionalFieldOf("heightmap", Heightmap.Types.WORLD_SURFACE_WG).forGetter(TemplateStructure::getHeightmap),
            Codec.INT.optionalFieldOf("vertical_offset", 0).forGetter(TemplateStructure::getVerticalOffset),
            Codec.INT.optionalFieldOf("required_clearance", 0).forGetter(TemplateStructure::getRequiredClearance),
            Codec.INT.optionalFieldOf("bounding_box_expansion", 0).forGetter(TemplateStructure::getBoundingBoxExpansion),
            Codec.BOOL.optionalFieldOf("extend_bounds_to_build_height", false).forGetter(TemplateStructure::isExtendBoundsToBuildHeight),
            Codec.BOOL.optionalFieldOf("random_chunk_offset", true).forGetter(TemplateStructure::isRandomChunkOffset),
            Rotation.CODEC.optionalFieldOf("rotation").forGetter(TemplateStructure::getRotation),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", LiquidSettings.APPLY_WATERLOGGING).forGetter(TemplateStructure::getLiquidSettings)
    ).apply(inst, TemplateStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Heightmap.Types heightmap;
    private final int verticalOffset;
    private final int requiredClearance;
    private final int boundingBoxExpansion;
    private final boolean extendBoundsToBuildHeight;
    private final boolean randomChunkOffset;
    private final Optional<Rotation> rotation;
    private final LiquidSettings liquidSettings;

    public TemplateStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, Heightmap.Types heightmap,
                             int verticalOffset, int requiredClearance, int boundingBoxExpansion,
                             boolean extendBoundsToBuildHeight, boolean randomChunkOffset,
                             Optional<Rotation> rotation, LiquidSettings liquidSettings) {
        super(settings);
        this.startPool = startPool;
        this.heightmap = heightmap;
        this.verticalOffset = verticalOffset;
        this.requiredClearance = requiredClearance;
        this.boundingBoxExpansion = boundingBoxExpansion;
        this.extendBoundsToBuildHeight = extendBoundsToBuildHeight;
        this.randomChunkOffset = randomChunkOffset;
        this.rotation = rotation;
        this.liquidSettings = liquidSettings;
    }

    protected Holder<StructureTemplatePool> getStartPool() {
        return this.startPool;
    }

    protected Heightmap.Types getHeightmap() {
        return this.heightmap;
    }

    protected int getVerticalOffset() {
        return this.verticalOffset;
    }

    protected int getRequiredClearance() {
        return this.requiredClearance;
    }

    protected int getBoundingBoxExpansion() {
        return this.boundingBoxExpansion;
    }

    protected boolean isExtendBoundsToBuildHeight() {
        return this.extendBoundsToBuildHeight;
    }

    protected boolean isRandomChunkOffset() {
        return this.randomChunkOffset;
    }

    protected Optional<Rotation> getRotation() {
        return this.rotation;
    }

    protected LiquidSettings getLiquidSettings() {
        return this.liquidSettings;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        RandomSource rand = context.random();
        int x = chunkPos.getMinBlockX() + (this.isRandomChunkOffset() ? rand.nextInt(16) : 0);
        int z = chunkPos.getMinBlockZ() + (this.isRandomChunkOffset() ? rand.nextInt(16) : 0);
        int y = context.chunkGenerator().getFirstOccupiedHeight(x, z, this.getHeightmap(), context.heightAccessor(), context.randomState());
        BlockPos origin = new BlockPos(x, y, z);

        if (context.heightAccessor().isOutsideBuildHeight(origin.above(this.getRequiredClearance()))) {
            return Optional.empty();
        }

        Rotation rotation = this.getRotation().orElseGet(() -> Rotation.getRandom(rand));

        return Optional.of(new GenerationStub(origin, builder -> {
            StructurePoolElement poolElement = this.getStartPool().value().getRandomTemplate(context.random());
            BoundingBox structureBox = this.expand(
                    poolElement.getBoundingBox(context.structureTemplateManager(), origin, rotation), context.heightAccessor());

            builder.addPiece(new PoolElementStructurePiece(
                    context.structureTemplateManager(),
                    poolElement,
                    origin,
                    poolElement.getGroundLevelDelta() - this.getVerticalOffset(),
                    rotation,
                    structureBox,
                    this.getLiquidSettings()
            ));
        }));
    }

    private BoundingBox expand(BoundingBox box, LevelHeightAccessor heightAccessor) {
        int expansion = this.getBoundingBoxExpansion();
        return new BoundingBox(
                box.minX() - expansion, box.minY(), box.minZ() - expansion,
                box.maxX() + expansion,
                this.isExtendBoundsToBuildHeight() ? heightAccessor.getMaxBuildHeight() : box.maxY(),
                box.maxZ() + expansion
        );
    }

    @Override
    public StructureType<?> type() {
        return WorldGenAS.TEMPLATE_STRUCTURE.get();
    }
}
