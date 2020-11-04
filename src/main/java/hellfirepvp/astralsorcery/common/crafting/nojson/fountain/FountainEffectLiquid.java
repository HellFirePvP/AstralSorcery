package hellfirepvp.astralsorcery.common.crafting.nojson.fountain;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.RefreshFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXSpritePlane;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.common.block.tile.fountain.BlockFountainPrime;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.CapabilitiesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileFountain;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FountainEffectLiquid
 * Created by HellFirePvP
 * Date: 01.11.2020 / 11:23
 */
public class FountainEffectLiquid extends FountainEffect<LiquidContext> {

    public FountainEffectLiquid() {
        super(AstralSorcery.key("effect_liquid"));
    }

    @Nonnull
    @Override
    public BlockFountainPrime getAssociatedPrime() {
        return BlocksAS.FOUNTAIN_PRIME_LIQUID;
    }

    @Nonnull
    @Override
    public LiquidContext createContext(TileFountain fountain) {
        return new LiquidContext(fountain.getPos());
    }

    @Override
    public void tick(TileFountain fountain, LiquidContext ctx, int operationTick, LogicalSide side, OperationSegment currentSegment) {
        if (side.isClient()) {
            tickEffects(fountain, ctx, operationTick, currentSegment);
            return;
        }

        if (currentSegment.isLaterOrEqualTo(OperationSegment.RUNNING)) {
            World w = fountain.getWorld();
            if (fountain.getTicksExisted() % 32 == 0) {
                digCone(w, ctx);
            }

            if (ctx.tickLiquidProduction()) {
                ctx.resetLiquidProductionTick(rand);
                produceLiquid(fountain);
            }
        }
    }

    private void produceLiquid(TileFountain fountain) {
        Chunk ch = fountain.getWorld().getChunkAt(fountain.getPos());
        ch.getCapability(CapabilitiesAS.CHUNK_FLUID).ifPresent(entry -> {
            int drain = 200 + rand.nextInt(400);
            FluidStack drained;
            if (!entry.isEmpty() && entry.isInitialized()) {
                drained = entry.drain(drain, IFluidHandler.FluidAction.SIMULATE);
            } else {
                drained = new FluidStack(Fluids.WATER, drain);
            }

            int fillable = fountain.getTank().fill(drained, IFluidHandler.FluidAction.SIMULATE);
            if (fillable > 0) {
                FluidStack actual = entry.drain(fillable, IFluidHandler.FluidAction.EXECUTE);
                fountain.getTank().fill(actual, IFluidHandler.FluidAction.EXECUTE);
            }
        });
    }

    private void digCone(World world, LiquidContext ctx) {
        if (world instanceof ServerWorld) {
            dig((ServerWorld) world, ctx.getDigPositions());
        }
    }

