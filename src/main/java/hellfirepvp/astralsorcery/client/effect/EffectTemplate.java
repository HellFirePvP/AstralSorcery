/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import com.mojang.blaze3d.vertex.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EffectTemplate
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EffectTemplate<T extends EntityVisualFX> implements ParticleRenderType {

    private static int counter = 0;
    private final int id;

    protected final RenderType renderType;
    protected final Function<Vector3, T> particleCreator;
    protected final boolean isTranslucent;

    public EffectTemplate(RenderType renderType, boolean isTranslucent, Function<Vector3, T> particleCreator) {
        this.isTranslucent = isTranslucent;
        this.id = counter++;
        this.renderType = renderType;
        this.particleCreator = particleCreator;
    }

    public RenderType getRenderType() {
        return this.renderType;
    }

    public T createParticle(Vector3 pos) {
        return this.particleCreator.apply(pos);
    }

    public void renderAll(List<T> effects, Camera renderInfo, MultiBufferSource.BufferSource drawBuffer, float pTicks) {
        EffectTemplate rawTpl = this;
        effects.stream()
                .filter(effect -> effect instanceof ImmediateVFX)
                .forEach(effect -> ((ImmediateVFX) effect).renderImmediate(rawTpl, renderInfo, drawBuffer, pTicks));

        RenderType drawType = this.getRenderType();
        VertexConsumer buf = drawBuffer.getBuffer(drawType);
        effects.forEach(effect -> effect.render(this, renderInfo, buf, pTicks));
        drawBuffer.endBatch(drawType);
    }

    @Override
    @Nullable
    public final BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
        return null;
    }

    @Override
    public final boolean isTranslucent() {
        return this.isTranslucent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectTemplate<?> that = (EffectTemplate<?>) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
