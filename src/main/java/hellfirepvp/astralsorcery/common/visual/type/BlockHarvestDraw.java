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
import hellfirepvp.astralsorcery.client.effect.function.FXMotionFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockHarvestDraw
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BlockHarvestDraw implements VisualEffectTypes.Effect {

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockHarvestDraw> STREAM_CODEC = StreamCodec.composite(
            Vector3.STREAM_CODEC,
            BlockHarvestDraw::getFrom,
            Vector3.STREAM_CODEC,
            BlockHarvestDraw::getTo,
            ColorWrapper.STREAM_CODEC,
            BlockHarvestDraw::getColor,
            BlockHarvestDraw::new
    );
    public static final VisualEffectTypes.EffectType<BlockHarvestDraw> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("block_harvest_draw"), STREAM_CODEC);

    private final Vector3 from, to;
    private final ColorWrapper color;

    private BlockHarvestDraw(Vector3 from, Vector3 vector3, ColorWrapper color) {
        this.from = from;
        this.to = vector3;
        this.color = color;
    }

    public static BlockHarvestDraw make(BlockPos from, Vector3 to, ColorWrapper color) {
        return new BlockHarvestDraw(new Vector3(from), to, color);
    }

    public static BlockHarvestDraw make(Vector3 from, Vector3 to, ColorWrapper color) {
        return new BlockHarvestDraw(from, to, color);
    }

    private Vector3 getFrom() {
        return this.from;
    }

    private Vector3 getTo() {
        return this.to;
    }

    private ColorWrapper getColor() {
        return this.color;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        FXColorFunction<?> colorFn = FXColorFunction.constant(this.getColor());

        for (int i = 0; i < 10; i++) {
            Vector3 pos = this.getFrom().copy().add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .alpha(FXAlphaFunction.fadeIn(10)
                            .andThen(FXAlphaFunction.FADE_OUT)
                            .andThen(FXAlphaFunction.proximity(this::getTo, 10F)))
                    .color(rand.nextFloat() > 0.75F ? FXColorFunction.WHITE : colorFn)
                    .setScale(0.25F + rand.nextFloat() * 0.15F)
                    .motion(FXMotionFunction.target(this::getTo, 0.06F + rand.nextFloat() * 0.06F))
                    .setMaxAge(50 + rand.nextInt(40));
        }
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
