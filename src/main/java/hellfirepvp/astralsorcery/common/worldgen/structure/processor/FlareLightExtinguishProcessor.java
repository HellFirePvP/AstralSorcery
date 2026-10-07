/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.processor;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FlareLightExtinguishProcessor
 * Created by HellFirePvP
 * Date: 07.10.2026 / 15:20
 */
public class FlareLightExtinguishProcessor extends StructureProcessor {

    public static final MapCodec<FlareLightExtinguishProcessor> CODEC = MapCodec.unit(FlareLightExtinguishProcessor::getInstance);
    private static final FlareLightExtinguishProcessor INSTANCE = new FlareLightExtinguishProcessor();

    private FlareLightExtinguishProcessor() {}

    public static FlareLightExtinguishProcessor getInstance() {
        return INSTANCE;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader level,
                                                        BlockPos offset,
                                                        BlockPos pos,
                                                        StructureTemplate.StructureBlockInfo originalBlockInfo,
                                                        StructureTemplate.StructureBlockInfo currentBlockInfo,
                                                        StructurePlaceSettings settings,
                                                        @Nullable StructureTemplate template) {
        if (currentBlockInfo.state().is(BlocksAS.FLARE_LIGHT)) {
            boolean shouldExtinguish = false;
            BlockState resultState = Blocks.AIR.defaultBlockState();
            for (Direction dir : Direction.values()) {
                if (dir == Direction.DOWN) continue;

                if (level.getFluidState(currentBlockInfo.pos().relative(dir)).is(FluidTags.WATER)) {
                    shouldExtinguish = true;
                    if (dir == Direction.UP) {
                        resultState = Blocks.WATER.defaultBlockState();
                    }
                    break;
                }
            }
            if (shouldExtinguish) {
                return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), resultState, null);
            }
        }
        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WorldGenAS.FLARE_LIGHT_EXTINGUISH_PROCESSOR.get();
    }
}
