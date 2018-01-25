/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.attributes;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.world.WorldGenAttribute;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeMarble
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:57
 */
public class GenAttributeMarble extends WorldGenAttribute {

    private final WorldGenMinable marbleMineable;

    public GenAttributeMarble() {
        super(0);
        marbleMineable = new WorldGenMinable(
                BlocksAS.blockMarble.getDefaultState().
                        withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW),
                Config.marbleVeinSize);
    }

    //WorldGenMinable has the offset built-in.
    //shifting it by 8 myself would just shift it 16 in total, not solving the problem.
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        for (int i = 0; i < Config.marbleAmount; i++) {
            int rX = (chunkX  * 16) + random.nextInt(16);
            int rY = 50 + random.nextInt(10);
            int rZ = (chunkZ  * 16) + random.nextInt(16);
            BlockPos pos = new BlockPos(rX, rY, rZ);
            marbleMineable.generate(world, random, pos);
        }
    }
}
