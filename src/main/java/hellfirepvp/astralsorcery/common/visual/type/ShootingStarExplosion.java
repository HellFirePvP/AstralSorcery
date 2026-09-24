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
import hellfirepvp.astralsorcery.client.effect.function.FXPersistenceFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.common.visual.VectorPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.network.play.PktPlayVisualEffect;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ShootingStarExplosion
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ShootingStarExplosion extends VectorPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, ShootingStarExplosion> STREAM_CODEC = StreamCodec.composite(
            Vector3.STREAM_CODEC,
            VectorPosEffect::getPos,
            ColorWrapper.STREAM_CODEC,
            ShootingStarExplosion::getColor,
            ShootingStarExplosion::new);
    public static final VisualEffectTypes.EffectType<ShootingStarExplosion> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("shooting_star_explosion"), STREAM_CODEC);

    private final ColorWrapper color;

    protected ShootingStarExplosion(Vector3 pos, ColorWrapper color) {
        super(pos);
        this.color = color;
    }

    public static ShootingStarExplosion at(Vector3 vec, ColorWrapper color) {
        return new ShootingStarExplosion(vec, color);
    }

    private ColorWrapper getColor() {
        return this.color;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        ColorWrapper brighterColor = this.getColor().brighter();
        Supplier<ColorWrapper> randomColor = () -> MiscUtil.getRandomEntry(rand, this.getColor(), brighterColor, ColorWrapper.WHITE).orElseThrow();
        for (int i = 0; i < 10; i++) {
            Vector3 randPos = this.getPos().copy().add(
                    rand.nextFloat() * 8 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 5,
                    rand.nextFloat() * 8 * (rand.nextBoolean() ? 1 : -1));
            EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                    .spawn(this.getPos())
                    .makeDefault(randPos)
                    .setBuildTime(1)
                    .setAlpha(0.6F)
                    .color(FXColorFunction.constant(randomColor.get()))
                    .persistence(FXPersistenceFunction.ALWAYS_PERSIST);
        }

        for (float degree = 0F; degree <= 360F; degree += 2.4F) {
            Vector3 dir = new Vector3(1, 0, 0).rotate(Math.toRadians(degree), Vector3.RotAxis.Y_AXIS).normalize();
            Vector3 pos = dir.copy().multiply(4.5F + rand.nextFloat() * 2F).add(this.getPos());

            dir.multiply(0.002F).setY(0).add(rand.nextFloat() * 0.004F, rand.nextFloat() * 0.001F, rand.nextFloat() * 0.004F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(FXColorFunction.constant(randomColor.get()))
                    .setAlpha(0.7F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setScale(0.8F + rand.nextFloat() * 0.4F)
                    .setMotion(dir)
                    .setGravity(Vector3.y(rand.nextFloat() * 0.0006F))
                    .persistence(FXPersistenceFunction.ALWAYS_PERSIST)
                    .setMaxAge(40 + rand.nextInt(40));
        }

        for (int i = 0; i < 70; i++) {
            Vector3 dir = Vector3.positiveYRandom(rand).normalize();
            Vector3 motion = dir.multiply(0.15F).multiply(0.4F + rand.nextFloat() * 0.6F);
            motion = VectorUtil.withRandomOffset(motion, rand, 0.001F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(this.getPos().copy().add(dir.copy().normalize().multiply(rand.nextFloat() * 3F)))
                    .color(FXColorFunction.constant(randomColor.get()))
                    .setAlpha(0.7F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setScale(0.8F + rand.nextFloat() * 0.4F)
                    .setMotion(motion)
                    .setGravity(Vector3.y(0.001F))
                    .persistence(FXPersistenceFunction.ALWAYS_PERSIST)
                    .setMaxAge(40 + rand.nextInt(50));
        }

        AbstractRenderTexture tex = MiscUtil.getRandomEntry(rand,
                TexturesAS.EFFECT_SMOKE_1, TexturesAS.EFFECT_SMOKE_2, TexturesAS.EFFECT_SMOKE_3, TexturesAS.EFFECT_SMOKE_4).orElseThrow();
        EffectHelper.of(EffectTemplatesAS.IMMEDIATE_TEXTURE_SPRITE)
                .spawn(this.getPos().copy().addY(0.05F))
                .setSpriteSheet(tex.asSpriteSheet())
                .setNoRotation(rand.nextFloat() * 360F)
                .setAlpha(0.75F)
                .alpha(FXAlphaFunction.FADE_OUT)
                .color(FXColorFunction.WHITE)
                .setScale(10F)
                .setMaxAge(30 + rand.nextInt(10));
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }

    @Override
    public void sendToNearby(ServerLevel sLevel, Vec3i pos) {
        PacketDistributor.sendToPlayersNear(sLevel, null, pos.getX(), pos.getY(), pos.getZ(), 256,
                PktPlayVisualEffect.playEffect(this));
    }
}
