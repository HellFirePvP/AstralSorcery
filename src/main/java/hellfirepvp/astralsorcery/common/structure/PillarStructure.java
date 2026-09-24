/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.block.PillarBlock;
import hellfirepvp.observerlib.api.block.MatchableState;
import hellfirepvp.observerlib.api.block.SimpleMatchableBlock;
import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PillarStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface PillarStructure {

    default <T extends PillarBlock> void addPillar(DeferredBlock<T> block, int x, int y, int z, int height) {
        BlockArray blockArray = (BlockArray) this;

        if (height == 1) {
            blockArray.addBlock(getPillarState(block, PillarBlock.PillarType.MIDDLE), x, y, z);
        } else {
            blockArray.addBlock(getPillarState(block, PillarBlock.PillarType.BOTTOM), x, y, z);
            for (int i = 1; i < height - 1; i++) {
                blockArray.addBlock(getPillarState(block, PillarBlock.PillarType.MIDDLE), x, y + i, z);
            }
            blockArray.addBlock(getPillarState(block, PillarBlock.PillarType.TOP), x, y + height - 1, z);
        }
    }

    default <T extends PillarBlock> MatchableState getPillarState(DeferredBlock<T> block, PillarBlock.PillarType type) {
        return getPillarState(block.get(), type);
    }

    default <T extends PillarBlock> MatchableState getPillarState(T block, PillarBlock.PillarType type) {
        return new SimpleMatchableBlock(block) {
            @Nonnull
            @Override
            public BlockState getDescriptiveState(long tick) {
                return block.defaultBlockState().setValue(PillarBlock.PILLAR_TYPE, type);
            }
        };
    }

}
