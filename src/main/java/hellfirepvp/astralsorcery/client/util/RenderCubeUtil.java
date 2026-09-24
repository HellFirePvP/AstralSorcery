/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderCubeUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderCubeUtil {

    public static void drawCentredCube(VertexConsumer vb, PoseStack pose, UVFrame uv, ColorWrapper color, int packedLight, int packedOverlay) {
        Vector3 ux = new Vector3(1, 0, 0);
        Vector3 uy = new Vector3(0, 1, 0);
        Vector3 uz = new Vector3(0, 0, 1);

        RenderingDrawUtil.renderNormalQuad(vb, pose, new Vector3(-0.5, -0.5, 0.5), ux.copy(), uz.copy().multiply(-1), color, packedLight, packedOverlay, uv);
        RenderingDrawUtil.renderNormalQuad(vb, pose, new Vector3(-0.5, 0.5, -0.5), ux.copy(), uz.copy(), color, packedLight, packedOverlay, uv);
        RenderingDrawUtil.renderNormalQuad(vb, pose, new Vector3(-0.5, -0.5, -0.5), ux.copy(), uy.copy(), color, packedLight, packedOverlay, uv);
        RenderingDrawUtil.renderNormalQuad(vb, pose, new Vector3(0.5, -0.5, 0.5), ux.copy().multiply(-1), uy.copy(), color, packedLight, packedOverlay, uv);
        RenderingDrawUtil.renderNormalQuad(vb, pose, new Vector3(-0.5, -0.5, -0.5), uy.copy(), uz.copy(), color, packedLight, packedOverlay, uv);
        RenderingDrawUtil.renderNormalQuad(vb, pose, new Vector3(0.5, -0.5, -0.5), uz.copy(), uy.copy(), color, packedLight, packedOverlay, uv);
    }
}
