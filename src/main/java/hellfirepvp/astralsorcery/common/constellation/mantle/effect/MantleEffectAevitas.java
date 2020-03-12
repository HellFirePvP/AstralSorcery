/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectAevitas
 * Created by HellFirePvP
 * Date: 11.03.2020 / 21:31
 */
public class MantleEffectAevitas extends MantleEffect {

    public static Config CONFIG = new Config("aevitas");

    public MantleEffectAevitas() {
        super(ConstellationsAS.aevitas);
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    protected void tickServer(PlayerEntity player) {
        super.tickServer(player);

        World world = player.getEntityWorld();
        BlockPos playerPos = player.getPosition();
        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                BlockPos at = playerPos.add(xx, -1, zz);
                MiscUtils.executeWithChunk(world, at, () -> {
                    if (!World.isOutsideBuildHeight(at) && world.getBlockState(at).isAir(world, at)) {
                        world.setBlockState(at, BlocksAS.VANISHING.getDefaultState());
                    }
                });
            }
        }

        //TODO secondary effect
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }
}
