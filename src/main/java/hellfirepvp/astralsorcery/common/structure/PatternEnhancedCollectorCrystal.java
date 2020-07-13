/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.block.marble.BlockMarblePillar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.observerlib.api.block.MatchableState;
import hellfirepvp.observerlib.api.block.SimpleMatchableBlock;
import hellfirepvp.observerlib.api.util.PatternBlockArray;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternEnhancedCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 23:13
 */
public class PatternEnhancedCollectorCrystal extends PatternBlockArray {

    public PatternEnhancedCollectorCrystal() {
        super(StructureTypesAS.PTYPE_ENHANCED_COLLECTOR_CRYSTAL.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState chiseled = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState raw = BlocksAS.MARBLE_RAW.getDefaultState();
        BlockState runed = BlocksAS.MARBLE_RUNED.getDefaultState();
        BlockState engraved = BlocksAS.MARBLE_ENGRAVED.getDefaultState();

        addBlockCube(raw, -1, -5, -1, 1, -5, 1);
        addBlockCube(Blocks.AIR.getDefaultState(), 1, 1, 1, -1, -1, -1);
        for (BlockPos offset : TileCollectorCrystal.OFFSETS_LIQUID_STARLIGHT) {
            addBlock(BlocksAS.FLUID_LIQUID_STARLIGHT.getDefaultState(), offset.getX(), offset.getY(), offset.getZ());
        }

        addBlock(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL.getDefaultState(), 0, 0, 0);

        addBlock(chiseled, 0, -2, 0);
        addBlock(getPillarState(BlockMarblePillar.PillarType.MIDDLE), 0, -3, 0);
        addBlock(engraved, 0, -4, 0);

        addBlock(chiseled, -2, -4, -2);
        addBlock(chiseled, -2, -4,  2);
        addBlock(chiseled,  2, -4,  2);
        addBlock(chiseled,  2, -4, -2);
        addBlock(engraved, -2, -3, -2);
        addBlock(engraved, -2, -3,  2);
        addBlock(engraved,  2, -3,  2);
        addBlock(engraved,  2, -3, -2);

        addBlock(runed, -2, -4, -1);
        addBlock(runed, -2, -4,  0);
        addBlock(runed, -2, -4,  1);
        addBlock(runed,  2, -4, -1);
        addBlock(runed,  2, -4,  0);
        addBlock(runed,  2, -4,  1);
        addBlock(runed, -1, -4, -2);
        addBlock(runed,  0, -4, -2);
        addBlock(runed,  1, -4, -2);
        addBlock(runed, -1, -4,  2);
        addBlock(runed,  0, -4,  2);
        addBlock(runed,  1, -4,  2);
    }

    private MatchableState getPillarState(BlockMarblePillar.PillarType type) {
        return new SimpleMatchableBlock(BlocksAS.MARBLE_PILLAR) {
            @Nonnull
            @Override
            public BlockState getDescriptiveState(long tick) {
                return BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, type);
            }
        };
    }
}
