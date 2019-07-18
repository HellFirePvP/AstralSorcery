/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXSpritePlane;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextSpriteSheet
 * Created by HellFirePvP
 * Date: 18.07.2019 / 00:25
 */
public class RenderContextSpriteSheet extends BatchRenderContext<FXSpritePlane> {

    public RenderContextSpriteSheet(AbstractRenderableTexture tex) {
        this(new SpriteSheetResource(tex));
    }

    public RenderContextSpriteSheet(SpriteSheetResource ssr) {
        super(ssr,
                (ctx, pTicks) -> {
                    GlStateManager.pushMatrix();
                    RenderingVectorUtils.removeStandardTranslationFromTESRMatrix(pTicks);
                    GlStateManager.disableCull();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0001F);
                    GlStateManager.enableBlend();
                    Blending.DEFAULT.applyStateManager();
                    ctx.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                },
                (pTicks) -> {
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.disableBlend();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    GlStateManager.enableCull();
                    GlStateManager.popMatrix();
                },
                (ctx, pos) -> new FXSpritePlane(pos));
    }

}
