/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderablePage
 * Created by HellFirePvP
 * Date: 03.08.2019 / 18:53
 */
public interface RenderablePage {

    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY);

    default public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {}

    default public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return false;
    }

    default public boolean propagateMouseDrag(double mouseDX, double mouseDZ) {
        return false;
    }

    public static FontRenderer getFontRenderer() {
        return Minecraft.getInstance().fontRenderer;
    }

}
