/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.processor;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.block.FlareLightBlock;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FlareLightColorizationProcessor
 * Created by HellFirePvP
 * Date: 07.10.2026 / 23:16
 */
public class FlareLightColorizationProcessor extends StructureProcessor {

    public static final MapCodec<FlareLightColorizationProcessor> CODEC = MapCodec.unit(FlareLightColorizationProcessor::getInstance);
    private static final FlareLightColorizationProcessor INSTANCE = new FlareLightColorizationProcessor();

    private FlareLightColorizationProcessor() {}

    public static FlareLightColorizationProcessor getInstance() {
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
            BlockState randomColor = BlocksAS.FLARE_LIGHT.get().defaultBlockState().setValue(FlareLightBlock.COLOR,
                    MiscUtil.getRandomEntry(settings.getRandom(currentBlockInfo.pos()), DyeColor.values()).orElse(DyeColor.WHITE));
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), randomColor, currentBlockInfo.nbt());
        }
        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WorldGenAS.FLARE_LIGHT_COLORIZATION_PROCESSOR.get();
    }
}
