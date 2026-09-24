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
import hellfirepvp.astralsorcery.common.visual.BlockPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointTransmutationSparkle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointTransmutationSparkle extends BlockPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, FocalPointTransmutationSparkle> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            FocalPointTransmutationSparkle::getBlockPos,
            ColorWrapper.STREAM_CODEC,
            FocalPointTransmutationSparkle::getColor,
            FocalPointTransmutationSparkle::new);
    public static final VisualEffectTypes.EffectType<FocalPointTransmutationSparkle> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("focal_point_transmutation_sparkle"), STREAM_CODEC);

    private final ColorWrapper color;

    private FocalPointTransmutationSparkle(BlockPos pos, ColorWrapper color) {
        super(pos);
        this.color = color;
    }

    public ColorWrapper getColor() {
        return this.color;
    }

    public static FocalPointTransmutationSparkle at(BlockPos pos, ColorWrapper color) {
        return new FocalPointTransmutationSparkle(pos, color);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        if (rand.nextBoolean()) {
            Vector3 offset;
            BlockState existingState = level.getBlockState(this.getBlockPos());
            if (Block.isFaceFull(existingState.getShape(level, this.getBlockPos()), Direction.UP)) {
                offset = VectorUtil.withRandomOffset(this.getCenteredPos(), rand, 0.45F).setY(this.getPos().getY() + 1);
            } else {
                offset = VectorUtil.withRandomOffset(this.getCenteredPos(), rand, 0.45F);
            }

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .setScale(0.15F + rand.nextFloat() * 0.05F)
                    .alpha(FXAlphaFunction.PYRAMID)
                    .color(FXColorFunction.constant(rand.nextInt(3) != 0 ? ColorsAS.ROCK_CRYSTAL : this.getColor()))
                    .setGravity(new Vector3(0, 0.0001F, 0))
                    .setMaxAge(40 + rand.nextInt(20));
        }

        if (rand.nextInt(70) == 0) {
            Vector3 offset = VectorUtil.withRandomOffset(this.getCenteredPos(), rand, 0.4F);
            float size = 1F + rand.nextFloat() * 0.3F;

            EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                    .spawn(offset)
                    .setup(offset.copy().addY(3 + rand.nextFloat()), size, size)
                    .color(FXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                    .setAlpha(0.75F);
        }
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
