/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.fluid;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockLiquidStarlight
 * Created by HellFirePvP
 * Date: 20.09.2019 / 21:21
 */
public class BlockLiquidStarlight extends FlowingFluidBlock {

    public BlockLiquidStarlight(Supplier<? extends FlowingFluid> fluidSupplier) {
        super(fluidSupplier, Block.Properties.create(Material.WATER)
                .doesNotBlockMovement()
                .lightValue(15)
                .hardnessAndResistance(100.0F)
                .noDrops());
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        Integer level = state.get(LEVEL);
        double percHeight = 1D - (((double) level + 1) / 8D);
        playLiquidStarlightBlockEffect(rand, new Vector3(pos).addY(percHeight * rand.nextFloat()), 1F);
        playLiquidStarlightBlockEffect(rand, new Vector3(pos).addY(percHeight * rand.nextFloat()), 1F);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playLiquidStarlightBlockEffect(Random rand, Vector3 at, float blockSize) {
        if (rand.nextInt(3) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.clone().add(
                            0.5 + rand.nextFloat() * (blockSize / 2) * (rand.nextBoolean() ? 1 : -1),
                            0,
                            0.5 + rand.nextFloat() * (blockSize / 2) * (rand.nextBoolean() ? 1 : -1)))
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.06F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(ColorsAS.ROCK_CRYSTAL));
        }
    }

}
