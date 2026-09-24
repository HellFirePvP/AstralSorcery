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
import hellfirepvp.astralsorcery.common.container.ContainerAltarRadiance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenContainerAltarRadiance
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenContainerAltarRadiance extends ScreenContainerAltar<ContainerAltarRadiance> {

    public ScreenContainerAltarRadiance(ContainerAltarRadiance menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 256, 255);
    }

    @Override
    public AbstractRenderTexture getBackgroundTexture() {
        return TexturesAS.SCREEN_CONTAINER_ALTAR_RADIANCE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderAltarOutput(guiGraphics, 193, 23, partialTick);
        this.renderFocusStarfield(guiGraphics, 170, 86, partialTick);
    }
}
