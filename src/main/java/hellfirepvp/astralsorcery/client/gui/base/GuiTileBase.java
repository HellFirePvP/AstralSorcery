/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.base;

import hellfirepvp.astralsorcery.client.gui.GuiWHScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiTileBase
 * Created by HellFirePvP
 * Date: 02.03.2017 / 16:34
 */
public class GuiTileBase extends GuiWHScreen {

    private final TileEntity te;

    protected GuiTileBase(TileEntity te, int guiHeight, int guiWidth) {
        super(guiHeight, guiWidth);
        this.te = te;
    }

    public TileEntity getOwningTileEntity() {
        return te;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if(te.isInvalid()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

}
