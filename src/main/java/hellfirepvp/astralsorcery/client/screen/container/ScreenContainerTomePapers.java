/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.screen.base.ScreenContainerMenu;
import hellfirepvp.astralsorcery.common.container.ContainerTomePapers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenContainerTomePapers
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenContainerTomePapers extends ScreenContainerMenu<ContainerTomePapers> {

    public ScreenContainerTomePapers(ContainerTomePapers menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
    }

    @Override
    public AbstractRenderTexture getBackgroundTexture() {
        return TexturesAS.SCREEN_CONTAINER_TOME_PAPERS;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
}
