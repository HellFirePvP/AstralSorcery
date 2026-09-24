/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container.altar;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.common.container.ContainerAltarResonance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenContainerAltarResonance
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenContainerAltarResonance extends ScreenContainerAltar<ContainerAltarResonance> {

    public ScreenContainerAltarResonance(ContainerAltarResonance menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 0, 0);
    }

    @Override
    protected int getScreenWidth() {
        return this.getMenu().isExpanded() ? 256 : 216;
    }

    @Override
    protected int getScreenHeight() {
        return this.getMenu().isExpanded() ? 255 : 215;
    }

    @Override
    public AbstractRenderTexture getBackgroundTexture() {
        return this.getMenu().isExpanded() ? TexturesAS.SCREEN_CONTAINER_ALTAR_RESONANCE_EXPANDED : TexturesAS.SCREEN_CONTAINER_ALTAR_RESONANCE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.getMenu().isExpanded()) {
            this.renderAltarOutput(guiGraphics, 193, 75, partialTick);
        } else {
            this.renderAltarOutput(guiGraphics, 154, 55, partialTick);
        }
    }
}
