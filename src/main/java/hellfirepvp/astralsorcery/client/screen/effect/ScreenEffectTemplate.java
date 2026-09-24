/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.lib.ShadersAS;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenEffectTemplate
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenEffectTemplate<T extends EntityVisualScreenFX> extends EffectTemplate<T> {

    private final ScreenParticleCreator<T> particleCreator;

    public ScreenEffectTemplate(RenderType renderType, boolean isTranslucent, ScreenParticleCreator<T> particleCreator) {
        super(renderType, isTranslucent, pos -> null);
        this.particleCreator = particleCreator;
    }

    @Override
    public T createParticle(Vector3 pos) {
        throw new UnsupportedOperationException("Use createParticle with template for screen effects.");
    }

    @Override
    public void renderAll(List<T> effects, Camera renderInfo, MultiBufferSource.BufferSource drawBuffer, float pTicks) {
        throw new UnsupportedOperationException("Unable to draw screen effects with world render context.");
    }

    public T createParticle(ScreenEffectTicket<?, ?> ticket, double x, double y) {
        return this.particleCreator.create(ticket, x, y);
    }

    public void renderAll(List<T> effects, GuiGraphics graphics, float pTicks) {
        if (effects.isEmpty()) return;
        VertexConsumer buf = graphics.bufferSource().getBuffer(this.getRenderType());
        effects.forEach(effect -> effect.render(this, buf, graphics, pTicks));
    }

    public interface ScreenParticleCreator<T extends EntityVisualScreenFX> {

        public T create(ScreenEffectTicket<?, ?> ticket, double x, double y);

    }
}
