/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXFloatingCube
 * Created by HellFirePvP
 * Date: 21.10.2017 / 17:41
 */
public class EntityFXFloatingCube extends EntityComplexFX {

    private final TextureAtlasSprite tas;
    private final AbstractRenderableTexture tex;

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
    private RefreshFunction refreshFunction = null;
    private float alphaMultiplier = 1F, tumbleIntensityMultiplier = 1F;
    private ScaleFunction<EntityFXFloatingCube> scaleFunction = (fx, pos, pTicks, scaleIn) -> scaleIn;
    private float scale = 1F;
    private boolean disableDepth = false;

    private float textureSubSizePercentage = 1F;
    private int lightCoordX = -1, lightCoordY = -1;

    private Function<EntityFXFloatingCube, Color> colorHandler = null;

    public EntityFXFloatingCube(IBlockState blockState) {
        this.tas = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState);
        this.tex = null;
    }

    public EntityFXFloatingCube(TextureAtlasSprite tas) {
        this.tas = tas;
        this.tex = null;
    }

    public EntityFXFloatingCube(AbstractRenderableTexture tex) {
        this.tas = null;
        this.tex = tex;
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

    public EntityFXFloatingCube setColorHandler(Function<EntityFXFloatingCube, Color> colorHandler) {
        this.colorHandler = colorHandler;
        return this;
    }

    public EntityFXFloatingCube setTextureSubSizePercentage(float textureSubSizePercentage) {
        this.textureSubSizePercentage = textureSubSizePercentage;
        return this;
    }

    public EntityFXFloatingCube setTumbleIntensityMultiplier(float tumbleIntensityMultiplier) {
        this.tumbleIntensityMultiplier = tumbleIntensityMultiplier;
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

    public EntityFXFloatingCube setRefreshFunction(RefreshFunction refreshFunction) {
        this.refreshFunction = refreshFunction;
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
        return 2;
    }

    @Override
    public void tick() {
        this.age++;

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

        if(this.tumbleIntensityMultiplier > 0 && this.rotationChange.lengthSquared() > 0) {
            Vector3 degAxis = rotationDegreeAxis.clone();
            Vector3 modify = this.rotationChange.clone().multiply(tumbleIntensityMultiplier);
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            this.rotationDegreeAxis.add(modify);

            Vector3 newDegAxis = rotationDegreeAxis;
            newDegAxis.setX(newDegAxis.getX() % 360D).setY(newDegAxis.getY() % 360D).setZ(newDegAxis.getZ() % 360D);
            if(!degAxis.add(modify).equals(newDegAxis)) {
                this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone().subtract(modify);
            }
        } else {
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
        }

        if (this.refreshFunction != null && this.canRemove() && this.refreshFunction.shouldRefresh()) {
            this.age = 0;
        }
    }

    @Override
    public void render(float pTicks) {
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        if(blending == null) {
            GlStateManager.disableBlend();
        } else {
            GlStateManager.enableBlend();
            GL11.glEnable(GL11.GL_BLEND);
            blending.applyStateManager();
            blending.apply();
        }
        if(disableDepth) {
            GlStateManager.disableDepth();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        GlStateManager.disableCull();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);

        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);

        Vector3 translateTo = getInterpolatedPosition(pTicks);
        GlStateManager.translate(translateTo.getX(), translateTo.getY(), translateTo.getZ());

        float scaleF = this.scale;
        scaleF = scaleFunction.getScale(this, translateTo, pTicks, scaleF);

        Vector3 rotation = getInterpolatedRotation(pTicks);
        GlStateManager.rotate(((float) rotation.getX()), 1, 0, 0);
        GlStateManager.rotate(((float) rotation.getY()), 0, 1, 0);
        GlStateManager.rotate(((float) rotation.getZ()), 0, 0, 1);

        Point.Double uv = null;
        if (tex != null) {
            tex.bindTexture();
            uv = tex.getUVOffset();
        }

        double u = uv != null ? uv.x : tas.getMinU();
        double v = uv != null ? uv.y : tas.getMinV();

        double uLength = (uv != null ? tex.getUWidth() : (tas.getMaxU() - tas.getMinU())) * textureSubSizePercentage;
        double vLength = (uv != null ? tex.getVWidth() : (tas.getMaxV() - tas.getMinV())) * textureSubSizePercentage;

        Color c = Color.WHITE;
        if (colorHandler != null) {
            Color tmp = colorHandler.apply(this);
            if (tmp != null) {
                c = tmp;
            }
        }

        float alpha = alphaFunction.getAlpha(age, maxAge);
        alpha *= alphaMultiplier;

        float cR = ((float) c.getRed()) / 255F;
        float cG = ((float) c.getGreen()) / 255F;
        float cB = ((float) c.getBlue()) / 255F;

        if(lightCoordX == -1 && lightCoordY == -1) {
            RenderingUtils.renderTexturedCubeCentralWithColor(new Vector3(), scaleF,
                    u, v, uLength, vLength,
                    cR, cG, cB, alpha);
        } else {
            RenderingUtils.renderTexturedCubeCentralWithLightAndColor(new Vector3(), scaleF,
                    u, v, uLength, vLength,
                    lightCoordX, lightCoordY,
                    cR, cG, cB, alpha);
        }

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
        GlStateManager.enableCull();
        if(disableDepth) {
            GlStateManager.enableDepth();
        }
        Blending.DEFAULT.applyStateManager();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
        TextureHelper.refreshTextureBindState();
    }

}
