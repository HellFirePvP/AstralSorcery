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
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.visual.BlockPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RockCrystalSparkle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RockCrystalSparkle extends BlockPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, RockCrystalSparkle> STREAM_CODEC =
            BlockPosEffect.createCodec(RockCrystalSparkle::new);
    public static final VisualEffectTypes.EffectType<RockCrystalSparkle> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("rock_crystal_sparkle"), STREAM_CODEC);

    private RockCrystalSparkle(BlockPos pos) {
        super(pos);
    }

    public static RockCrystalSparkle at(BlockPos pos) {
        return new RockCrystalSparkle(pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        FXAlphaFunction<EntityVisualFX> alphaFn = FXAlphaFunction.invert(FXAlphaFunction.proximityToPlayer(100)).andThen(FXAlphaFunction.FADE_OUT);

        Vector3 center = VectorUtil.withRandomOffset(this.getPos(), rand, 0.5F)
                .add(0.5, 0.5, 0.5);
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE_DEPTH)
                .spawn(center)
                .color(FXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                .setScale(0.4F)
                .alpha(alphaFn)
                .setMaxAge(20 + rand.nextInt(20));

        for (int i = 0; i < 2; i++) {
            if (rand.nextBoolean()) {

                Vector3 at = VectorUtil.withRandomOffset(this.getPos(), rand, 1F)
                        .add(0.5, 0.5, 0.5);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE_DEPTH)
                        .spawn(at)
                        .color(FXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                        .setScale(0.4F)
                        .alpha(alphaFn)
                        .setMaxAge(30 + rand.nextInt(10));
            }
        }
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
