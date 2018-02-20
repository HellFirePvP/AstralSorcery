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
        GL11.glPushMatrix();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        getGroup().beginDrawing(vb);
        render(vb, pTicks);
        tes.draw();
        GL11.glPopMatrix();
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
            }
        }

        public void prepareGLContext() {
            switch (this) {
                case SOLID_COLOR_SPHERE:
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glEnable(GL11.GL_BLEND);
                    Blending.DEFAULT.apply();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0001F);
                    GL11.glDisable(GL11.GL_CULL_FACE);

                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    break;
            }
        }

        public void revertGLContext() {
            switch (this) {
                case SOLID_COLOR_SPHERE:
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glPopAttrib();
                    break;
            }
        }

    }

}
