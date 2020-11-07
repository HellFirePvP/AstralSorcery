package hellfirepvp.astralsorcery.common.crafting.nojson.fountain;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.fountain.BlockFountainPrime;
import hellfirepvp.astralsorcery.common.tile.TileFountain;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FountainEffect
 * Created by HellFirePvP
 * Date: 01.11.2020 / 10:07
 */
public abstract class FountainEffect<E extends FountainEffect.EffectContext> {

    protected static final Random rand = new Random();

    private final ResourceLocation id;

    protected FountainEffect(ResourceLocation id) {
        this.id = id;
    }

    public final ResourceLocation getId() {
        return id;
    }

    @Nonnull
    public abstract BlockFountainPrime getAssociatedPrime();

    @Nonnull
    public abstract E createContext(TileFountain fountain);

    public abstract void tick(TileFountain fountain, E context, int operationTick, LogicalSide side, OperationSegment currentSegment);

    public abstract void transition(TileFountain fountain, E context, LogicalSide side, OperationSegment prevSegment, OperationSegment nextSegment);

    public abstract void onReplace(TileFountain fountain, E context, @Nullable FountainEffect<?> newEffect, LogicalSide side);

    @OnlyIn(Dist.CLIENT)
    protected void playFountainVortexParticles(Vec3i pos, float chance) {
        Vector3 at = new Vector3(pos).add(0.5, 0.5, 0.5);
        for (int i = 0; i < 18; i++) {
            if (rand.nextFloat() >= chance) {
                continue;
            }
            Vector3 particlePos = new Vector3(
                    pos.getX() - 3   + rand.nextFloat() * 7,
                    pos.getY()       + rand.nextFloat(),
                    pos.getZ() - 3   + rand.nextFloat() * 7
            );
            Vector3 motion = particlePos.clone().vectorFromHereTo(at).normalize().divide(30);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(particlePos)
                    .setMotion(motion)
                    .setAlphaMultiplier(1F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                    .color(VFXColorFunction.WHITE)
                    .setMaxAge(20 + rand.nextInt(40));
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void playFountainArcs(Vec3i pos, float chance) {
        if (rand.nextFloat() < chance && rand.nextInt(8) == 0) {
            Vector3 at = new Vector3(pos).add(0.5, 0.5, 0.5);

            Vector3 pos1 = Vector3.random().setY(0).normalize().multiply(4).add(at);
            Vector3 pos2 = Vector3.random().setY(0).normalize().multiply(4).add(at);

            EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                    .spawn(pos1)
                    .makeDefault(pos2);
        }
    }

    public final boolean isInTick(OperationSegment segment, int operationTick) {
        int start = getSegmentStartTick(segment);
        if (operationTick > start) {
            return operationTick <= start + getSegmentDuration(segment);
        }
        return false;
    }

    public final int getSegmentStartTick(OperationSegment segment) {
        int ticks = 0;
        for (int i = 0; i < segment.ordinal(); i++) {
            int duration = getSegmentDuration(OperationSegment.values()[i]);
            if (duration != -1) {
                ticks += duration;
            }
        }
        return ticks;
    }

    public final float getSegmentPercent(OperationSegment segment, int operationTick) {
        int start = getSegmentStartTick(segment);
        operationTick -= start;
        if (operationTick <= 0) {
            return 0F;
        }
        float duration = getSegmentDuration(segment);
        return Math.min(1F, operationTick / duration);
    }

    public int getSegmentDuration(OperationSegment segment) {
        return segment.getDuration();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FountainEffect<?> effect = (FountainEffect<?>) o;
        return Objects.equals(id, effect.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract static class EffectContext {

        public abstract void readFromNBT(CompoundNBT compound);

        public abstract void writeToNBT(CompoundNBT compound);

    }

    public static enum OperationSegment {

        INACTIVE(-1),
        STARTUP(60),
        PREPARATION(200),
        RUNNING(-1);

        private final int duration;

        OperationSegment(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public boolean isLaterThan(OperationSegment other) {
            return this.ordinal() > other.ordinal();
        }

        public boolean isLaterOrEqualTo(OperationSegment other) {
            return this.ordinal() >= other.ordinal();
        }
    }
}
