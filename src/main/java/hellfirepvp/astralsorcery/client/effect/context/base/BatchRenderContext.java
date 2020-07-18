/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.effect.EntityDynamicFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHandler;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.order.OrderSortable;
import hellfirepvp.observerlib.client.util.RenderTypeDecorator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BatchRenderContext
 * Created by HellFirePvP
 * Date: 07.07.2019 / 10:58
 */
public class BatchRenderContext<T extends EntityVisualFX> extends OrderSortable {

    private static int counter = 0;

    private final int id;
    private final SpriteSheetResource sprite;
    protected RenderType renderType;
    protected BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator;

    public BatchRenderContext(RenderType renderType,
                              BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator) {
        this(new SpriteSheetResource(BlockAtlasTexture.getInstance()), renderType, particleCreator);
    }

    public BatchRenderContext(AbstractRenderableTexture texture,
                              RenderType renderType,
                              BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator) {
        this(new SpriteSheetResource(texture), renderType, particleCreator);
    }

    public BatchRenderContext(SpriteSheetResource sprite,
                              RenderType renderType,
                              BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator) {
        this.id = counter++;
        this.sprite = sprite;
        this.renderType = renderType;
        this.particleCreator = particleCreator.andThen(fx -> {
            int frames = this.sprite.getFrameCount();
            if (frames > 1) {
                fx.setMaxAge(frames);
            }
            return fx;
        });
    }

    public T makeParticle(Vector3 pos) {
        return this.particleCreator.apply(this, pos);
    }

    public SpriteSheetResource getSprite() {
        return sprite;
    }

    public void renderAll(List<EffectHandler.PendingEffect> effects, MatrixStack renderStack, IDrawRenderTypeBuffer drawBuffer, float pTicks) {
        //Erase type due to impossible typing
        BatchRenderContext blankCtx = this;
        effects.stream()
                .filter(effect -> effect.getEffect() instanceof EntityDynamicFX)
                .forEach(effect -> ((EntityDynamicFX) effect.getEffect()).renderNow(blankCtx, renderStack, drawBuffer, pTicks));

        RenderTypeDecorator decorated = RenderTypeDecorator.wrapSetup(this.getRenderType(), () -> {
            RenderSystem.enableTexture();
            this.getSprite().bindTexture();
        }, () -> {
            BlockAtlasTexture.getInstance().bindTexture();
            RenderSystem.disableTexture();
        });
        IVertexBuilder buf = drawBuffer.getBuffer(decorated);
        effects.forEach(effect -> effect.getEffect().render(this, renderStack, buf, pTicks));
        this.drawBatched(buf, drawBuffer);
    }

    private void drawBatched(IVertexBuilder buf, IDrawRenderTypeBuffer renderTypeBuffer) {
        if (buf instanceof BufferBuilder) {
            Vec3d view = RenderInfo.getInstance().getARI().getProjectedView();
            ((BufferBuilder) buf).sortVertexData((float) view.x, (float) view.y, (float) view.z);
        }
        renderTypeBuffer.draw();
    }

    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchRenderContext that = (BatchRenderContext) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
