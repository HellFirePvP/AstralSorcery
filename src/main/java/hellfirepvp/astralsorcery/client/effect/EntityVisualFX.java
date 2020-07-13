/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.*;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityVisualFX
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:21
 */
public abstract class EntityVisualFX extends EntityComplexFX {

    private Vector3 oldPos;
    private Vector3 motion = new Vector3();
    private float gravityY = 0;

    private float alphaMultiplier = 1F;
    private float scaleMultiplier = 1F;

    private VFXAlphaFunction alphaFunction = VFXAlphaFunction.CONSTANT;
    private VFXRenderOffsetFunction renderOffsetFunction = VFXRenderOffsetFunction.IDENTITY;
    private VFXScaleFunction scaleFunction = VFXScaleFunction.IDENTITY;
    private VFXColorFunction colorFunction = VFXColorFunction.WHITE;

    private VFXMotionController motionController = VFXMotionController.IDENTITY;
    private VFXPositionController positionController = VFXPositionController.CONSTANT;

    public EntityVisualFX(Vector3 pos) {
        super(pos);
        this.oldPos = this.pos.clone();
    }

    public <T extends EntityVisualFX> T setAlphaMultiplier(float alphaMultiplier) {
        this.alphaMultiplier = alphaMultiplier;
        return (T) this;
    }

    public <T extends EntityVisualFX> T setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
        return (T) this;
    }

    public float getAlphaMultiplier() {
        return alphaMultiplier;
    }

    public float getScaleMultiplier() {
        return scaleMultiplier;
    }

    public Vector3 getOldPosition() {
        return oldPos.clone();
    }

    public <T extends EntityComplexFX> T setPosition(Vector3 pos) {
        super.setPosition(pos);
        this.oldPos = pos.clone();
        return (T) this;
    }

    public Vector3 getMotion() {
        return motion.clone();
    }

    public <T extends EntityVisualFX> T setMotion(Vector3 motion) {
        this.motion = motion.clone();
        return (T) this;
    }

    public <T extends EntityVisualFX> T setGravityStrength(float grav) {
        this.gravityY = grav;
        return (T) this;
    }

    @Override
    public void tick() {
        super.tick();

        this.motion = this.motionController.updateMotion(this, this.getMotion().addY(-this.gravityY));

        Vector3 newPos = this.positionController.updatePosition(this, this.getPosition(), this.getMotion());
        this.oldPos = this.pos.clone();
        this.pos = newPos;
    }

    public abstract <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks);

    public int getAlpha(float pTicks) {
        return (int) (this.alphaFunction.getAlpha(this, this.getAlphaMultiplier(), pTicks) * 255F);
    }

    public float getScale(float pTicks) {
        return this.scaleFunction.getScale(this, this.getScaleMultiplier(), pTicks);
    }

    public Color getColor(float pTicks) {
        return this.colorFunction.getColor(this, pTicks);
    }

    public Vector3 getRenderPosition(float pTicks) {
        return this.renderOffsetFunction.changeRenderPosition(this,
                RenderingVectorUtils.interpolate(this.getOldPosition(), this.getPosition(), pTicks), pTicks);
    }

    public <T extends EntityVisualFX> T color(VFXColorFunction<?> colorFunction) {
        this.colorFunction = colorFunction;
        return (T) this;
    }

    public <T extends EntityVisualFX> T alpha(VFXAlphaFunction<?> alphaFunction) {
        this.alphaFunction = alphaFunction;
        return (T) this;
    }

    public <T extends EntityVisualFX> T renderOffset(VFXRenderOffsetFunction<?> renderOffsetController) {
        this.renderOffsetFunction = renderOffsetController;
        return (T) this;
    }

    public <T extends EntityVisualFX> T scale(VFXScaleFunction<?> scaleFunction) {
        this.scaleFunction = scaleFunction;
        return (T) this;
    }

    public <T extends EntityVisualFX> T motion(VFXMotionController<?> motionController) {
        this.motionController = motionController;
        return (T) this;
    }

    public <T extends EntityVisualFX> T position(VFXPositionController<?> positionController) {
        this.positionController = positionController;
        return (T) this;
    }

}
