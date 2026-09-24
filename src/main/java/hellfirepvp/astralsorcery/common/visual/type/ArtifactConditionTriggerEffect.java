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
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionTriggerEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionTriggerEffect implements VisualEffectTypes.Effect {

    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactConditionTriggerEffect> STREAM_CODEC = StreamCodec.composite(
            Vector3.STREAM_CODEC,
            ArtifactConditionTriggerEffect::getFrom,
            Vector3.STREAM_CODEC,
            ArtifactConditionTriggerEffect::getTo,
            ColorWrapper.STREAM_CODEC,
            ArtifactConditionTriggerEffect::getColor,
            ArtifactConditionTriggerEffect::new);
    public static final VisualEffectTypes.EffectType<ArtifactConditionTriggerEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("artifact_condition_trigger_effect"), STREAM_CODEC);

    private final Vector3 from, to;
    private final ColorWrapper color;

    private ArtifactConditionTriggerEffect(Vector3 from, Vector3 to, ColorWrapper color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }

    public static ArtifactConditionTriggerEffect of(Vector3 from, Vector3 to, ColorWrapper color) {
        return new ArtifactConditionTriggerEffect(from, to, color);
    }

    public Vector3 getFrom() {
        return this.from;
    }

    public Vector3 getTo() {
        return this.to;
    }

    public ColorWrapper getColor() {
        return this.color;
    }

    @Override
    public void playEffect(RandomSource rand) {
        List<Vector3> pointPath = VectorUtil.iteratePoints(this.getFrom(), this.getTo(), 0.3F);

        pointPath.forEach(pos -> {

            for (int j = 0; j < 2; j++) {
                Vector3 effectPos = VectorUtil.withRandomOffset(pos.copy(), rand, 0.1F);
                Vector3 dir = Vector3.random(rand).normalize().multiply(0.005F + rand.nextFloat() * 0.005F);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(effectPos)
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .color(FXColorFunction.constant(this.getColor()))
                        .setScale(0.3F + rand.nextFloat() * 0.15F)
                        .setMotion(dir)
                        .setGravity(Vector3.y(0.0005F))
                        .setMaxAge(20 + rand.nextInt(20));
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(effectPos)
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(0.1F + rand.nextFloat() * 0.05F)
                        .setMotion(dir)
                        .setGravity(Vector3.y(0.0005F))
                        .setMaxAge(20 + rand.nextInt(8));
            }
        });
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }

    @Override
    public void sendToNearby(ServerLevel sLevel, Vec3i pos) {
        sLevel.players().stream()
                .filter(player -> this.getFrom().distanceSquared(player) < 4096 || this.getTo().distanceSquared(player) < 4096)
                .forEach(this::sendEffect);
    }
}
