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
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.util.SphereBuilder;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXColorEffectSphere
 * Created by HellFirePvP
 * Date: 18.07.2019 / 21:10
 */
public class FXColorEffectSphere extends EntityVisualFX {

    private double alphaFadeMaxDist = -1;
    private boolean removeIfInvisible = false;

    private List<SphereBuilder.TriangleFace> sphereFaces = new LinkedList<>();

    public FXColorEffectSphere(Vector3 pos) {
        super(pos);
    }

    public FXColorEffectSphere setupSphere(Vector3 axis, float scale) {
        return this.setupSphere(axis, scale, 8, 10);
    }

    public FXColorEffectSphere setupSphere(Vector3 axis, float scale, int fractionsSplit, int fractionsCircle) {
        this.setScaleMultiplier(scale);

        Vector3 actAxis = axis.clone().normalize().multiply(scale);
        fractionsSplit = MathHelper.clamp(fractionsSplit, 2, Integer.MAX_VALUE);
        fractionsCircle = MathHelper.clamp(fractionsCircle, 3, Integer.MAX_VALUE);
        this.sphereFaces = SphereBuilder.buildFaces(actAxis, fractionsSplit, fractionsCircle);
        return this;
    }

    public FXColorEffectSphere setAlphaFadeDistance(double fadeDistance) {
        if (fadeDistance > 0) {
            this.alpha((fx, alpha, pTicks) -> {
                Entity rView = Minecraft.getInstance().getRenderViewEntity();
                if (rView == null) {
                    rView = Minecraft.getInstance().player;
                }
                Vector3 plVec = Vector3.atEntityCenter(rView);
                double dst = plVec.distance(getRenderPosition(pTicks)) - 1.2;

                alpha *= 1D - (dst / this.alphaFadeMaxDist);
                alpha = MathHelper.clamp(alpha, 0, 1);
                return alpha;
            });
        } else {
            this.alpha(VFXAlphaFunction.CONSTANT);
        }
        return this;
    }

    public FXColorEffectSphere setRemoveIfInvisible(boolean removeIfInvisible) {
        this.removeIfInvisible = removeIfInvisible;
        return this;
    }

    @Override
    public void tick() {
        if (alphaFadeMaxDist == -1 && !removeIfInvisible) {
            super.tick();
        }
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {
        int alpha = this.getAlpha(pTicks);
        if (this.removeIfInvisible && alpha <= 0) {
            this.requestRemoval();
            return;
        }
        Color c = this.getColor(pTicks);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        Matrix4f matr = renderStack.getLast().getMatrix();
        Vector3 pos = this.getRenderPosition(pTicks);
        for (SphereBuilder.TriangleFace face : this.sphereFaces) {
            pos.clone().add(face.getV1()).drawPos(matr, vb).color(r, g, b, alpha).endVertex();
            pos.clone().add(face.getV2()).drawPos(matr, vb).color(r, g, b, alpha).endVertex();
            pos.clone().add(face.getV3()).drawPos(matr, vb).color(r, g, b, alpha).endVertex();
        }
    }
}
