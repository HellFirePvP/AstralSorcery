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
 * Class: TileConstellationDiscoveryScreen
 * Created by HellFirePvP
 * Date: 15.02.2020 / 08:47
 */
public abstract class TileConstellationDiscoveryScreen<T extends TileEntity & NamedInventoryTile, D extends ConstellationDiscoveryScreen.DrawArea> extends ConstellationDiscoveryScreen<D> {

    private final T tile;

    protected TileConstellationDiscoveryScreen(T tile, int guiHeight, int guiWidth) {
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
