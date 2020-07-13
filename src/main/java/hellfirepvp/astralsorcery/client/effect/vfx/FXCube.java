/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.effect.EntityDynamicFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.LightmapUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Tuple;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXCube
 * Created by HellFirePvP
 * Date: 18.07.2019 / 14:07
 */
public class FXCube extends EntityVisualFX implements EntityDynamicFX {

    private TextureAtlasSprite tas = null;

    private Vector3 rotationDegreeAxis = new Vector3();
    private Vector3 prevRotationDegreeAxis = new Vector3();
    private Vector3 rotationChange = new Vector3();

    private float tumbleIntensityMultiplier = 1F;
    private float textureSubSizePercentage = 1F;

    public FXCube(Vector3 pos) {
        super(pos);
    }

    public FXCube setTextureAtlasSprite(TextureAtlasSprite tas) {
         this.tas = tas;
         return this;
    }

    public FXCube setTextureSubSizePercentage(float textureSubSizePercentage) {
        this.textureSubSizePercentage = textureSubSizePercentage;
        return this;
    }

    public FXCube setTumbleIntensityMultiplier(float tumbleIntensityMultiplier) {
        this.tumbleIntensityMultiplier = tumbleIntensityMultiplier;
        return this;
    }

    public FXCube tumble() {
        this.rotationDegreeAxis = Vector3.positiveYRandom().multiply(360);
        this.rotationChange = Vector3.random().multiply(12);
        return this;
    }

    public Vector3 getInterpolatedRotation(float pTicks) {
        return new Vector3(
                RenderingVectorUtils.interpolate(prevRotationDegreeAxis.getX(), rotationDegreeAxis.getX(), pTicks),
                RenderingVectorUtils.interpolate(prevRotationDegreeAxis.getY(), rotationDegreeAxis.getY(), pTicks),
                RenderingVectorUtils.interpolate(prevRotationDegreeAxis.getZ(), rotationDegreeAxis.getZ(), pTicks));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tumbleIntensityMultiplier > 0 && this.rotationChange.lengthSquared() > 0) {
            Vector3 degAxis = rotationDegreeAxis.clone();
            Vector3 modify = this.rotationChange.clone().multiply(tumbleIntensityMultiplier);
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            this.rotationDegreeAxis.add(modify);

            Vector3 newDegAxis = rotationDegreeAxis;
            newDegAxis.setX(newDegAxis.getX() % 360D).setY(newDegAxis.getY() % 360D).setZ(newDegAxis.getZ() % 360D);
            if (!degAxis.add(modify).equals(newDegAxis)) {
                this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone().subtract(modify);
            }
        } else {
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
        }
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {}

    @Override
    public <T extends EntityVisualFX & EntityDynamicFX> void renderNow(BatchRenderContext<T> ctx, MatrixStack renderStack, IDrawRenderTypeBuffer drawBuffer, float pTicks) {
        float u, v, uLength, vLength;
        if (this.tas != null) {
            u = this.tas.getMinU();
            v = this.tas.getMinV();
            uLength = (this.tas.getMaxU() - u) * this.textureSubSizePercentage;
            vLength = (this.tas.getMaxV() - v) * this.textureSubSizePercentage;
        } else {
            SpriteSheetResource ssr = ctx.getSprite();
            Tuple<Float, Float> uv = ssr.getUVOffset(this.getAge());
            u = uv.getA();
            v = uv.getB();
            uLength = ssr.getULength() * this.textureSubSizePercentage;
            vLength = ssr.getVLength() * this.textureSubSizePercentage;
        }

        int alpha = this.getAlpha(pTicks);
        Color c = this.getColor(pTicks);
        Vector3 translateTo = this.getRenderPosition(pTicks).subtract(RenderingVectorUtils.getStandardTranslationRemovalVector(pTicks));
        Vector3 rotation = getInterpolatedRotation(pTicks);
        float scale = this.getScale(pTicks);

        renderStack.push();
        renderStack.translate(translateTo.getX(), translateTo.getY(), translateTo.getZ());
        renderStack.rotate(Vector3f.XP.rotationDegrees((float) rotation.getX()));
        renderStack.rotate(Vector3f.YP.rotationDegrees((float) rotation.getY()));
        renderStack.rotate(Vector3f.ZP.rotationDegrees((float) rotation.getZ()));
        renderStack.scale(scale, scale, scale);

        IVertexBuilder buf = drawBuffer.getBuffer(ctx.getRenderType());
        RenderingDrawUtils.renderTexturedCubeCentralColorLighted(buf, renderStack,
                u, v, uLength, vLength,
                c.getRed(), c.getGreen(), c.getBlue(), alpha,
                LightmapUtil.getPackedFullbrightCoords());

        renderStack.pop();
    }
}
