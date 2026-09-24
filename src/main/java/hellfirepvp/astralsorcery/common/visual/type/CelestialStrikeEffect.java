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
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.network.play.PktPlayVisualEffect;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.visual.VectorPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialStrikeEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialStrikeEffect extends VectorPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, CelestialStrikeEffect> STREAM_CODEC =
            VectorPosEffect.createCodec(CelestialStrikeEffect::new);
    public static final VisualEffectTypes.EffectType<CelestialStrikeEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("celestial_strike"), STREAM_CODEC);

    private CelestialStrikeEffect(Vector3 pos) {
        super(pos);
    }

    public static CelestialStrikeEffect at(Vector3 pos) {
        return new CelestialStrikeEffect(pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        Vector3 center = this.getPos();
        Vector3 beamPos = center.copy();

        spawnBeam(beamPos, 16, FXColorFunction.WHITE, 25);
        for (ColorWrapper color : new ColorWrapper[] {ColorsAS.CELESTIAL_STRIKE_LIGHT, ColorsAS.CELESTIAL_STRIKE_DARK}) {
            beamPos.add(rand.nextFloat() - rand.nextFloat(), 0, rand.nextFloat() - rand.nextFloat());
            spawnBeam(beamPos, 16 + rand.nextFloat() * 2F, FXColorFunction.constant(color), 24 + rand.nextInt(6));
        }

        AbstractRenderTexture tex = MiscUtil.getRandomEntry(rand,
                TexturesAS.EFFECT_SMOKE_1, TexturesAS.EFFECT_SMOKE_2, TexturesAS.EFFECT_SMOKE_3, TexturesAS.EFFECT_SMOKE_4).orElseThrow();
        EffectHelper.of(EffectTemplatesAS.IMMEDIATE_TEXTURE_SPRITE)
                .spawn(center.copy().addY(0.1F))
                .setSpriteSheet(tex.asSpriteSheet())
                .setNoRotation(rand.nextFloat() * 360F)
                .setAlpha(0.4F)
                .alpha(FXAlphaFunction.FADE_OUT)
                .color(FXColorFunction.WHITE)
                .setScale(17F)
                .setMaxAge(30 + rand.nextInt(10));

        for (int i = 0; i < 43; i++) {
            Vector3 randTo = center.copy().add(
                    (rand.nextDouble() * 9) - (rand.nextDouble() * 9),
                    rand.nextDouble() * 5,
                    (rand.nextDouble() * 9) - (rand.nextDouble() * 9));
            EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                    .spawn(center.copy())
                    .makeDefault(randTo)
                    .color(FXColorFunction.constant(getEffectColor(rand)));
        }

        for (int i = 0; i < 40; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(center.copy().add(
                            (rand.nextFloat() - rand.nextFloat()) * 4,
                            rand.nextFloat() * 9,
                            (rand.nextFloat() - rand.nextFloat()) * 4))
                    .color(FXColorFunction.constant(getEffectColor(rand)))
                    .setScale(0.85F)
                    .setGravity(Vector3.y(0.005F))
                    .setMaxAge(14 + rand.nextInt(6));
        }

        this.spawnRing(center, rand, 200 + rand.nextInt(40), 0.3F, 0.4F, 1.2F);
        this.spawnRing(center, rand, 100 + rand.nextInt(40), 0.2F, 0.1F, 0.7F);
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnBeam(Vector3 at, double height, FXColorFunction<?> color, int maxAge) {
        EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                .spawn(at.copy().addY(-4))
                .setup(at.copy().addY(height), 9, 6)
                .alpha(FXAlphaFunction.FADE_OUT)
                .color(color)
                .setAlpha(1F)
                .setMaxAge(maxAge);
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnRing(Vector3 center, RandomSource rand, int count, float baseSpeed, float randSpeed, float scale) {
        List<Vector3> circle = VectorUtil.getCircleOffsets(center, Vector3.RotAxis.Y_AXIS, 7.5F + rand.nextFloat(), count);
        for (Vector3 at : circle) {
            Vector3 dir = at.copy().subtract(center).normalize().multiply(baseSpeed + randSpeed * rand.nextFloat());
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(FXColorFunction.constant(ColorsAS.CELESTIAL_STRIKE_LIGHT))
                    .setAlpha(0.4F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setScale(scale)
                    .setMotion(dir)
                    .setMaxAge(14 + rand.nextInt(6));
        }
    }

    public static ColorWrapper getEffectColor(RandomSource rand) {
        return MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, ColorsAS.CELESTIAL_STRIKE_LIGHT, ColorsAS.CELESTIAL_STRIKE_DARK).orElseThrow();
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }

    @Override
    public void sendToNearby(ServerLevel sLevel, Vec3i pos) {
        PacketDistributor.sendToPlayersNear(sLevel, null, pos.getX(), pos.getY(), pos.getZ(), 96,
                PktPlayVisualEffect.playEffect(this));
    }
}
