package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.block.marble.BlockMarblePillar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.observerlib.api.block.MatchableState;
import hellfirepvp.observerlib.api.block.SimpleMatchableBlock;
import hellfirepvp.observerlib.api.util.PatternBlockArray;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternFountain
 * Created by HellFirePvP
 * Date: 29.10.2020 / 19:51
 */
public class PatternFountain extends PatternBlockArray {

    public PatternFountain() {
        super(StructureTypesAS.PTYPE_FOUNTAIN.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState runed = BlocksAS.MARBLE_RUNED.getDefaultState();
        BlockState sooty = BlocksAS.BLACK_MARBLE_RAW.getDefaultState();

        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                for (int yy = -6; yy <= 3; yy++) {
                    if (Math.abs(xx) == 3 && Math.abs(zz) == 3) {
                        continue; //Exclude corners
                    }
                    if (xx == 0 && zz == 0 && Math.abs(yy) == 1) {
                        continue; //Exclude chalice and prime
                    }
                    this.addBlock(MatchableState.REQUIRES_AIR, xx, yy, zz);
                }
            }
        }

        addBlock(BlocksAS.FOUNTAIN, 0, 0, 0);

        addBlock(sooty,  4,  0,  0);
        addBlock(sooty, -4,  0,  0);
        addBlock(sooty,  0,  0,  4);
        addBlock(sooty,  0,  0, -4);

        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), 4,  1,  0);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), 4,  2,  0);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), 4, -1,  0);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), 4, -2,  0);

        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM),-4,  1,  0);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP),-4,  2,  0);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP),-4, -1,  0);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM),-4, -2,  0);

        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), 0,  1,  4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), 0,  2,  4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), 0, -1,  4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), 0, -2,  4);

        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), 0,  1, -4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), 0,  2, -4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), 0, -1, -4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), 0, -2, -4);

        addBlock(runed,  4,  0,  1);
        addBlock(runed,  4,  0,  2);
        addBlock(runed,  4,  0, -1);
        addBlock(runed,  4,  0, -2);

        addBlock(runed, -4,  0,  1);
        addBlock(runed, -4,  0,  2);
        addBlock(runed, -4,  0, -1);
        addBlock(runed, -4,  0, -2);

        addBlock(runed,  1,  0,  4);
        addBlock(runed,  2,  0,  4);
        addBlock(runed, -1,  0,  4);
        addBlock(runed, -2,  0,  4);

        addBlock(runed,  1,  0, -4);
        addBlock(runed,  2,  0, -4);
        addBlock(runed, -1,  0, -4);
        addBlock(runed, -2,  0, -4);

        addBlock(runed,  3,  0,  3);
        addBlock(runed,  3,  0, -3);
        addBlock(runed, -3,  0, -3);
        addBlock(runed, -3,  0,  3);
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
