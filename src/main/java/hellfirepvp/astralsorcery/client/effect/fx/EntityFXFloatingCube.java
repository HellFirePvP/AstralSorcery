/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.fx;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXFloatingCube
 * Created by HellFirePvP
 * Date: 21.10.2017 / 17:41
 */
public class EntityFXFloatingCube extends EntityComplexFX {

    private final TextureAtlasSprite tas;

    private MotionController<EntityFXFloatingCube> motionController = (fx, v) -> v;
    private PositionController<EntityFXFloatingCube> positionController = (fx, v, m) -> v.add(m);
    private Vector3 rotationDegreeAxis = new Vector3();
    private Vector3 prevRotationDegreeAxis = new Vector3();
    private Vector3 rotationChange = new Vector3();
    private Vector3 motion = new Vector3();
    private Vector3 position = new Vector3();
    private Vector3 prevPosition = new Vector3();

    private Blending blending = Blending.ADDITIVEDARK;
    private AlphaFunction alphaFunction = AlphaFunction.CONSTANT;
    private float alphaMultiplier = 1F;
    private ScaleFunction<EntityFXFloatingCube> scaleFunction = (fx, pTicks, scaleIn) -> scaleIn;
    private float scale = 1F;
    private boolean disableDepth = false;

    private float textureSubSizePercentage = 1F;
    private int lightCoordX = -1, lightCoordY = -1;

    public EntityFXFloatingCube(IBlockState blockState) {
        this.tas = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState);
    }

    public EntityFXFloatingCube(TextureAtlasSprite tas) {
        this.tas = tas;
    }

    public EntityFXFloatingCube setDisableDepth(boolean disableDepth) {
        this.disableDepth = disableDepth;
        return this;
    }

    public EntityFXFloatingCube setWorldLightCoord(World iba, BlockPos pos) {
        int light = iba.isBlockLoaded(pos) ? iba.getCombinedLight(pos, 0) : 0;
        this.lightCoordX = light >> 16 & 65535;
        this.lightCoordY = light & 65535;
        return this;
    }

    public EntityFXFloatingCube setTextureSubSizePercentage(float textureSubSizePercentage) {
        this.textureSubSizePercentage = textureSubSizePercentage;
        return this;
    }

    public EntityFXFloatingCube setBlendMode(Blending blending) {
        this.blending = blending;
        return this;
    }

    public EntityFXFloatingCube setPosition(Vector3 position) {
        this.position = position;
        this.prevPosition = this.position.clone();
        return this;
    }

    public EntityFXFloatingCube setPosition(double x, double y, double z) {
        this.position = new Vector3(x, y, z);
        this.prevPosition = this.position.clone();
        return this;
    }

    public EntityFXFloatingCube setMotion(Vector3 motion) {
        this.motion = motion;
        return this;
    }

    public EntityFXFloatingCube setMotion(double x, double y, double z) {
        this.motion = new Vector3(x, y, z);
        return this;
    }

    public EntityFXFloatingCube setMotionController(MotionController<EntityFXFloatingCube> motionController) {
        this.motionController = motionController;
        return this;
    }

    public EntityFXFloatingCube setPositionController(PositionController<EntityFXFloatingCube> positionController) {
        this.positionController = positionController;
        return this;
    }

    public EntityFXFloatingCube setAlphaFunction(AlphaFunction alphaFunction) {
        this.alphaFunction = alphaFunction;
        return this;
    }

    public EntityFXFloatingCube setAlphaMultiplier(float alphaMultiplier) {
        this.alphaMultiplier = alphaMultiplier;
        return this;
    }

    public EntityFXFloatingCube setScaleFunction(ScaleFunction<EntityFXFloatingCube> scaleFunction) {
        this.scaleFunction = scaleFunction;
        return this;
    }

    public EntityFXFloatingCube setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public EntityFXFloatingCube tumble() {
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
        if(blending == null) {
            GL11.glDisable(GL11.GL_BLEND);
        } else {
            GL11.glEnable(GL11.GL_BLEND);
            blending.apply();
        }
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
        GL11.glScaled(scaleF, scaleF, scaleF);

        Vector3 rotation = getInterpolatedRotation(pTicks);
        GL11.glRotated(rotation.getX(), 1, 0, 0);
        GL11.glRotated(rotation.getY(), 0, 1, 0);
        GL11.glRotated(rotation.getZ(), 0, 0, 1);

        double u = tas.getMinU();
        double v = tas.getMinV();

        double uLength = (tas.getMaxU() - tas.getMinU()) * textureSubSizePercentage;
        double vLength = (tas.getMaxV() - tas.getMinV()) * textureSubSizePercentage;

        if(lightCoordX == -1 && lightCoordY == -1) {
            RenderingUtils.renderTexturedCubeCentralWithColor(new Vector3(), scaleF,
                    u, v, uLength, vLength,
                    1F, 1F, 1F, 1F);
        } else {
            RenderingUtils.renderTexturedCubeCentralWithLightAndColor(new Vector3(), scaleF,
                    u, v, uLength, vLength,
                    lightCoordX, lightCoordY,
                    1F, 1F, 1F, 1F);
        }

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

    private int getLight() {
        BlockPos blockpos = this.position.toBlockPos();
        return Minecraft.getMinecraft().world.isBlockLoaded(blockpos) ? Minecraft.getMinecraft().world.getCombinedLight(blockpos, 0) : 0;
    }

}
