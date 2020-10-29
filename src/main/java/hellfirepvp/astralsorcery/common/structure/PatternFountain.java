package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.observerlib.api.util.PatternBlockArray;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;

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
        BlockState pillar = BlocksAS.MARBLE_PILLAR.getDefaultState();
        BlockState sooty = BlocksAS.BLACK_MARBLE_RAW.getDefaultState();

        addBlock(BlocksAS.FOUNTAIN, 0, 0, 0);

        addBlock(sooty,  4,  0,  0);
        addBlock(sooty, -4,  0,  0);
        addBlock(sooty,  0,  0,  4);
        addBlock(sooty,  0,  0, -4);

        addBlock(pillar, 4,  1,  0);
        addBlock(pillar, 4,  2,  0);
        addBlock(pillar, 4, -1,  0);
        addBlock(pillar, 4, -2,  0);

        addBlock(pillar,-4,  1,  0);
        addBlock(pillar,-4,  2,  0);
        addBlock(pillar,-4, -1,  0);
        addBlock(pillar,-4, -2,  0);

        addBlock(pillar, 0,  1,  4);
        addBlock(pillar, 0,  2,  4);
        addBlock(pillar, 0, -1,  4);
        addBlock(pillar, 0, -2,  4);

        addBlock(pillar, 0,  1, -4);
        addBlock(pillar, 0,  2, -4);
        addBlock(pillar, 0, -1, -4);
        addBlock(pillar, 0, -2, -4);

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

        for (int yy = -2; yy <= 2; yy++) {
            for (int xx = -3; xx <= 3; xx++) {
                for (int zz = -3; zz <= 3; zz++) {
                    if(Math.abs(xx) == 3 && Math.abs(zz) == 3) continue; //corners

                    if(xx == 0 && zz == 0) {
                        if (yy == -2) {
                            addBlock(Blocks.AIR, xx, yy, zz);
                        }
                    } else {
                        addBlock(Blocks.AIR, xx, yy, zz);
                    }
                }
            }
        }
    }
}
