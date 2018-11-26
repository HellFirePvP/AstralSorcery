/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.compound;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.Blending;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompoundObjectEffect
 * Created by HellFirePvP
 * Date: 16.02.2017 / 16:34
 */
public abstract class CompoundObjectEffect extends EntityComplexFX {

    @Override
    public final void render(float pTicks) {
        GlStateManager.pushMatrix();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        getGroup().beginDrawing(vb);
        render(vb, pTicks);
        tes.draw();
        GlStateManager.popMatrix();
    }

    public abstract ObjectGroup getGroup();

    public abstract void render(BufferBuilder vb, float pTicks);

    public enum ObjectGroup {

        SOLID_COLOR_SPHERE;

        public void beginDrawing(BufferBuilder vb) {
            switch (this) {
                case SOLID_COLOR_SPHERE:
                    vb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
                    break;
                default:
                    break;
            }
        }

        public void prepareGLContext() {
            switch (this) {
                case SOLID_COLOR_SPHERE:
                    GlStateManager.enableBlend();
                    Blending.DEFAULT.apply();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0001F);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    break;
                default:
                    break;
            }
        }

        public void revertGLContext() {
            switch (this) {
                case SOLID_COLOR_SPHERE:
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    GlStateManager.depthMask(true);
                    GlStateManager.disableBlend();
                    GlStateManager.enableTexture2D();
                    break;
                default:
                    break;
            }
        }

    }

}
