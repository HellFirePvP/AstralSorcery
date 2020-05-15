package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.block.marble.BlockMarblePillar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.observerlib.api.block.MatchableState;
import hellfirepvp.observerlib.api.block.SimpleMatchableBlock;
import hellfirepvp.observerlib.api.util.PatternBlockArray;
import net.minecraft.block.BlockState;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternAttunementAltar
 * Created by HellFirePvP
 * Date: 18.11.2019 / 21:23
 */
public class PatternAttunementAltar extends PatternBlockArray {

    public PatternAttunementAltar() {
        super(StructureTypesAS.PTYPE_ATTUNEMENT_ALTAR.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState arch = BlocksAS.MARBLE_ARCH.getDefaultState();
        BlockState sooty = BlocksAS.BLACK_MARBLE_RAW.getDefaultState();

        addBlock(BlocksAS.ATTUNEMENT_ALTAR.getDefaultState(), 0, 0, 0);

        addBlockCube(arch, -7, -1, -8,  7, -1, -8);
        addBlockCube(arch, -7, -1,  8,  7, -1,  8);
        addBlockCube(arch, -8, -1, -7, -8, -1,  7);
        addBlockCube(arch,  8, -1, -7,  8, -1,  7);

        addBlockCube(sooty, -7, -1, -7,  7, -1,  7);

        pillar(-8, -0, -8);
        pillar(-8, -0,  8);
        pillar( 8, -0, -8);
        pillar( 8, -0,  8);

        addBlock(arch,-9, -1, -9);
        addBlock(arch,-9, -1, -8);
        addBlock(arch,-9, -1, -7);
        addBlock(arch,-8, -1, -9);
        addBlock(arch,-7, -1, -9);

        addBlock(arch,-9, -1,  9);
        addBlock(arch,-9, -1,  8);
        addBlock(arch,-9, -1,  7);
        addBlock(arch,-8, -1,  9);
        addBlock(arch,-7, -1,  9);

        addBlock(arch, 9, -1, -9);
        addBlock(arch, 9, -1, -8);
        addBlock(arch, 9, -1, -7);
        addBlock(arch, 8, -1, -9);
        addBlock(arch, 7, -1, -9);

        addBlock(arch, 9, -1,  9);
        addBlock(arch, 9, -1,  8);
        addBlock(arch, 9, -1,  7);
        addBlock(arch, 8, -1,  9);
        addBlock(arch, 7, -1,  9);
    }

    private void pillar(int x, int y, int z) {
        addBlock(BlocksAS.MARBLE_RUNED.getDefaultState(), x, y,     z);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), x, y + 1, z);
        addBlock(getPillarState(BlockMarblePillar.PillarType.MIDDLE), x, y + 2, z);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), x, y + 3, z);
        addBlock(BlocksAS.MARBLE_CHISELED.getDefaultState(), x, y + 4, z);
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
