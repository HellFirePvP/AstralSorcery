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
import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.client.effect.function.FXMotionFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXCube;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.visual.VectorPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SwordShockwaveEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SwordShockwaveEffect extends VectorPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, SwordShockwaveEffect> STREAM_CODEC = StreamCodec.composite(
            Vector3.STREAM_CODEC,
            VectorPosEffect::getPos,
            ByteBufCodecs.VAR_INT,
            SwordShockwaveEffect::getRadius,
            SwordShockwaveEffect::new);
    public static final VisualEffectTypes.EffectType<SwordShockwaveEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("sword_shockwave"), STREAM_CODEC);

    private final int radius;

    private SwordShockwaveEffect(Vector3 pos, int radius) {
        super(pos);
        this.radius = radius;
    }

    public static SwordShockwaveEffect at(Vector3 pos, int radius) {
        return new SwordShockwaveEffect(pos, radius);
    }

    private int getRadius() {
        return this.radius;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Vector3 center = this.getPos();
        for (int ring = 1; ring <= this.getRadius(); ring++) {
            this.playRing(level, center, ring, (ring - 1) * 2, rand);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playRing(Level level, Vector3 center, int ring, int delay, RandomSource rand) {
        for (Vector3 at : VectorUtil.getCircleOffsets(center, Vector3.RotAxis.Y_AXIS, ring, ring * 10)) {
            BlockPos pos = findGround(level, at.toBlockPos());
            if (pos == null) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            Vector3 motion = Vector3.y(0.22F + rand.nextFloat() * 0.12F);

            VFXCube cube = EffectHelper.of(EffectTemplatesAS.TRANSLUCENT_CUBE_SINGLE).spawn(new Vector3(pos));
            cube.setRenderState(state).tumble();
            cube.setScale(0.4F);
            cube.setMotion(motion);
            cube.setGravity(Vector3.y(-0.03F));
            cube.motion(delayedMotion(delay, motion));
            cube.setMaxAge(delay + 16 + rand.nextInt(6));

            if (rand.nextInt(4) == 0) {
                EffectUtil.playBlockBreakParticles(pos, state);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static FXMotionFunction<EntityFX> delayedMotion(int delay, Vector3 motion) {
        return (fx, currentMotion) -> {
            int age = fx.getAge();
            if (age < delay) {
                return new Vector3();
            }
            return age == delay ? motion.copy() : currentMotion;
        };
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    private static BlockPos findGround(Level level, BlockPos at) {
        for (int dy = 1; dy >= -2; dy--) {
            BlockPos pos = at.offset(0, dy, 0);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && state.getFluidState().isEmpty() && level.getBlockState(pos.above()).isAir()) {
                return pos;
            }
        }
        return null;
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
