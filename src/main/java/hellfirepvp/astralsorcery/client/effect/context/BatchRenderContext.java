/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.EffectProperties;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import net.minecraft.client.renderer.vertex.VertexBuffer;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BatchRenderContext
 * Created by HellFirePvP
 * Date: 07.07.2019 / 10:58
 */
public class BatchRenderContext {

    private static int counter = 0;

    private final int id;
    private final SpriteSheetResource sprite;
    private final int renderLayer;
    private final Supplier<VertexBuffer> before;
    private final Consumer<VertexBuffer> after;
    private RenderTarget renderTarget = RenderTarget.RENDERLOOP;

    public BatchRenderContext(AbstractRenderableTexture resource, Supplier<VertexBuffer> before, Consumer<VertexBuffer> after, int renderLayer) {
        this(new SpriteSheetResource(resource), before, after, renderLayer);
    }

    public BatchRenderContext(SpriteSheetResource sprite, Supplier<VertexBuffer> before, Consumer<VertexBuffer> after, int renderLayer) {
        this.renderLayer = renderLayer;
        this.id = counter++;
        this.before = before;
        this.after = after;
        this.sprite = sprite;
    }

    public EffectProperties makeProperties() {
        return new EffectProperties(this);
    }

    public BatchRenderContext setRenderTarget(RenderTarget renderTarget) {
        this.renderTarget = renderTarget;
        return this;
    }

    public SpriteSheetResource getSprite() {
        return sprite;
    }

    public VertexBuffer prepare() {
        sprite.bindTexture();
        return before.get();
    }

    public void draw(VertexBuffer vb) {
        after.accept(vb);
    }

    //Valid layers: 0, 1, 2
    //Lower layers are rendered first.
    public int getLayer() {
        return this.renderLayer;
    }

    public RenderTarget getRenderTarget() {
        return renderTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchRenderContext that = (BatchRenderContext) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static enum RenderTarget {

        RENDERLOOP

    }
}
