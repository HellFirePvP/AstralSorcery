/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXPositionFunction;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.checkerframework.checker.units.qual.A;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXLightBeam
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXLightBeam extends EntityVisualFX {

    private final SpriteSheet lightBeamSprite;
    private Vector3 to;
    private Vector3 prevTo;
    private FXPositionFunction toPositionFunction = FXPositionFunction.IDENTITY;

    private double fromSize;
    private double toSize;

    public VFXLightBeam(Vector3 pos, SpriteSheet lightBeamSprite) {
        super(pos);
        this.lightBeamSprite = lightBeamSprite;
        this.to = pos.copy();
        this.prevTo = pos.copy();
        this.updateBoundingBox();
    }

    public VFXLightBeam setup(Vector3 to, double fromSize, double toSize) {
        this.to = to;
        this.prevTo = to.copy();
        this.fromSize = fromSize;
        this.toSize = toSize;
        this.updateBoundingBox();
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

    //The actual bounding box solution doesn't work as it needs to constantly readjust and resize anyway
    @Override
    protected void updateBoundingBox() {
        if (this.to == null || this.prevTo == null) return;

        float scale = this.getScale(1F);
        this.setRenderBox(new AABB(
                this.pos.getX() - scale,
                this.pos.getY(),
                this.pos.getZ() - scale,
                this.to.getX() + scale,
                this.to.getY(),
                this.to.getZ() + scale
        ));
    }

    @Override
    public void tick() {
        super.tick();

        Vector3 newTo = this.toPositionFunction.updatePosition(this, this.getToPos(), this.getMotion());
        this.prevTo = this.to.copy();
        this.to = newTo;
        this.updateBoundingBox();
    }

    @Override
    public void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks) {
        Vector3 relativeFromPos = this.getRenderOffset(this.getInterpolatedPos(pTicks), pTicks).subtract(renderInfo.getPosition());
        Vector3 relativeToPos = this.getRenderOffset(this.getInterpolatedToPos(pTicks), pTicks).subtract(renderInfo.getPosition());
        Vector3 beamDirection = relativeToPos.copy().subtract(relativeFromPos);
        Vector3 beamNormal = beamDirection.copy().perpendicular().normalize();
        ColorWrapper color = this.getColor(pTicks).copyWithAlpha(this.getAlphaI(pTicks));
        float scale = this.getScale(pTicks);

        UVFrame uv = this.lightBeamSprite.getUV(this.getAge());

        this.renderLightBeamRay(vb, relativeFromPos, relativeToPos, beamDirection, beamNormal, Math.toRadians(  0F), scale, color, uv);
        this.renderLightBeamRay(vb, relativeFromPos, relativeToPos, beamDirection, beamNormal, Math.toRadians(120F), scale, color, uv);
        this.renderLightBeamRay(vb, relativeFromPos, relativeToPos, beamDirection, beamNormal, Math.toRadians(240F), scale, color, uv);
    }

    protected void renderLightBeamRay(VertexConsumer vb, Vector3 renderFrom, Vector3 renderTo, Vector3 beamDirection, Vector3 beamNormal, double beamAngle, float scale, ColorWrapper color, UVFrame uv) {
        Vector3 angleNorm = beamNormal.copy().rotate(beamAngle, beamDirection).normalize();
        Vector3 normFrom = angleNorm.copy().multiply(this.fromSize * scale);
        Vector3 normTo = angleNorm.multiply(this.toSize * scale);

        renderTo.copy().add(normTo.copy().multiply(-1))
                .drawPos(vb).setColor(color.getColor()).setUv(uv.u(), uv.v() + uv.vHeight());
        renderTo.copy().add(normTo.copy())
                .drawPos(vb).setColor(color.getColor()).setUv(uv.u() + uv.uWidth(), uv.v() + uv.vHeight());
        renderFrom.copy().add(normFrom.copy())
                .drawPos(vb).setColor(color.getColor()).setUv(uv.u() + uv.uWidth(), uv.v());
        renderFrom.copy().add(normFrom.copy().multiply(-1))
                .drawPos(vb).setColor(color.getColor()).setUv(uv.u(), uv.v());
    }
}
