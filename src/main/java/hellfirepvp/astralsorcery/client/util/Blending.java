/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Blending
 * Created by HellFirePvP
 * Date: 01.10.2016 / 14:06
 */
public enum Blending {

    DEFAULT(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA),
    ALPHA(GL11.GL_ONE, GL11.GL_SRC_ALPHA),
    PREALPHA(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA),
    MULTIPLY(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA),
    ADDITIVE(GL11.GL_ONE, GL11.GL_ONE),
    ADDITIVEDARK(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR),
    OVERLAYDARK(GL11.GL_SRC_COLOR, GL11.GL_ONE),
    ADDITIVE_ALPHA(GL11.GL_SRC_ALPHA, GL11.GL_ONE),
    CONSTANT_ALPHA(GL11.GL_ONE, GL11.GL_ONE_MINUS_CONSTANT_ALPHA),
    INVERTEDADD(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);

    public final int sfactor;
    public final int dfactor;

    private Blending(int s, int d) {
        sfactor = s;
        dfactor = d;
    }

    public void apply() {
        GL11.glBlendFunc(sfactor, dfactor);
    }

    public void applyStateManager() {
        GlStateManager.blendFunc(sfactor, dfactor);
    }

}
