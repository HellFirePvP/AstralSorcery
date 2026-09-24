/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.visual.type;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ChaliceLiquidInteractionEffect
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class ChaliceLiquidInteractionEffect implements VisualEffectTypes.Effect {

    private static final float PARTICLE_STEP_LENGTH = 0.3F;
    private static final int GENERIC_PASSES = 2;

    public static final StreamCodec<RegistryFriendlyByteBuf, ChaliceLiquidInteractionEffect> STREAM_CODEC = StreamCodec.composite(
            Segment.STREAM_CODEC, ChaliceLiquidInteractionEffect::getFirst,
            Segment.OPTIONAL_STREAM_CODEC, ChaliceLiquidInteractionEffect::getSecondOptional,
            ChaliceLiquidInteractionEffect::new);

    public static final VisualEffectTypes.EffectType<ChaliceLiquidInteractionEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("chalice_liquid_interaction"), STREAM_CODEC);

    private final Segment first;
    @Nullable
    private final Segment second;

    private ChaliceLiquidInteractionEffect(Segment first, Optional<Segment> second) {
        this.first = first;
        this.second = second.orElse(null);
    }

    public static ChaliceLiquidInteractionEffect chaliceReaction(Vec3 chaliceA, Vec3 chaliceB, Vec3 midpoint, FluidStack fluidA, FluidStack fluidB) {
        return new ChaliceLiquidInteractionEffect(
                new Segment(toVec(chaliceA), toVec(midpoint), fluidA),
                Optional.of(new Segment(toVec(chaliceB), toVec(midpoint), fluidB)));
    }

    public static ChaliceLiquidInteractionEffect lightwellDraw(Vec3 wellCenter, Vec3 chaliceCenter, FluidStack fluid) {
        return new ChaliceLiquidInteractionEffect(
                new Segment(toVec(wellCenter), toVec(chaliceCenter), fluid),
                Optional.empty());
    }

    private static Vector3 toVec(Vec3 v) {
        return new Vector3(v.x, v.y, v.z);
    }

    private Segment getFirst() {
        return this.first;
    }

    private Optional<Segment> getSecondOptional() {
        return Optional.ofNullable(this.second);
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        drawSegment(rand, this.first);
        if (this.second != null) {
            drawSegment(rand, this.second);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawSegment(RandomSource rand, Segment segment) {
        int tint = IClientFluidTypeExtensions.of(segment.fluid.getFluid()).getTintColor(segment.fluid);
        ColorWrapper base = ColorWrapper.opaque(tint);
        ColorWrapper accent = base.brighter();

        List<Vector3> points = VectorUtil.iteratePoints(segment.from, segment.to, PARTICLE_STEP_LENGTH);
        for (Vector3 point : points) {
            for (int pass = 0; pass < GENERIC_PASSES; pass++) {
                Vector3 jittered = point.copy().add(
                        (rand.nextDouble() - 0.5D) * 0.2D,
                        (rand.nextDouble() - 0.5D) * 0.2D,
                        (rand.nextDouble() - 0.5D) * 0.2D);
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(jittered)
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .color(FXColorFunction.constant(accent))
                        .setScale(0.15F + rand.nextFloat() * 0.2F)
                        .setMotion(Vector3.random(rand).multiply(0.01F))
                        .setMaxAge(20 + rand.nextInt(25));
            }
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(point.copy())
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .color(FXColorFunction.constant(base))
                    .setScale(0.25F + rand.nextFloat() * 0.1F)
                    .setMotion(Vector3.random(rand).multiply(0.005F))
                    .setMaxAge(15 + rand.nextInt(10));
        }
    }

    public record Segment(Vector3 from, Vector3 to, FluidStack fluid) {

        public static final StreamCodec<RegistryFriendlyByteBuf, Segment> STREAM_CODEC = StreamCodec.composite(
                Vector3.STREAM_CODEC, Segment::from,
                Vector3.STREAM_CODEC, Segment::to,
                FluidStack.STREAM_CODEC, Segment::fluid,
                Segment::new);

        public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Segment>> OPTIONAL_STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public Optional<Segment> decode(RegistryFriendlyByteBuf buf) {
                        if (!buf.readBoolean()) {
                            return Optional.empty();
                        }
                        return Optional.of(STREAM_CODEC.decode(buf));
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, Optional<Segment> value) {
                        buf.writeBoolean(value.isPresent());
                        value.ifPresent(segment -> STREAM_CODEC.encode(buf, segment));
                    }
                };
    }
}
