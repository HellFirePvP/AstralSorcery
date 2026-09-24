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
import hellfirepvp.astralsorcery.client.effect.function.FXCollisionFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.common.visual.BlockPosEffect;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockBreakEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BlockBreakEffect extends BlockPosEffect {

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockBreakEffect> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            BlockPosEffect::getBlockPos,
            ByteBufCodecs.fromCodecWithRegistries(BlockState.CODEC),
            BlockBreakEffect::getState,
            ColorWrapper.STREAM_CODEC,
            BlockBreakEffect::getColor,
            BlockBreakEffect::new);
    public static final VisualEffectTypes.EffectType<BlockBreakEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("block_break_effect"), STREAM_CODEC);

    private final BlockState state;
    private final ColorWrapper color;

    private BlockBreakEffect(BlockPos pos, BlockState state, ColorWrapper color) {
        super(pos);
        this.state = state;
        this.color = color;
    }

    private BlockState getState() {
        return this.state;
    }

    private ColorWrapper getColor() {
        return this.color;
    }

    public static BlockPosEffect at(BlockPos pos, BlockState state) {
        return at(pos, state, ColorWrapper.WHITE);
    }

    public static BlockPosEffect at(BlockPos pos, BlockState state, ColorWrapper color) {
        return new BlockBreakEffect(pos, state, color);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        EffectUtil.playBlockBreakParticles(this.getBlockPos(), this.getState());

        for (int i = 0; i < 3; i++) {

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(VectorUtil.withRandomOffset(this.getPos(), rand, 0.5F))
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .color(FXColorFunction.constant(this.getColor()))
                    .setScale(0.25F + rand.nextFloat() * 0.2F)
                    .setMotion(Vector3.random(rand).multiply(0.05F))
                    .setGravity(Vector3.y(-0.02F))
                    .collision(FXCollisionFunction.COLLIDE_WITH_BLOCKS)
                    .setMaxAge(30 + rand.nextInt(15));
        }
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
