/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context.base;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.draw.BufferBatchHelper;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.order.OrderSortable;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BatchRenderContext
 * Created by HellFirePvP
 * Date: 07.07.2019 / 10:58
 */
public class BatchRenderContext<T extends EntityVisualFX> extends OrderSortable {

    private static BufferContext ctx = BufferBatchHelper.make();
    private static int counter = 0;

    private final int id;
    private final SpriteSheetResource sprite;
    protected BiConsumer<BufferContext, Float> before;
    protected Consumer<Float> after;
    protected BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator;
    private RenderTarget renderTarget = RenderTarget.RENDERLOOP;

    public BatchRenderContext(AbstractRenderableTexture resource,
                              BiConsumer<BufferContext, Float> before,
                              Consumer<Float> after,
                              BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator) {
        this(new SpriteSheetResource(resource), before, after, particleCreator);
    }

    public BatchRenderContext(SpriteSheetResource sprite,
                              BiConsumer<BufferContext, Float> before,
                              Consumer<Float> after,
                              BiFunction<BatchRenderContext<T>, Vector3, T> particleCreator) {
        this.id = counter++;
        this.sprite = sprite;
        this.before = before;
        this.after = after;
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

    public BufferContext prepare(float pTicks) {
        sprite.bindTexture();
        before.accept(ctx, pTicks);
        return ctx;
    }

    public void draw(float pTicks) {
        Vec3d view = RenderInfo.getInstance().getView();
        ctx.sortVertexData((float) view.x, (float) view.y, (float) view.z);
        ctx.draw();
        after.accept(pTicks);
    }

    public BatchRenderContext setRenderTarget(RenderTarget renderTarget) {
        this.renderTarget = renderTarget;
        return this;
    }

    public RenderTarget getRenderTarget() {
        return renderTarget;
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

    public static enum RenderTarget {

        RENDERLOOP

    }
}
