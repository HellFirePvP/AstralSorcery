/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.Tuple;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXFacingParticle
 * Created by HellFirePvP
 * Date: 08.07.2019 / 19:29
 */
public class FXFacingParticle extends EntityVisualFX {

    public FXFacingParticle(Vector3 pos) {
        super(pos);
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {
        SpriteSheetResource ssr = ctx.getSprite();
        Vector3 vec = this.getRenderPosition(pTicks);
        int alpha = this.getAlpha(pTicks);
        float fScale = this.getScale(pTicks);
        Color col = this.getColor(pTicks);
        Tuple<Float, Float> uvOffset = ssr.getUVOffset(this.getAge());

        RenderingDrawUtils.renderFacingQuadVB(vb, renderStack,
                vec.getX(), vec.getY(), vec.getZ(),
                fScale, 0F,
                uvOffset.getA(), uvOffset.getB(), ssr.getULength(), ssr.getVLength(),
                col.getRed(), col.getGreen(), col.getBlue(), alpha);
    }
}
