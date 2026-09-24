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
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class CelestialStrikeBeamEffect implements VisualEffectTypes.Effect {

    public static final StreamCodec<RegistryFriendlyByteBuf, CelestialStrikeBeamEffect> STREAM_CODEC = StreamCodec.composite(
            Vector3.STREAM_CODEC,
            CelestialStrikeBeamEffect::getFrom,
            Vector3.STREAM_CODEC,
            CelestialStrikeBeamEffect::getTo,
            CelestialStrikeBeamEffect::new
    );
    public static final VisualEffectTypes.EffectType<CelestialStrikeBeamEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("celestial_strike_beam"), STREAM_CODEC);

    private final Vector3 from, to;

    private CelestialStrikeBeamEffect(Vector3 from, Vector3 to) {
        this.from = from;
        this.to = to;
    }

    public static CelestialStrikeBeamEffect of(Vector3 from, Vector3 to) {
        return new CelestialStrikeBeamEffect(from.copy(), to.copy());
    }

    private Vector3 getFrom() {
        return this.from;
    }

    private Vector3 getTo() {
        return this.to;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        VectorUtil.iteratePoints(this.from, this.to, 0.35F).forEach(pos -> {
            Vector3 effectPos = VectorUtil.withRandomOffset(pos, rand, 0.15F);
            Vector3 dir = Vector3.random(rand).normalize().multiply(0.01F + rand.nextFloat() * 0.01F);

            for (int i = 0; i < 4; i++) {
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(effectPos)
                        .color(FXColorFunction.constant(CelestialStrikeEffect.getEffectColor(rand)))
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(0.2F + rand.nextFloat() * 0.15F)
                        .setMotion(dir)
                        .setMaxAge(10 + rand.nextInt(8));
            }
        });
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }

    public void sendToNearby(ServerLevel sLevel) {
        this.sendToNearby(sLevel, this.from.toBlockPos());
    }
}
