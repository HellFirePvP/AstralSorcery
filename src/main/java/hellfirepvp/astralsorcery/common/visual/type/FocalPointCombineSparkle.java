/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.visual.type;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.visual.VectorPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointCombineSparkle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointCombineSparkle extends VectorPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, FocalPointCombineSparkle> STREAM_CODEC = StreamCodec.composite(
            Vector3.STREAM_CODEC,
            FocalPointCombineSparkle::getPos,
            ColorWrapper.STREAM_CODEC,
            FocalPointCombineSparkle::getColor,
            FocalPointCombineSparkle::new);
    public static final VisualEffectTypes.EffectType<FocalPointCombineSparkle> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("focal_point_combine_sparkle"), STREAM_CODEC);

    private final ColorWrapper color;

    private FocalPointCombineSparkle(Vector3 pos, ColorWrapper color) {
        super(pos);
        this.color = color;
    }

    public ColorWrapper getColor() {
        return this.color;
    }

    public static FocalPointCombineSparkle at(Vector3 pos, ColorWrapper color) {
        return new FocalPointCombineSparkle(pos, color);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        if (rand.nextBoolean()) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(this.getPos().addY(0.5F))
                    .setScale(0.25F + rand.nextFloat() * 0.1F)
                    .color(FXColorFunction.constant(rand.nextBoolean() ? ColorsAS.ROCK_CRYSTAL : this.getColor()))
                    .setMotion(Vector3.random(rand).normalize().multiply(0.025F + rand.nextFloat() * 0.01F))
                    .setGravity(new Vector3(0, 0.001F, 0))
                    .setMaxAge(40 + rand.nextInt(20));
        }

        if (ClientProxy.getClientTick() % 20 == 0) {
            Vector3 offset = VectorUtil.withRandomOffset(this.getPos(), rand, 0.15F);
            float size = 1F + rand.nextFloat() * 0.3F;

            EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                    .spawn(offset)
                    .setup(offset.copy().addY(5 + rand.nextFloat()), size, size)
                    .color(FXColorFunction.constant(rand.nextBoolean() ? ColorsAS.ROCK_CRYSTAL : this.getColor()))
                    .setAlpha(0.75F);
        }
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
