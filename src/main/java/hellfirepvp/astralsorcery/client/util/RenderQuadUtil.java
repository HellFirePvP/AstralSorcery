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
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;


/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderQuadUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderQuadUtil {

    public static DrawBuilder rect(VertexConsumer buf, PoseStack renderStack, float offsetX, float offsetY, float width, float height) {
        return new DrawBuilder(buf, renderStack, offsetX, offsetY, width, height);
    }

    public static class DrawBuilder {

        private final VertexConsumer buf;
        private final PoseStack renderStack;
        private final float offsetX, offsetY;
        private final float width, height;
        private float u = 0F, v = 0F, uWidth = 1F, vWidth = 1F;
        private ColorWrapper color = ColorWrapper.WHITE;

        private DrawBuilder(VertexConsumer buf, PoseStack renderStack, float offsetX, float offsetY, float width, float height) {
            this.buf = buf;
            this.renderStack = renderStack;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.width = width;
            this.height = height;
        }

        public DrawBuilder tex(TextureAtlasSprite tas) {
            return this.tex(tas.getU0(), tas.getV0(), tas.getU1() - tas.getU0(), tas.getV1() - tas.getV0());
        }

        public DrawBuilder tex(AbstractRenderTexture texture) {
            return this.tex(texture.getUV());
        }

        public DrawBuilder tex(SpriteSheet sprite, long tick) {
            return this.tex(sprite.getUV(tick));
        }

        public DrawBuilder tex(UVFrame uv) {
            return this.tex(uv.u(), uv.v(), uv.uWidth(), uv.vHeight());
        }

        public DrawBuilder tex(float u, float v, float uWidth, float vWidth) {
            this.u = u;
            this.v = v;
            this.uWidth = uWidth;
            this.vWidth = vWidth;
            return this;
        }

        public DrawBuilder color(ColorWrapper color) {
            this.color = color;
            return this;
        }

        public DrawBuilder color(int color) {
            return this.color(ColorWrapper.transparent(color));
        }

        public DrawBuilder color(int r, int g, int b, int a) {
            return this.color(ColorWrapper.of(r, g, b, a));
        }

        public DrawBuilder color(float r, float g, float b, float a) {
            return this.color(ColorWrapper.of(r, g, b, a));
        }

        public DrawBuilder draw() {
            int r = this.color.getRed();
            int g = this.color.getGreen();
            int b = this.color.getBlue();
            int a = this.color.getAlpha();
            Matrix4f offset = this.renderStack.last().pose();
            buf.addVertex(offset, offsetX,         offsetY + height, 0).setColor(r, g, b, a).setUv(u, v + vWidth);
            buf.addVertex(offset, offsetX + width, offsetY + height, 0).setColor(r, g, b, a).setUv(u + uWidth, v + vWidth);
            buf.addVertex(offset, offsetX + width, offsetY,          0).setColor(r, g, b, a).setUv(u + uWidth, v);
            buf.addVertex(offset, offsetX,         offsetY,          0).setColor(r, g, b, a).setUv(u, v);
            return this;
        }
    }

}
