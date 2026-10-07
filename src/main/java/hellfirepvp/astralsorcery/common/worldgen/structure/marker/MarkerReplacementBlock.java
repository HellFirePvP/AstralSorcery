/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.structure.marker;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.StructureMarkerReplacementTypesAS;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MarkerReplacementBlock
 * Created by HellFirePvP
 * Date: 06.10.2026 / 19:02
 */
public class MarkerReplacementBlock extends StructureMarkerReplacement {

    public static final Type<MarkerReplacementBlock> TYPE = new Type<>(RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockStateProvider.CODEC.fieldOf("state").forGetter(MarkerReplacementBlock::getStateProvider)
    ).apply(inst, MarkerReplacementBlock::new)));

    private final BlockStateProvider stateProvider;

    public MarkerReplacementBlock(BlockStateProvider stateProvider) {
        this.stateProvider = stateProvider;
    }

    public static MarkerReplacementBlock of(Supplier<? extends Block> block) {
        return of(block.get());
    }

    public static MarkerReplacementBlock of(Block block) {
        return of(block.defaultBlockState());
    }

    public static MarkerReplacementBlock of(BlockState state) {
        return new MarkerReplacementBlock(BlockStateProvider.simple(state));
    }

    public BlockStateProvider getStateProvider() {
        return this.stateProvider;
    }

    @Override
    public Type<?> getType() {
        return StructureMarkerReplacementTypesAS.BLOCK.get();
    }

    @Override
    public StructureTemplate.StructureBlockInfo replace(LevelReader level, StructureTemplate.StructureBlockInfo marker, RandomSource rand) {
        return new StructureTemplate.StructureBlockInfo(marker.pos(), this.getStateProvider().getState(rand, marker.pos()), null);
    }
}
