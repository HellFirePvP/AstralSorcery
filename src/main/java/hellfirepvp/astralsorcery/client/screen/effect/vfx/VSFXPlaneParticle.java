/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect.vfx;

import com.mojang.blaze3d.vertex.*;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.screen.effect.EntityVisualScreenFX;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicket;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VSFXPlaneParticle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VSFXPlaneParticle extends EntityVisualScreenFX {

    private SpriteSheet spriteSheet;

    public VSFXPlaneParticle(ScreenEffectTicket<?, ?> ticket, double x, double y, AbstractRenderTexture texture) {
        this(ticket, x, y, texture.asSpriteSheet());
    }

    public VSFXPlaneParticle(ScreenEffectTicket<?, ?> ticket, double x, double y, SpriteSheet spriteSheet) {
        super(ticket, x, y);
        this.spriteSheet = spriteSheet;
    }

    protected void setSpriteSheet(SpriteSheet spriteSheet) {
        this.spriteSheet = spriteSheet;
    }

    @Override
    public void render(EffectTemplate<?> ctx, VertexConsumer buf, GuiGraphics graphics, float pTicks) {
        Vector3 relativePos = this.getRenderOffset(this.getInterpolatedPos(pTicks), pTicks);
        float scale = this.getScale(pTicks);
        int color = this.getColor(pTicks).copyWithAlpha(this.getAlphaI(pTicks)).getColor();
        UVFrame uv = this.spriteSheet.getUV(this.getAge());

        float x = (float) relativePos.getX() - (scale / 2);
        float y = (float) relativePos.getY() - (scale / 2);

        float u0 = uv.u();
        float v0 = uv.v();
        float u1 = uv.u() + uv.uWidth();
        float v1 = uv.v() + uv.vHeight();

        PoseStack.Pose pose = graphics.pose().last();
        buf.addVertex(pose, x,         y,         0).setUv(u0, v0).setColor(color);
        buf.addVertex(pose, x,         y + scale, 0).setUv(u0, v1).setColor(color);
        buf.addVertex(pose, x + scale, y + scale, 0).setUv(u1, v1).setColor(color);
        buf.addVertex(pose, x + scale, y,         0).setUv(u1, v0).setColor(color);
    }
}
