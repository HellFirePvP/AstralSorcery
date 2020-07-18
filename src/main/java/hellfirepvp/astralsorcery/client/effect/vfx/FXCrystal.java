/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.effect.EntityDynamicFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.render.ObjModelRender;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import net.minecraft.client.renderer.Vector3f;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXCrystal
 * Created by HellFirePvP
 * Date: 05.04.2020 / 09:53
 */
public class FXCrystal extends EntityVisualFX implements EntityDynamicFX {

    private AbstractRenderableTexture alternativeTexture = null;
    private Color lightRayColor = null;
    private Vector3 rotation = new Vector3();

    public FXCrystal(Vector3 pos) {
        super(pos);
    }

    public FXCrystal rotation(float x, float y, float z) {
        this.rotation = new Vector3(x, y, z);
        return this;
    }

    public FXCrystal setLightRayColor(Color lightRayColor) {
        this.lightRayColor = lightRayColor;
        return this;
    }

    public FXCrystal setTexture(TextureQuery query) {
        this.alternativeTexture = query.resolve();
        return this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {}

    @Override
    public <T extends EntityVisualFX & EntityDynamicFX> void renderNow(BatchRenderContext<T> ctx, MatrixStack renderStack, IDrawRenderTypeBuffer drawBuffer, float pTicks) {
        if (this.alternativeTexture != null) {
            this.alternativeTexture.bindTexture();
        }

        int alpha = this.getAlpha(pTicks);
        Color c = this.getColor(pTicks);

        Vector3 vec = this.getRenderPosition(pTicks).subtract(RenderingVectorUtils.getStandardTranslationRemovalVector(pTicks));
        float scale = this.getScale(pTicks);

        if (this.lightRayColor != null) {
            long seed = 0x515F1EB654AB915EL;

            renderStack.push();
            renderStack.translate(vec.getX(), vec.getY(), vec.getZ());
            RenderingDrawUtils.renderLightRayFan(renderStack, drawBuffer, this.lightRayColor, seed, 5, 1F, 50);
            renderStack.pop();
            drawBuffer.draw();
        }

        RenderSystem.color4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, alpha / 255F);

        renderStack.push();
        renderStack.translate(vec.getX(), vec.getY() - 0.05F, vec.getZ());
        renderStack.scale(scale, scale, scale);
        renderStack.rotate(Vector3f.XP.rotationDegrees((float) rotation.getX()));
        renderStack.rotate(Vector3f.YP.rotationDegrees((float) rotation.getY()));
        renderStack.rotate(Vector3f.ZP.rotationDegrees((float) rotation.getZ()));

        BufferDecoratorBuilder.withColor((r, g, b, a) -> new int[] { c.getRed(), c.getGreen(), c.getBlue(), alpha})
                .decorate(drawBuffer.getBuffer(ctx.getRenderType()),
                        decorated -> ObjModelRender.renderCrystal(renderStack, decorated, drawBuffer::draw));

        renderStack.pop();

        RenderSystem.color4f(1F, 1F, 1F, 1F);

        if (this.alternativeTexture != null) {
            ctx.getSprite().bindTexture();
        }
    }
}
