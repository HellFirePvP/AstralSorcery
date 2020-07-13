/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXScaleFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.source.FXSourceLiquidFountain;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.BlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscPlayEffect
 * Created by HellFirePvP
 * Date: 31.08.2019 / 20:46
 */
public class MiscPlayEffect {

    private static final Random rand = new Random();

    @OnlyIn(Dist.CLIENT)
    public static void fireLightning(PktPlayEffect effect) {
        Vector3 start = ByteBufUtils.readVector(effect.getExtraData());
        Vector3 end   = ByteBufUtils.readVector(effect.getExtraData());
        Color color = Color.WHITE;
        if (effect.getExtraData().isReadable()) {
            color = new Color(effect.getExtraData().readInt(), true);
        }
        EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                .spawn(start)
                .makeDefault(end)
                .color(VFXColorFunction.constant(color));
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSingleBlockTumbleDepthEffect(Vector3 at, BlockState displayState) {
        EffectHelper.of(EffectTemplatesAS.BLOCK_TRANSLUCENT_IGNORE_DEPTH)
                .spawn(at.clone())
                .tumble()
                .setBlockState(displayState)
                .setMotion(new Vector3(0, 0.035, 0))
                .scale(VFXScaleFunction.SHRINK_EXP)
                .setMaxAge(40 + rand.nextInt(10));
    }

    @OnlyIn(Dist.CLIENT)
    public static void playTumbleBlockEffects(PktPlayEffect event) {
        BlockPos pos = ByteBufUtils.readPos(event.getExtraData());
        BlockState state = ByteBufUtils.readBlockState(event.getExtraData());
        Vector3 vec = new Vector3(pos).add(0.5F, 0.5F, 0.5F);

        playBlockParticles(state, pos);

        EffectHelper.of(EffectTemplatesAS.BLOCK_TRANSLUCENT)
                .spawn(vec)
                .tumble()
                .setBlockState(state)
                .setMotion(new Vector3(0, 0.035, 0))
                .scale(VFXScaleFunction.SHRINK_EXP)
                .setMaxAge(20 + rand.nextInt(15));
    }

    @OnlyIn(Dist.CLIENT)
    public static void playBlockEffects(PktPlayEffect event) {
        BlockPos pos = ByteBufUtils.readPos(event.getExtraData());
        BlockState state = ByteBufUtils.readBlockState(event.getExtraData());
        playBlockParticles(state, pos);
    }

    @OnlyIn(Dist.CLIENT)
    private static void playBlockParticles(BlockState state, BlockPos pos) {
        RenderingUtils.playBlockBreakParticles(pos, null, state);

        Vector3 vec = new Vector3(pos).add(0.5F, 0.5F, 0.5F);
        for (int i = 0; i < 6; i++) {
            Vector3 at = vec.add(
                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .setMotion(Vector3.random().multiply(0.045F))
                    .setScaleMultiplier(0.25F + rand.nextFloat() * 0.1F)
                    .color(VFXColorFunction.WHITE);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void liquidFountain(PktPlayEffect event) {
        FluidStack stack = ByteBufUtils.readFluidStack(event.getExtraData());
        Vector3 at = ByteBufUtils.readVector(event.getExtraData())
                .add(rand.nextFloat(), 0, rand.nextFloat());

        EffectHelper.spawnSource(new FXSourceLiquidFountain(at, stack));
    }

    @OnlyIn(Dist.CLIENT)
    public static void catalystBurst(PktPlayEffect event) {
        Vector3 vec = ByteBufUtils.readVector(event.getExtraData());

        BatchRenderContext<? extends FXFacingParticle> ctx;
        switch (rand.nextInt(3)) {
            case 2:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_3;
                break;
            case 1:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_2;
                break;
            default:
            case 0:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_1;
                break;
        }
        EffectHelper.of(ctx)
                .spawn(vec)
                .setScaleMultiplier(1.5F);
    }

    @OnlyIn(Dist.CLIENT)
    public static void gemCrystalBurst(PktPlayEffect event) {
        Vector3 vec = ByteBufUtils.readVector(event.getExtraData());
        BlockGemCrystalCluster.GrowthStageType type = MiscUtils.getEnumEntry(BlockGemCrystalCluster.GrowthStageType.class, event.getExtraData().readInt());

        BatchRenderContext<? extends FXFacingParticle> ctx = EffectTemplatesAS.GEM_CRYSTAL_BURST;
        float scale = 0.5F;
        switch (type) {
            case STAGE_2_SKY:
                ctx = EffectTemplatesAS.GEM_CRYSTAL_BURST_SKY;
                scale = 1.2F;
                break;
            case STAGE_2_DAY:
                ctx = EffectTemplatesAS.GEM_CRYSTAL_BURST_DAY;
                scale = 1.2F;
                break;
            case STAGE_2_NIGHT:
                ctx = EffectTemplatesAS.GEM_CRYSTAL_BURST_NIGHT;
                scale = 1.2F;
                break;
        }
        EffectHelper.of(ctx)
                .spawn(vec.add(0.5, 0.3, 0.5))
                .setScaleMultiplier(scale);
    }
}
