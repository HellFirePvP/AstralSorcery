/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderablePage
 * Created by HellFirePvP
 * Date: 03.08.2019 / 18:53
 */
public abstract class RenderablePage {

    @Nullable
    private final ResearchNode node;
    private final int nodePage;

    public RenderablePage(@Nullable ResearchNode node, int nodePage) {
        this.node = node;
        this.nodePage = nodePage;
    }

    public abstract void render(MatrixStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY);

    public void postRender(MatrixStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {}

    public boolean propagateMouseClick(double mouseX, double mouseZ) {
        return false;
    }

    public boolean propagateMouseDrag(double mouseDX, double mouseDZ) {
        return false;
    }

    public static FontRenderer getFontRenderer() {
        return Minecraft.getInstance().fontRenderer;
    }

    @Nullable
    protected final ResearchNode getResearchNode() {
        return this.node;
    }

    protected final int getNodePage() {
        return this.nodePage;
    }
}
