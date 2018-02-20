/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.block;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.AirBlockRenderWorld;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectTranslucentFallingBlock
 * Created by HellFirePvP
 * Date: 12.03.2017 / 11:09
 */
public class EffectTranslucentFallingBlock extends EntityComplexFX {

    private final IBlockState blockState;

    private MotionController<EffectTranslucentFallingBlock> motionController = (fx, v) -> v;
    private PositionController<EffectTranslucentFallingBlock> positionController = (fx, v, m) -> v.add(m);
    private Vector3 rotationDegreeAxis = new Vector3();
    private Vector3 prevRotationDegreeAxis = new Vector3();
    private Vector3 rotationChange = new Vector3();
    private Vector3 motion = new Vector3();
    private Vector3 position = new Vector3();
    private Vector3 prevPosition = new Vector3();

    private AlphaFunction alphaFunction = AlphaFunction.CONSTANT;
    private float alphaMultiplier = 1F;
    private ScaleFunction<EffectTranslucentFallingBlock> scaleFunction = (fx, pTicks, scaleIn) -> 1F;
    private float scale = 1F;
    private boolean disableDepth = false;

    public EffectTranslucentFallingBlock(IBlockState blockState) {
        this.blockState = blockState;
    }

    public EffectTranslucentFallingBlock setDisableDepth(boolean disableDepth) {
        this.disableDepth = disableDepth;
        return this;
    }

    public EffectTranslucentFallingBlock setPosition(Vector3 position) {
        this.position = position;
        this.prevPosition = this.position.clone();
        return this;
    }

    public EffectTranslucentFallingBlock setPosition(double x, double y, double z) {
        this.position = new Vector3(x, y, z);
        this.prevPosition = this.position.clone();
        return this;
    }

    public EffectTranslucentFallingBlock setMotion(Vector3 motion) {
        this.motion = motion;
        return this;
    }

    public EffectTranslucentFallingBlock setMotion(double x, double y, double z) {
        this.motion = new Vector3(x, y, z);
        return this;
    }

    public EffectTranslucentFallingBlock setMotionController(MotionController<EffectTranslucentFallingBlock> motionController) {
        this.motionController = motionController;
        return this;
    }

    public EffectTranslucentFallingBlock setPositionController(PositionController<EffectTranslucentFallingBlock> positionController) {
        this.positionController = positionController;
        return this;
    }

    public EffectTranslucentFallingBlock setAlphaFunction(AlphaFunction alphaFunction) {
        this.alphaFunction = alphaFunction;
        return this;
    }

    public EffectTranslucentFallingBlock setAlphaMultiplier(float alphaMultiplier) {
        this.alphaMultiplier = alphaMultiplier;
        return this;
    }

    public EffectTranslucentFallingBlock setScaleFunction(ScaleFunction<EffectTranslucentFallingBlock> scaleFunction) {
        this.scaleFunction = scaleFunction;
        return this;
    }

    public EffectTranslucentFallingBlock setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public EffectTranslucentFallingBlock tumble() {
        this.rotationDegreeAxis = Vector3.positiveYRandom().multiply(360);
        this.rotationChange = Vector3.random().multiply(12);
        return this;
    }

    public Vector3 getMotion() {
        return motion;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getRotation() {
        return rotationDegreeAxis;
    }

    public Vector3 getInterpolatedPosition(float percent) {
        return new Vector3(
                RenderingUtils.interpolate(prevPosition.getX(), position.getX(), percent),
                RenderingUtils.interpolate(prevPosition.getY(), position.getY(), percent),
                RenderingUtils.interpolate(prevPosition.getZ(), position.getZ(), percent));
    }

    public Vector3 getInterpolatedRotation(float percent) {
        return new Vector3(
                RenderingUtils.interpolate(prevRotationDegreeAxis.getX(), rotationDegreeAxis.getX(), percent),
                RenderingUtils.interpolate(prevRotationDegreeAxis.getY(), rotationDegreeAxis.getY(), percent),
                RenderingUtils.interpolate(prevRotationDegreeAxis.getZ(), rotationDegreeAxis.getZ(), percent));
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public void tick() {
        super.tick();

        Vector3 motion = this.motion.clone();
        if (motionController != null) {
            motion = motionController.updateMotion(this, motion);
        }
        this.motion = motion.clone();
        if (positionController != null) {
            Vector3 newPos = positionController.updatePosition(this, this.position.clone(), this.motion);
            this.prevPosition = this.position.clone();
            this.position = newPos;
        } else {
            this.prevPosition = this.position.clone();
            this.position.add(this.motion);
        }

        if(this.rotationChange.lengthSquared() > 0) {
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            this.rotationDegreeAxis.add(this.rotationChange);
        }
    }

    @Override
    public void render(float pTicks) {
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.ADDITIVEDARK.apply();
        if(disableDepth) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        GL11.glDisable(GL11.GL_CULL_FACE);

        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);

        Vector3 translateTo = getInterpolatedPosition(pTicks);
        GL11.glTranslated(translateTo.getX(), translateTo.getY(), translateTo.getZ());

        float alpha = alphaFunction.getAlpha(age, maxAge);
        alpha *= alphaMultiplier;
        GL11.glColor4f(1F, 1F, 1F, alpha);

        float scaleF = this.scale;
        scaleF = scaleFunction.getScale(this, pTicks, scaleF);
        GL11.glTranslated(0.5, 0.5, 0.5);
        GL11.glScaled(scaleF, scaleF, scaleF);
        GL11.glTranslated(-0.5, -0.5, -0.5);

        Vector3 rotation = getInterpolatedRotation(pTicks);
        GL11.glTranslated(0.5, 0.5, 0.5);
        GL11.glRotated(rotation.getX(), 1, 0, 0);
        GL11.glRotated(rotation.getY(), 0, 1, 0);
        GL11.glRotated(rotation.getZ(), 0, 0, 1);
        GL11.glTranslated(-0.5, -0.5, -0.5);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        IBlockAccess world = new AirBlockRenderWorld(Biomes.PLAINS, Minecraft.getMinecraft().world.getWorldType());
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(this.blockState, BlockPos.ORIGIN, world, vb);
        tes.draw();

        GL11.glEnable(GL11.GL_CULL_FACE);
        if(disableDepth) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        TextureHelper.refreshTextureBindState();
    }

}
