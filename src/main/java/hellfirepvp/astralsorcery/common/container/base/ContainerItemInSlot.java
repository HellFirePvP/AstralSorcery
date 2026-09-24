/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerItemInSlot
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ContainerItemInSlot extends AbstractContainerMenu {

    private final Inventory playerInv;
    private final int slot;
    private final ItemStack stack;

    protected ContainerItemInSlot(@Nullable MenuType<?> menuType, Inventory playerInv, int slotId, int windowId) {
        super(menuType, windowId);
        this.playerInv = playerInv;
        this.slot = slotId;
        this.stack = playerInv.getItem(slotId);
    }

    public Inventory getPlayerInv() {
        return this.playerInv;
    }

    public Player getPlayer() {
        return this.getPlayerInv().player;
    }

    public abstract Predicate<ItemStack> validContainerStack();

    @Override
    public boolean stillValid(Player player) {
        if (this.slot == -1) return true;
        ItemStack stack = player.getInventory().getItem(this.slot);
        return !stack.isEmpty() && this.validContainerStack().test(stack);
    }
}
