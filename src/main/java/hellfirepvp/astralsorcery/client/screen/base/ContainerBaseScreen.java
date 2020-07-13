/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.common.container.ContainerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerBaseScreen
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:08
 */
public abstract class ContainerBaseScreen<T extends TileEntity, C extends ContainerTileEntity<T>> extends ContainerScreen<C> {

    public ContainerBaseScreen(C screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, pTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();

        TileEntity te = this.container.getTileEntity();
        if (te.isRemoved() || !this.container.canInteractWith(Minecraft.getInstance().player)) {
            this.onClose();
        }
    }
}
