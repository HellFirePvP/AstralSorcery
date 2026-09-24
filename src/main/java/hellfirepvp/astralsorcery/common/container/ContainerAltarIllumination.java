/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerAltarIllumination
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ContainerAltarIllumination extends ContainerAltar {

    public ContainerAltarIllumination(@Nullable MenuType<?> menuType, TileAltar tile, Inventory playerInv, int containerId) {
        super(menuType, tile, playerInv, containerId);
        this.addDefaultPlayerSlots(playerInv, this.getPlayerInventoryOffset().x(), this.getPlayerInventoryOffset().y());
        this.addAltarGridSlots();
    }

    public ContainerAltarIllumination(@Nullable MenuType<?> menuType, TileAltar tile, Inventory playerInv, int containerId, RegistryFriendlyByteBuf byteBuf) {
        this(menuType, tile, playerInv, containerId);
    }

    @Override
    protected IntPoint getAltarGridOffset() {
        return new IntPoint(36, 37);
    }

    @Override
    protected IntPoint getPlayerInventoryOffset() {
        return new IntPoint(28, 133);
    }
}
