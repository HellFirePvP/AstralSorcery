/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.Tuple;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXLightbeam
 * Created by HellFirePvP
 * Date: 17.07.2019 / 23:43
 */
public class FXLightbeam extends EntityVisualFX {

    private Vector3 from, to, aim, aimPerp;
    private double fromSize, toSize;

    public FXLightbeam(Vector3 pos) {
        super(pos);
        this.from = pos;
        this.alpha(VFXAlphaFunction.PYRAMID);
    }

    public FXLightbeam setup(Vector3 to, double fromSize, double toSize) {
        this.to = to;
        this.aim = to.clone().subtract(this.getPosition());
        this.aimPerp = aim.clone().perpendicular().normalize();
        this.fromSize = fromSize;
        this.toSize = toSize;
        return this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, BufferContext buf, float pTicks) {
        Color c = this.getColor(pTicks);
        float r = c.getRed() / 255F;
        float g = c.getGreen() / 255F;
        float b = c.getBlue() / 255F;
        float a = this.getAlpha(pTicks);
        r *= a;
        g *= a;
        b *= a;
        float scale = this.getScale(pTicks);

        renderCurrentTextureAroundAxis(buf, ctx, Math.toRadians(0F), scale, r, g, b, a);
        renderCurrentTextureAroundAxis(buf, ctx, Math.toRadians(120F), scale, r, g, b, a);
        renderCurrentTextureAroundAxis(buf, ctx, Math.toRadians(240F), scale, r, g, b, a);
    }

    private <T extends EntityVisualFX> void renderCurrentTextureAroundAxis(BufferBuilder buf, BatchRenderContext<T> ctx, double angle, float scale, float r, float g, float b, float a) {
        Vector3 perp = aimPerp.clone().rotate(angle, aim).normalize();
        Vector3 perpTo = perp.clone().multiply(toSize * scale);
        Vector3 perpFrom = perp.multiply(fromSize * scale);

        SpriteSheetResource ssr = ctx.getSprite();
        Tuple<Double, Double> uvOffset = ssr.getUVOffset(age);
        double u = uvOffset.getA();
        double v = uvOffset.getB();
        double uWidth = ssr.getULength();
        double vHeight = ssr.getVLength();

        Vector3 vec = to.clone().add(perpTo.clone().multiply(-1));
        vec.drawPos(buf).tex(u,          v + vHeight)            .color(r, g, b, a).endVertex();
        vec = to.clone().add(perpTo);
        vec.drawPos(buf).tex(u + uWidth, v + vHeight).color(r, g, b, a).endVertex();
        vec = from.clone().add(perpFrom);
        vec.drawPos(buf).tex(u + uWidth, v)                      .color(r, g, b, a).endVertex();
        vec = from.clone().add(perpFrom.clone().multiply(-1));
        vec.drawPos(buf).tex(u,          v)                                  .color(r, g, b, a).endVertex();
    }

}
