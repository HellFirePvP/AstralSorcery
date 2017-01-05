/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscEffectUtil
 * Created by HellFirePvP
 * Date: 06.08.2016 / 01:56
 */
public class MiscEffectUtil {

    private static final ParticleDigging.Factory diggingFactory = new ParticleDigging.Factory();

    public static void doBlockBreakParticles(World world, BlockPos pos, IBlockState dummyState, ParticleManager manager) {
        for (int xx = 0; xx < 4; ++xx) {
            for (int yy = 0; yy < 4; ++yy) {
                for (int zz = 0; zz < 4; ++zz) {
                    double pX = (double) pos.getX() + ((double) xx + 0.5D) / 4.0D;
                    double pY = (double) pos.getY() + ((double) yy + 0.5D) / 4.0D;
                    double pZ = (double) pos.getZ() + ((double) zz + 0.5D) / 4.0D;
                    Particle p = diggingFactory.createParticle(0, world, pX, pY, pZ,
                            pX - (double) pos.getX() - 0.5D, pY - (double) pos.getY() - 0.5D, pZ - (double) pos.getZ() - 0.5D,
                            Block.getStateId(dummyState));
                    manager.addEffect(p);
                }
            }
        }
    }

}
