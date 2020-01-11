/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXCube
 * Created by HellFirePvP
 * Date: 18.07.2019 / 14:07
 */
public class FXCube extends EntityVisualFX {

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
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, BufferContext buf, float pTicks) {
        double u, v, uLength, vLength;
        if (this.tas != null) {
            u = this.tas.getMinU();
            v = this.tas.getMinV();
            uLength = (this.tas.getMaxU() - u) * this.textureSubSizePercentage;
            vLength = (this.tas.getMaxV() - v) * this.textureSubSizePercentage;
        } else {
            SpriteSheetResource ssr = ctx.getSprite();
            Tuple<Double, Double> uv = ssr.getUVOffset(this.getAge());
            u = uv.getA();
            v = uv.getB();
            uLength = ssr.getULength() * this.textureSubSizePercentage;
            vLength = ssr.getVLength() * this.textureSubSizePercentage;
        }

        float alpha = this.getAlpha(pTicks);
        float scale = this.getScale(pTicks);
        Color c = this.getColor(pTicks);
        float r = c.getRed() / 255F;
        float g = c.getGreen() / 255F;
        float b = c.getBlue() / 255F;

        GlStateManager.pushMatrix();

        Vector3 translateTo = this.getRenderPosition(pTicks);
        GlStateManager.translated(translateTo.getX(), translateTo.getY(), translateTo.getZ());
        Vector3 rotation = getInterpolatedRotation(pTicks);
        GlStateManager.rotated(((float) rotation.getX()), 1, 0, 0);
        GlStateManager.rotated(((float) rotation.getY()), 0, 1, 0);
        GlStateManager.rotated(((float) rotation.getZ()), 0, 0, 1);

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        RenderingDrawUtils.drawWithBlockLight(15, () -> {
            RenderingDrawUtils.renderTexturedCubeCentralColor(buf, scale,
                    u, v, uLength, vLength,
                    r, g, b, alpha);
        });
        buf.draw();

        GlStateManager.popMatrix();
    }
}
