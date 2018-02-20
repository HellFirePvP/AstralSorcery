/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.attributes;

import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.world.WorldGenAttribute;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeAquamarine
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:59
 */
public class GenAttributeAquamarine extends WorldGenAttribute {

    public GenAttributeAquamarine() {
        super(0);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        for (int i = 0; i < Config.aquamarineAmount; i++) {
            int rX = (chunkX  * 16) + random.nextInt(16) + 8;
            int rY = 48 + random.nextInt(19);
            int rZ = (chunkZ  * 16) + random.nextInt(16) + 8;
            BlockPos pos = new BlockPos(rX, rY, rZ);
            IBlockState stateAt = world.getBlockState(pos);
            if(!stateAt.getBlock().equals(Blocks.SAND)) {
                continue;
            }

            boolean foundWater = false;
            for (int yy = 0; yy < 2; yy++) {
                BlockPos check = pos.offset(EnumFacing.UP, yy);
                IBlockState bs = world.getBlockState(check);
                Block block = bs.getBlock();
                if((block instanceof BlockLiquid && bs.getMaterial() == Material.WATER) ||
                        block.equals(Blocks.ICE) || block.equals(Blocks.PACKED_ICE) || block.equals(Blocks.FROSTED_ICE)) {
                    foundWater = true;
                    break;
                }
            }
            if(!foundWater)
                continue;

            world.setBlockState(pos, BlocksAS.customSandOre.getDefaultState()
                    .withProperty(BlockCustomSandOre.ORE_TYPE, BlockCustomSandOre.OreType.AQUAMARINE));
        }
    }
}
