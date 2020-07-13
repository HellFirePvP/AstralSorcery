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
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;

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

        if (this.rotationChange.lengthSquared() > 0) {
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            this.rotationDegreeAxis.add(this.rotationChange);
        }
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {
        if (this.blockState == null) {
            return;
        }

        int alpha = this.getAlpha(pTicks);
        Color c = this.getColor(pTicks);
        int[] colorOverride = new int[] { c.getRed(), c.getGreen(), c.getBlue(), alpha };

        Vector3 translate = this.getRenderPosition(pTicks).subtract(RenderingVectorUtils.getStandardTranslationRemovalVector(pTicks));
        Vector3 rotation = this.getInterpolatedRotation(pTicks);
        float scale = this.getScale(pTicks);

        renderStack.push();
        renderStack.translate(translate.getX(), translate.getY(), translate.getZ());

        renderStack.translate(0.5, 0.5, 0.5);
        renderStack.scale(scale, scale, scale);
        renderStack.rotate(Vector3f.XP.rotationDegrees((float) rotation.getX()));
        renderStack.rotate(Vector3f.YP.rotationDegrees((float) rotation.getY()));
        renderStack.rotate(Vector3f.ZP.rotationDegrees((float) rotation.getZ()));
        renderStack.translate(-0.5, -0.5, -0.5);

        new BufferDecoratorBuilder()
                .setColorDecorator((r, g, b, a) -> colorOverride)
                .decorate(vb, decorated -> RenderingUtils.renderSimpleBlockModel(this.blockState, renderStack, decorated));

        renderStack.pop();
    }
}
