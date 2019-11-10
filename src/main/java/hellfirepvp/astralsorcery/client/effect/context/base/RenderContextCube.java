/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context.base;

import hellfirepvp.astralsorcery.client.effect.vfx.FXCube;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTextureResource;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextCube
 * Created by HellFirePvP
 * Date: 18.07.2019 / 14:07
 */
public abstract class RenderContextCube extends BatchRenderContext<FXCube> {

    protected RenderContextCube(BiConsumer<BufferContext, Float> before, Consumer<Float> after, BiFunction<BatchRenderContext<FXCube>, Vector3, FXCube> particleCreator) {
        this(BlockAtlasTextureResource.getInstance(), before, after, particleCreator);
    }

    public RenderContextCube(AbstractRenderableTexture resource, BiConsumer<BufferContext, Float> before, Consumer<Float> after, BiFunction<BatchRenderContext<FXCube>, Vector3, FXCube> particleCreator) {
        super(resource, before, after, particleCreator);
    }

    public RenderContextCube(SpriteSheetResource sprite, BiConsumer<BufferContext, Float> before, Consumer<Float> after, BiFunction<BatchRenderContext<FXCube>, Vector3, FXCube> particleCreator) {
        super(sprite, before, after, particleCreator);
    }
}
