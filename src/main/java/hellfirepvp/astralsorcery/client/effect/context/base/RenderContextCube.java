/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context.base;

import hellfirepvp.astralsorcery.client.effect.vfx.FXCube;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.RenderType;

import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextCube
 * Created by HellFirePvP
 * Date: 18.07.2019 / 14:07
 */
public class RenderContextCube extends BatchRenderContext<FXCube> {

    public RenderContextCube(RenderType renderType, BiFunction<BatchRenderContext<FXCube>, Vector3, FXCube> particleCreator) {
        super(renderType, particleCreator);
    }

    public RenderContextCube(AbstractRenderableTexture texture, RenderType renderType, BiFunction<BatchRenderContext<FXCube>, Vector3, FXCube> particleCreator) {
        super(new SpriteSheetResource(texture), renderType, particleCreator);
    }

    public RenderContextCube(SpriteSheetResource sprite, RenderType renderType, BiFunction<BatchRenderContext<FXCube>, Vector3, FXCube> particleCreator) {
        super(sprite, renderType, particleCreator);
    }

}
