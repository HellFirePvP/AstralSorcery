/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.world;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SkyCollectionHelper
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:14
 */
public class SkyCollectionHelper {

    private static final int accuracy = 32;
    private static final Random sharedRand = new Random();

    @OnlyIn(Dist.CLIENT)
    public static Optional<Float> getSkyNoiseDistributionClient(World world, BlockPos pos) {
        return WorldSeedCache.getSeedIfPresent(world).map(seed -> getDistributionInternal(seed, pos));
    }

    public static float getSkyNoiseDistribution(World world, BlockPos pos) {
        return getDistributionInternal(MiscUtils.getRandomWorldSeed(world), pos);
    }

    private static float getDistributionInternal(long seed, BlockPos pos) {
        BlockPos lowerAnchorPoint = new BlockPos(
                (int) Math.floor((float) pos.getX() / accuracy) * accuracy,
                0,
                (int) Math.floor((float) pos.getZ() / accuracy) * accuracy);
        float layer0 = getNoiseDistribution(seed,
                lowerAnchorPoint,
                lowerAnchorPoint.add(accuracy, 0, 0),
                lowerAnchorPoint.add(0, 0, accuracy),
                lowerAnchorPoint.add(accuracy, 0, accuracy),
                pos);
        return layer0 * layer0;
    }

    private static float getNoiseDistribution(long seed, BlockPos lXlZ, BlockPos hXlZ, BlockPos lXhZ, BlockPos hXhZ, BlockPos exact) {
        float nll = getNoise(seed, lXlZ.getX(), lXlZ.getZ());
        float nhl = getNoise(seed, hXlZ.getX(), hXlZ.getZ());
        float nlh = getNoise(seed, lXhZ.getX(), lXhZ.getZ());
        float nhh = getNoise(seed, hXhZ.getX(), hXhZ.getZ());

        float xPart = Math.abs(((float) (exact.getX() - lXlZ.getX()) ) / accuracy);
        float zPart = Math.abs(((float) (exact.getZ() - lXlZ.getZ()) ) / accuracy);

        return cosInterpolate(cosInterpolate(nll, nhl, xPart), cosInterpolate(nlh, nhh, xPart), zPart);
    }

    private static float cosInterpolate(float l, float h, float partial) {
        float t2 = (1F - MathHelper.cos((float) (partial * Math.PI))) / 2F;
        return (l * (1F - t2) + h * t2);
    }

    private static float getNoise(long seed, int posX, int posZ) {
        sharedRand.setSeed(
                simple_hash(new int[] {
                        (int) (seed),
                        (int) (seed >> 32),
                        posX,
                        posZ
                }, 4)
        );
        sharedRand.nextLong();
        return sharedRand.nextFloat();
    }

    private static int simple_hash(int[] is, int count) {
        int hash = 80238287;
        for (int i = 0; i < count; i++) {
            hash = (hash << 4) ^ (hash >> 28) ^ (is[i] * 5449 % 130651);
        }
        return hash % 75327403;
    }

}
