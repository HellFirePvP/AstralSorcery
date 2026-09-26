/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import hellfirepvp.astralsorcery.common.focal.FocalPointManager;
import hellfirepvp.astralsorcery.common.focal.node.BasicFocalPointNode;
import hellfirepvp.astralsorcery.common.util.data.ColumnPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointRegisterProcessor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointRegisterProcessor extends StructureProcessor {

    public static final MapCodec<FocalPointRegisterProcessor> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistryCodecs.homogeneousList(RegistriesAS.KEY_CONSTELLATIONS).fieldOf("available_constellations").forGetter(FocalPointRegisterProcessor::getAvailableConstellations)
    ).apply(inst, FocalPointRegisterProcessor::new));

    private final HolderSet<BaseConstellation> availableConstellations;

    public FocalPointRegisterProcessor(HolderSet<BaseConstellation> availableConstellations) {
        this.availableConstellations = availableConstellations;
    }

    public HolderSet<BaseConstellation> getAvailableConstellations() {
        return this.availableConstellations;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor levelGen, BlockPos offset, BlockPos pos, List<StructureTemplate.StructureBlockInfo> originalBlockInfos, List<StructureTemplate.StructureBlockInfo> processedBlockInfos, StructurePlaceSettings settings) {
        if (settings.getBoundingBox() != null && settings.getBoundingBox().isInside(offset)) {
            // The data marker pass. (Either no structure blocks, or all structure blocks)
            // They're normally filtered out. If the structure has no blocks, this doesn't run anyway.
            if (originalBlockInfos.isEmpty() || originalBlockInfos.stream().allMatch(info -> info.state().is(Blocks.STRUCTURE_BLOCK))) {
                ServerLevel actualLevel = levelGen.getLevel();

                this.getAvailableConstellations().getRandomElement(settings.getRandom(offset)).ifPresent(cstHolder -> {
                    // Only load then node if it's a server world, i.e. not during chunk generation
                    // The node will get loaded when the chunk is loaded, this processor runs pre-chunk loading
                    FocalPointManager.getInstance().addNewNode(actualLevel, new BasicFocalPointNode(ColumnPos.of(offset), cstHolder.value()), levelGen instanceof ServerLevel);
                });
            }
        }
        return processedBlockInfos;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WorldGenAS.FOCAL_POINT_REGISTER_PROCESSOR.get();
    }
}
