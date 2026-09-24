/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.util.SphereBuilder;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXColorSphere
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXColorSphere extends EntityVisualFX {

    private List<SphereBuilder.TriangleFace> sphereFaces = new ArrayList<>();

    public VFXColorSphere(Vector3 pos) {
        super(pos);
    }

    @Override
    protected void updateBoundingBox() {
        float scale = this.getScale();

        this.setRenderBox(new AABB(
                this.pos.getX() - scale,
                this.pos.getY() - scale,
                this.pos.getZ() - scale,
                this.pos.getX() + scale,
                this.pos.getY() + scale,
                this.pos.getZ() + scale
        ));
    }

    public VFXColorSphere setup(Vector3.RotAxis axis, float scale) {
        return this.setup(axis, scale, 8, 10);
    }

    public VFXColorSphere setup(Vector3.RotAxis axis, float scale, int fractionsSplit, int fractionsCircle) {
        this.setScale(scale);

        Vector3 dir = axis.getVector().multiply(scale);
        fractionsSplit = Mth.clamp(fractionsSplit, 2, Integer.MAX_VALUE);
        fractionsCircle = Mth.clamp(fractionsCircle, 3, Integer.MAX_VALUE);
        this.sphereFaces = SphereBuilder.buildFaces(dir, fractionsSplit, fractionsCircle);
        this.updateBoundingBox();
        return this;
    }

    @Override
    public void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks) {
        Vector3 relativePos = this.getRenderOffset(this.getInterpolatedPos(pTicks), pTicks).subtract(renderInfo.getPosition());
        ColorWrapper color = this.getColor(pTicks).copyWithAlpha(this.getAlphaI(pTicks));

        this.sphereFaces.forEach(face -> {
            relativePos.copy().add(face.getV1()).drawPos(vb).setColor(color.getColor());
            relativePos.copy().add(face.getV2()).drawPos(vb).setColor(color.getColor());
            relativePos.copy().add(face.getV3()).drawPos(vb).setColor(color.getColor());
        });
    }
}
