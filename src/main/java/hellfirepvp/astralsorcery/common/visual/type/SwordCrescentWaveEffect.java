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
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.visual.VectorPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SwordCrescentWaveEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SwordCrescentWaveEffect extends VectorPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, SwordCrescentWaveEffect> STREAM_CODEC = StreamCodec.composite(
            Vector3.STREAM_CODEC,
            VectorPosEffect::getPos,
            Vector3.STREAM_CODEC,
            SwordCrescentWaveEffect::getDirection,
            SwordCrescentWaveEffect::new);
    public static final VisualEffectTypes.EffectType<SwordCrescentWaveEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("sword_crescent_wave"), STREAM_CODEC);

    private final Vector3 direction;

    private SwordCrescentWaveEffect(Vector3 pos, Vector3 direction) {
        super(pos);
        this.direction = direction;
    }

    public static SwordCrescentWaveEffect at(Vector3 pos, Vector3 direction) {
        return new SwordCrescentWaveEffect(pos, direction);
    }

    private Vector3 getDirection() {
        return this.direction.copy();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        Vector3 origin = this.getPos();
        Vector3 look = this.getDirection().normalize();

        for (int i = 0; i < 70; i++) {
            double angle = Math.toRadians(-60D + rand.nextFloat() * 120D);
            Vector3 dir = look.copy().rotate(angle, Vector3.RotAxis.Y_AXIS).normalize();
            Vector3 at = origin.copy()
                    .add(dir.copy().multiply(0.6F + rand.nextFloat() * 0.5F))
                    .addY(-0.4F + rand.nextFloat() * 0.8F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(FXColorFunction.constant(CelestialStrikeEffect.getEffectColor(rand)))
                    .setScale(0.4F + rand.nextFloat() * 0.4F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setMotion(dir.copy().multiply(0.5F + rand.nextFloat() * 0.3F))
                    .setMaxAge(12 + rand.nextInt(8));
        }
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
