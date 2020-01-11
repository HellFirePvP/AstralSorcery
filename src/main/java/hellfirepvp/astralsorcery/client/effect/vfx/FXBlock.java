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
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXBlock
 * Created by HellFirePvP
 * Date: 18.07.2019 / 16:09
 */
public class FXBlock extends EntityVisualFX {

    private BlockState blockState = null;

    private Vector3 rotationDegreeAxis = new Vector3();
    private Vector3 prevRotationDegreeAxis = new Vector3();
    private Vector3 rotationChange = new Vector3();

    public FXBlock(Vector3 pos) {
        super(pos);
    }

    public FXBlock setBlockState(BlockState blockState) {
        this.blockState = blockState;
        return this;
    }

    public FXBlock tumble() {
        this.rotationDegreeAxis = Vector3.positiveYRandom().multiply(360);
        this.rotationChange = Vector3.random().multiply(12);
        return this;
    }

    public Vector3 getInterpolatedRotation(float percent) {
        return new Vector3(
                RenderingVectorUtils.interpolate(prevRotationDegreeAxis.getX(), rotationDegreeAxis.getX(), percent),
                RenderingVectorUtils.interpolate(prevRotationDegreeAxis.getY(), rotationDegreeAxis.getY(), percent),
                RenderingVectorUtils.interpolate(prevRotationDegreeAxis.getZ(), rotationDegreeAxis.getZ(), percent));
    }

    @Override
    public void tick() {
        super.tick();

        if(this.rotationChange.lengthSquared() > 0) {
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            this.rotationDegreeAxis.add(this.rotationChange);
        }
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, BufferContext buf, float pTicks) {
        if (this.blockState == null) {
            return;
        }
        float alpha = this.getAlpha(pTicks);
        float scale = this.getScale(pTicks);
        Color c = this.getColor(pTicks);
        float r = c.getRed() / 255F;
        float g = c.getGreen() / 255F;
        float b = c.getBlue() / 255F;

        GlStateManager.pushMatrix();
        GlStateManager.color4f(r, g, b, alpha);

        Vector3 translateTo = this.getRenderPosition(pTicks);
        GlStateManager.translated(translateTo.getX(), translateTo.getY(), translateTo.getZ());

        GlStateManager.translated(0.5, 0.5, 0.5);
        GlStateManager.scalef(scale, scale, scale);
        GlStateManager.translated(-0.5, -0.5, -0.5);

        Vector3 rotation = this.getInterpolatedRotation(pTicks);
        GlStateManager.translated(0.5, 0.5, 0.5);
        GlStateManager.rotated(rotation.getX(), 1, 0, 0);
        GlStateManager.rotated(rotation.getY(), 0, 1, 0);
        GlStateManager.rotated(rotation.getZ(), 0, 0, 1);
        GlStateManager.translated(-0.5, -0.5, -0.5);

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        RenderingUtils.renderSimpleBlockModel(this.blockState, buf);
        buf.draw();

        GlStateManager.popMatrix();
    }
}
