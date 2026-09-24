/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RayTraceUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RayTraceUtil {

    public static ClipContext getEntityViewContext(Entity entity, double distance) {
        float pTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        return getEntityViewContext(entity, distance, pTicks, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);
    }

    public static ClipContext getEntityViewContext(Entity entity, double distance, float partialTicks, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        Vec3 eyePos = entity.getEyePosition(partialTicks);
        Vec3 viewDir = entity.getViewVector(partialTicks);
        Vec3 scaledViewDir = eyePos.add(viewDir.x * distance, viewDir.y * distance, viewDir.z * distance);
        return new ClipContext(eyePos, scaledViewDir, blockMode, fluidMode, entity);
    }

    public static BlockHitResult clip(BlockGetter level, Vec3 from, Vec3 to) {
        return clip(level, from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE);
    }

    public static BlockHitResult clip(BlockGetter level, Vec3 from, Vec3 to, ClipContext.Block blockCheck, ClipContext.Fluid fluidCheck) {
        return clip(level, new ClipContext(from, to, blockCheck, fluidCheck, CollisionContext.empty()));
    }

    public static BlockHitResult clip(BlockGetter level, ClipContext ct) {
        return clip(level, ct, Collections.emptySet());
    }

    public static BlockHitResult clip(BlockGetter level, Vec3 from, Vec3 to, Set<BlockPos> excluded) {
        ClipContext ctx = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        if (excluded.isEmpty()) return level.clip(ctx);
        return new LevelClipHelper(level, excluded).clip(ctx);
    }

    public static BlockHitResult clip(BlockGetter level, ClipContext ctx, Set<BlockPos> excluded) {
        if (excluded.isEmpty()) return level.clip(ctx);
        return new LevelClipHelper(level, excluded).clip(ctx);
    }

    public static BlockHitResult clipPerPosition(BlockGetter level, ClipContext ctx, Predicate<BlockPos> stopFn) {
        return clipPerPosition(level, ctx, Collections.emptySet(), stopFn);
    }

    public static BlockHitResult clipPerPosition(BlockGetter level, ClipContext ctx, Set<BlockPos> excluded, Predicate<BlockPos> stopFn) {
        BlockGetter clipLevel = excluded.isEmpty() ? level : new LevelClipHelper(level, excluded);
        return BlockGetter.traverseBlocks(ctx.getFrom(), ctx.getTo(), ctx, (context, pos) -> {
            Vec3 from = context.getFrom();
            Vec3 to = context.getTo();
            if (stopFn.test(pos)) {
                return BlockHitResult.miss(Vec3.atCenterOf(pos), Direction.getNearest(from.subtract(to)), pos);
            }
            BlockState state = clipLevel.getBlockState(pos);
            FluidState fluidState = clipLevel.getFluidState(pos);
            VoxelShape posShape = context.getBlockShape(state, clipLevel, pos);
            BlockHitResult blockResult = clipLevel.clipWithInteractionOverride(from, to, pos, posShape, state);
            VoxelShape fluidShape = context.getFluidShape(fluidState, clipLevel, pos);
            BlockHitResult fluidResult = fluidShape.clip(from, to, pos);

            double dstBlock = blockResult == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(blockResult.getLocation());
            double dstFluid = fluidResult == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(fluidResult.getLocation());
            return dstBlock <= dstFluid ? blockResult : fluidResult;
        }, failedCtx -> {
            Vec3 direction = failedCtx.getFrom().subtract(failedCtx.getTo());
            return BlockHitResult.miss(failedCtx.getTo(), Direction.getNearest(direction), BlockPos.containing(failedCtx.getTo()));
        });
    }

    private record LevelClipHelper(BlockGetter decorated, Set<BlockPos> excluded) implements BlockGetter {

        @Nullable
            @Override
            public BlockEntity getBlockEntity(BlockPos pos) {
                if (this.excluded.contains(pos)) return null;
                return this.decorated.getBlockEntity(pos);
            }

            @Override
            public BlockState getBlockState(BlockPos pos) {
                if (this.excluded.contains(pos)) return Blocks.AIR.defaultBlockState();
                return this.decorated.getBlockState(pos);
            }

            @Override
            public FluidState getFluidState(BlockPos pos) {
                if (this.excluded.contains(pos)) return Fluids.EMPTY.defaultFluidState();
                return this.decorated.getFluidState(pos);
            }

            @Override
            public int getHeight() {
                return this.decorated.getHeight();
            }

            @Override
            public int getMinBuildHeight() {
                return this.decorated.getMinBuildHeight();
            }
        }
}
