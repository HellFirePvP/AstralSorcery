/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.vfx.FXLightning;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextLightning
 * Created by HellFirePvP
 * Date: 17.07.2019 / 22:30
 */
public class RenderContextLightning extends BatchRenderContext<FXLightning> {

    public RenderContextLightning() {
        super(TexturesAS.TEX_LIGHTNING_PART, 0,
                (ctx, pTicks) -> {
                    GlStateManager.pushMatrix();
                    RenderingVectorUtils.removeStandartTranslationFromTESRMatrix(pTicks);
                    GlStateManager.enableBlend();
                    GlStateManager.disableCull();
                    GlStateManager.disableAlphaTest();
                    Blending.ADDITIVE_ALPHA.applyStateManager();
                    ctx.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                },
                (pTicks) -> {
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.enableAlphaTest();
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                },
                (ctx, pos) -> new FXLightning(pos));
    }

}
