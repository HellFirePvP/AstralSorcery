/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXFacingSprite
 * Created by HellFirePvP
 * Date: 30.08.2019 / 21:24
 */
public class FXFacingSprite extends EntityVisualFX {

    private SpriteSheetResource sprite = null;

    public FXFacingSprite(Vector3 pos) {
        super(pos);
    }

    public FXFacingSprite setSprite(SpriteSheetResource sprite) {
        this.sprite = sprite;
        return this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, BufferContext buf, float pTicks) {
        SpriteSheetResource ssr = this.sprite != null ? this.sprite : ctx.getSprite();
        Vector3 vec = this.getRenderPosition(pTicks);
        float alpha = this.getAlpha(pTicks);
        float fScale = this.getScale(pTicks);
        Color col = this.getColor(pTicks);

        ssr.bindTexture();
        Tuple<Double, Double> uvOffset = ssr.getUVOffset(this);

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        RenderingDrawUtils.renderFacingQuadVB(buf,
                vec.getX(), vec.getY(), vec.getZ(),
                pTicks, fScale, 0F,
                uvOffset.getA(), uvOffset.getB(), ssr.getULength(), ssr.getVLength(),
                col.getRed(), col.getGreen(), col.getBlue(), alpha);
        buf.draw();
    }
}
