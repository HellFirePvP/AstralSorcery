/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.context.base.RenderContextCube;
import hellfirepvp.astralsorcery.client.effect.vfx.FXCube;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import org.lwjgl.opengl.GL11;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextTranslucentCube
 * Created by HellFirePvP
 * Date: 18.07.2019 / 15:04
 */
public class RenderContextTranslucentCube extends RenderContextCube {

    public RenderContextTranslucentCube(Blending blendMode) {
        super(before(blendMode), after(), (ctx, pos) -> new FXCube(pos));
    }

    public RenderContextTranslucentCube(Blending blendMode, AbstractRenderableTexture resource) {
        this(blendMode, new SpriteSheetResource(resource));
    }

    public RenderContextTranslucentCube(Blending blendMode, SpriteSheetResource sprite) {
        super(sprite, before(blendMode), after(), (ctx, pos) -> new FXCube(pos));
    }

    private static BiConsumer<BufferContext, Float> before(Blending blendMode) {
        return (ctx, pTicks) -> {
            GlStateManager.pushMatrix();
            RenderingVectorUtils.removeStandardTranslationFromTESRMatrix(pTicks);
            GlStateManager.color4f(1F, 1F, 1F, 1F);
            GlStateManager.enableBlend();
            blendMode.applyStateManager();
            GlStateManager.disableCull();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
            GlStateManager.depthMask(false);
        };
    }

    private static Consumer<Float> after() {
        return (pTicks) -> {
            GlStateManager.depthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableCull();
            Blending.DEFAULT.applyStateManager();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        };
    }

}