    private void dig(ServerWorld world, List<BlockPos> positions) {
        BlockDropCaptureAssist.startCapturing();
        try {
            positions.forEach(pos -> {
                MiscUtils.executeWithChunk(world, pos, () -> {
                    BlockState state = world.getBlockState(pos);
                    if (!state.isAir(world, pos) &&
                            world.getTileEntity(pos) == null &&
                            state.getBlockHardness(world, pos) >= 0 &&
                            !BlockUtils.isFluidBlock(state)) {
                        BlockUtils.breakBlockWithoutPlayer(world, pos, state, ItemStack.EMPTY, true, true, false);
                    }
                });
            });
        } finally {
            BlockDropCaptureAssist.getCapturedStacksAndStop();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffects(TileFountain fountain, LiquidContext ctx, int operationTick, OperationSegment currentSegment) {
        if (currentSegment.isLaterOrEqualTo(OperationSegment.STARTUP)) {
            FXSpritePlane sprite = (FXSpritePlane) ctx.fountainSprite;
            if (sprite == null) {
                sprite = EffectHelper.of(EffectTemplatesAS.TEXTURE_SPRITE)
                        .spawn(new Vector3(fountain).add(0.5, 0.5, 0.5))
                        .setAxis(Vector3.RotAxis.Y_AXIS)
                        .setNoRotation(45)
                        .setSprite(SpritesAS.SPR_FOUNTAIN_LIQUID)
                        .setAlphaMultiplier(1F)
                        .alpha((fx, alphaIn, pTicks) -> this.getSegmentPercent(OperationSegment.STARTUP, fountain.getTickActiveFountainEffect()))
                        .setScaleMultiplier(5.5F)
                        .refresh(RefreshFunction.tileExistsAnd(fountain, (tile, fx) -> tile.getCurrentEffect() == this));
            } else if (sprite.isRemoved() || sprite.canRemove()) {
                EffectHelper.refresh(sprite, EffectTemplatesAS.TEXTURE_SPRITE);
            }
            ctx.fountainSprite = sprite;
        }

        BlockPos fountainPos = fountain.getPos();
        float segmentPercent = getSegmentPercent(currentSegment, operationTick);
        switch (currentSegment) {
            case STARTUP:
                playFountainVortexParticles(fountainPos, segmentPercent);
                playFountainArcs(fountainPos, segmentPercent);
                break;
            case PREPARATION:
                playFountainArcs(fountainPos, 1F - segmentPercent);
                playFountainVortexParticles(fountainPos, 1F - segmentPercent);
                playDigPreparation(fountainPos, segmentPercent);
                break;
            case RUNNING:
                playFountainVortexParticles(fountainPos, 0.2F);
                playFountainArcs(fountainPos, 0.6F);
                playDigParticles(fountainPos);
                if (fountain.getTicksExisted() % 40 == 0) {
                    playDigLightbeam(fountainPos);
                }
                break;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playDigPreparation(Vec3i pos, float chance) {
        Vector3 at = new Vector3(pos).add(0.5, 0.5, 0.5);
        for (int i = 0; i < 12; i++) {
            if (rand.nextFloat() >= chance) {
                continue;
            }
            Vector3 particlePos = new Vector3(
                    pos.getX() - 0.4 + rand.nextFloat() * 1.8,
                    pos.getY()       - rand.nextFloat() * 3,
                    pos.getZ() - 0.4 + rand.nextFloat() * 1.8
            );
            Vector3 motion = particlePos.clone().vectorFromHereTo(at).normalize().divide(30);

            EntityVisualFX fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(particlePos)
                    .setMotion(motion)
                    .setAlphaMultiplier(1F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                    .setMaxAge(20 + rand.nextInt(40));
            if (rand.nextBoolean()) {
                fx.color(VFXColorFunction.WHITE);
            } else {
                fx.color(VFXColorFunction.constant(ColorsAS.DEFAULT_GENERIC_PARTICLE));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playDigParticles(Vec3i pos) {
        for (int i = 0; i < 2; i++) {
            Vector3 at = new Vector3(pos).add(
                    0.3 + rand.nextFloat() * 0.4,
                    -rand.nextFloat() * 1.7,
                    0.3 + rand.nextFloat() * 0.4);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .setScaleMultiplier(0.25F)
                    .setAlphaMultiplier(1F)
                    .setMotion(new Vector3(0, -rand.nextFloat() * 0.008F, 0))
                    .color(VFXColorFunction.random());
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playDigLightbeam(Vec3i pos) {
        Vector3 from = new Vector3(pos).add(0.5, 1.5, 0.5);
        MiscUtils.applyRandomOffset(from, rand, 0.1F);
        Vector3 to = from.clone().setY(0);

        float size = 6 + rand.nextFloat() * 2;
        EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                .spawn(from)
                .setup(to, size, size);
    }

    @Override
    public void transition(TileFountain fountain, LiquidContext ctx, LogicalSide side, OperationSegment prevSegment, OperationSegment nextSegment) {
        if (side.isServer()) {
            if (nextSegment == OperationSegment.RUNNING) {
                digCone(fountain.getWorld(), ctx);
            }
        } else {
            if (nextSegment == OperationSegment.RUNNING) {
                markDigProcess(fountain.getPos());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void markDigProcess(BlockPos pos) {
        for (int yy = 0; yy <= pos.getY(); yy++) {
            for (int i = 0; i < 4; i++) {
                Vector3 at = new Vector3(pos).setY(yy).add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .setAlphaMultiplier(1F)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                        .setMaxAge(20 + rand.nextInt(40));
            }
        }

        Vector3 from = new Vector3(pos).add(0.5, 0.5, 0.5);
        Vector3 to = from.clone().setY(0);
        EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                .spawn(from)
                .setup(to, 1.5, 1.5)
                .setAlphaMultiplier(1F)
                .alpha(VFXAlphaFunction.FADE_OUT)
                .color(VFXColorFunction.constant(ColorsAS.EFFECT_BLUE_LIGHT));
    }

    @Override
    public void onReplace(TileFountain fountain, LiquidContext ctx, @Nullable FountainEffect<?> newEffect, LogicalSide side) {
        if (side.isClient()) {
            removeSprite(ctx);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void removeSprite(LiquidContext ctx) {
        FXSpritePlane sprite = (FXSpritePlane) ctx.fountainSprite;
        if (sprite != null) {
            sprite.requestRemoval();
        }
    }
}
