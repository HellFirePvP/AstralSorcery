/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.base;

import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerTileEntity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ContainerTileEntity<T extends BlockEntity> extends AbstractContainerMenu {

    private final T tile;
    private final Inventory playerInv;

    protected ContainerTileEntity(@Nullable MenuType<?> menuType, T tile, Inventory playerInv, int containerId) {
        super(menuType, containerId);
        this.tile = tile;
        this.playerInv = playerInv;
    }

    public final T getTile() {
        return this.tile;
    }

    public final Inventory getPlayerInv() {
        return this.playerInv;
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos pos = this.getTile().getBlockPos();
        if (MiscUtil.getTileAt(this.getTile().getLevel(), pos, this.getTile().getClass(), false).isPresent()) {
            return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
        }
        return false;
    }
}
