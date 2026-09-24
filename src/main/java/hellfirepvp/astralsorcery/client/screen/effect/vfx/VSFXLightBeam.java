/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect.vfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.client.effect.function.FXPositionFunction;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.screen.effect.EntityVisualScreenFX;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicket;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VSFXLightBeam
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VSFXLightBeam extends EntityVisualScreenFX {

    private final SpriteSheet lightBeamSprite;
    private Vector3 to;
    private Vector3 prevTo;
    private FXPositionFunction toPositionFunction = FXPositionFunction.IDENTITY;

    private float fromSize;
    private float toSize;

    public VSFXLightBeam(ScreenEffectTicket<?, ?> ticket, double x, double y, SpriteSheet lightBeamSprite) {
        super(ticket, x, y);
        this.lightBeamSprite = lightBeamSprite;
    }

    public VSFXLightBeam setup(Vector2f to, float fromSize, float toSize) {
        this.to = new Vector3(to.x(), to.y(), 0);
        this.prevTo = this.to.copy();
        this.fromSize = fromSize;
        this.toSize = toSize;
        return this;
    }

    public Vector3 getToPos() {
        return this.to.copy();
    }

    public Vector3 getPrevToPos() {
        return this.prevTo.copy();
    }

    public Vector3 getInterpolatedToPos(float pTicks) {
        return RenderVectorUtil.interpolate(this.getPrevToPos(), this.getToPos(), pTicks);
    }

    public <T extends EntityFX> T toPosition(FXPositionFunction<?> toPositionFunction) {
        this.toPositionFunction = toPositionFunction;
        return (T) this;
    }

    @Override
    public void tick() {
        super.tick();

        Vector3 newTo = this.toPositionFunction.updatePosition(this, this.getToPos(), this.getMotion());
        this.prevTo = this.to.copy();
        this.to = newTo;
    }

    @Override
    public void render(EffectTemplate<?> ctx, VertexConsumer buf, GuiGraphics graphics, float pTicks) {
        Vector3 relativeFromPos = this.getRenderOffset(this.getInterpolatedPos(pTicks), pTicks);
        Vector3 relativeToPos = this.getRenderOffset(this.getInterpolatedToPos(pTicks), pTicks);
        Vector2f from = new Vector2f((float) relativeFromPos.getX(), (float) relativeFromPos.getY());
        Vector2f to = new Vector2f((float) relativeToPos.getX(), (float) relativeToPos.getY());

        float scale = this.getScale(pTicks);
        int color = this.getColor(pTicks).copyWithAlpha(this.getAlphaI(pTicks)).getColor();
        UVFrame uv = this.lightBeamSprite.getUV(this.getAge());
        float sizeFrom = this.fromSize * scale;
        float sizeTo = this.toSize * scale;

        Vector2f dir = new Vector2f(from).sub(to);
        Vector2f dirNormal = new Vector2f(dir).perpendicular().normalize().mul(-1F);
        Vector2f dirNormalFrom = new Vector2f(dirNormal).mul(sizeFrom / 2F);
        Vector2f dirNormalTo = new Vector2f(dirNormal).mul(sizeTo / 2F);

        PoseStack.Pose pose = graphics.pose().last();
        buf.addVertex(pose, from.x() + dirNormalFrom.x(), from.y() + dirNormalFrom.y(), 0)
                .setUv(uv.u(), uv.v())
                .setColor(color);
        buf.addVertex(pose, to.x() + dirNormalTo.x(), to.y() + dirNormalTo.y(), 0)
                .setUv(uv.u(), uv.v() + uv.vHeight())
                .setColor(color);
        buf.addVertex(pose, to.x() - dirNormalTo.x(), to.y() - dirNormalTo.y(), 0)
                .setUv(uv.u() + uv.uWidth(), uv.v() + uv.vHeight())
                .setColor(color);
        buf.addVertex(pose, from.x() - dirNormalFrom.x(), from.y() - dirNormalFrom.y(), 0)
                .setUv(uv.u() + uv.uWidth(), uv.v())
                .setColor(color);
    }
}
