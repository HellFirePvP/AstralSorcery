/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.function.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityVisualFX
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:21
 */
public abstract class EntityVisualFX extends EntityComplexFX {

    private Vector3 pos;
    private Vector3 oldPos;
    private Vector3 motion = new Vector3();

    private float alphaMultiplier = 1F;
    private float scaleMultiplier = 1F;

    private VFXAlphaFunction alphaFunction = VFXAlphaFunction.CONSTANT;
    private VFXRenderOffsetController renderOffsetController = VFXRenderOffsetController.IDENTITY;
    private VFXScaleFunction scaleFunction = VFXScaleFunction.IDENTITY;

    private VFXMotionController motionController = VFXMotionController.IDENTITY;
    private VFXPositionController positionController = VFXPositionController.CONSTANT;

    public EntityVisualFX(double x, double y, double z) {
        this(new Vector3(x, y, z));
    }

    public EntityVisualFX(Vector3 pos) {
        this.pos = pos;
        this.oldPos = this.pos.clone();
    }

    public void setAlphaMultiplier(float alphaMultiplier) {
        this.alphaMultiplier = alphaMultiplier;
    }

    public void setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
    }

    public float getAlphaMultiplier() {
        return alphaMultiplier;
    }

    public float getScaleMultiplier() {
        return scaleMultiplier;
    }

    public Vector3 getPosition() {
        return pos;
    }

    public Vector3 getOldPosition() {
        return oldPos;
    }

    public void setPosition(Vector3 pos) {
        this.pos = pos.clone();
        this.oldPos = pos.clone();
    }

    public Vector3 getMotion() {
        return motion;
    }

    public void setMotion(Vector3 motion) {
        this.motion = motion;
    }

    @Override
    public void tick() {
        super.tick();

        this.motion = this.motionController.updateMotion(this, this.getMotion().clone());

        Vector3 newPos = this.positionController.updatePosition(this, this.getPosition().clone(), this.getMotion().clone());
        this.oldPos = this.pos.clone();
        this.pos = newPos;
    }

    public float getAlpha(float pTicks) {
        return this.alphaFunction.getAlpha(this, this.getAlphaMultiplier(), pTicks);
    }

    public float getScale(float pTicks) {
        return this.scaleFunction.getScale(this, this.getScaleMultiplier(), pTicks);
    }

    public Vector3 getRenderPosition(float pTicks) {
        return this.renderOffsetController.changeRenderPosition(this, pTicks);
    }

    public <T extends EntityVisualFX> T alpha(VFXAlphaFunction<?> alphaFunction) {
        this.alphaFunction = alphaFunction;
        return (T) this;
    }

    public <T extends EntityVisualFX> T  renderOffset(VFXRenderOffsetController<?> renderOffsetController) {
        this.renderOffsetController = renderOffsetController;
        return (T) this;
    }

    public <T extends EntityVisualFX> T  scale(VFXScaleFunction<?> scaleFunction) {
        this.scaleFunction = scaleFunction;
        return (T) this;
    }

    public <T extends EntityVisualFX> T  motion(VFXMotionController<?> motionController) {
        this.motionController = motionController;
        return (T) this;
    }

    public <T extends EntityVisualFX> T  position(VFXPositionController<?> positionController) {
        this.positionController = positionController;
        return (T) this;
    }

}
