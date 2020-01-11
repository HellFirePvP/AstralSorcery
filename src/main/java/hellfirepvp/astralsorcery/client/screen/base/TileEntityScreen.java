/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.common.util.tile.NamedInventoryTile;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityScreen
 * Created by HellFirePvP
 * Date: 02.08.2019 / 20:35
 */
public class TileEntityScreen<T extends TileEntity & NamedInventoryTile> extends WidthHeightScreen {

    private final T tile;

    protected TileEntityScreen(T tile, int guiHeight, int guiWidth) {
        super(tile.getDisplayName(), guiHeight, guiWidth);
        this.tile = tile;
    }

    public T getTile() {
        return tile;
    }

    @Override
    public void tick() {
        super.tick();

        if (tile.isRemoved() || tile.getWorld().getDimension().getType() != Minecraft.getInstance().world.getDimension().getType()) {
            this.onClose();
        }
    }
}
