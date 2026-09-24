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
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LightningEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LightningEffect implements VisualEffectTypes.Effect {

    public static final StreamCodec<RegistryFriendlyByteBuf, LightningEffect> STREAM_CODEC = StreamCodec.composite(
            Vector3.STREAM_CODEC,
            LightningEffect::getFrom,
            Vector3.STREAM_CODEC,
            LightningEffect::getTo,
            ColorWrapper.STREAM_CODEC,
            LightningEffect::getColor,
            LightningEffect::new
    );
    public static final VisualEffectTypes.EffectType<LightningEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("lightning_effect"), STREAM_CODEC);

    private final Vector3 from, to;
    private final ColorWrapper color;

    private LightningEffect(Vector3 from, Vector3 to, ColorWrapper color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }

    public static LightningEffect make(Vector3 from, Vector3 to) {
        return make(from, to, ColorsAS.ENTITY_FLARE_LIGHTNING);
    }

    public static LightningEffect make(Vector3 from, Vector3 to, ColorWrapper color) {
        return new LightningEffect(from, to, color);
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
        EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                .spawn(this.getFrom())
                .makeDefault(this.getTo())
                .color(FXColorFunction.constant(this::getColor));
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }
}
