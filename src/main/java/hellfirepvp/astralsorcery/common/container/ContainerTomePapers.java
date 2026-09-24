/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.container.base.ContainerItemInSlot;
import hellfirepvp.astralsorcery.common.container.base.PlayerInventorySlots;
import hellfirepvp.astralsorcery.common.container.slot.ConstellationPaperSlot;
import hellfirepvp.astralsorcery.common.container.slot.ReadOnlySlot;
import hellfirepvp.astralsorcery.common.container.transfer.DefaultQuickMoveTransfer;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.MenuTypesAS;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContainerTomePapers
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ContainerTomePapers extends ContainerItemInSlot implements DefaultQuickMoveTransfer, PlayerInventorySlots {

    public ContainerTomePapers(@Nullable MenuType<?> menuType, Inventory playerInv, int slotId, int windowId) {
        super(menuType, playerInv, slotId, windowId);
        this.addDefaultPlayerSlots(8, 84, (index, x, y) -> {
            if (index == slotId) return new ReadOnlySlot(playerInv, index, x, y);
            return new Slot(playerInv, index, x, y);
        });
        this.addTomeSlots(playerInv);
    }

    protected void addTomeSlots(Inventory inv) {
        LogicalSide side = SidedHelper.getSide(inv.player);
        Deque<BaseConstellation> seenConstellations = new LinkedList<>(ResearchManager.getProgress(inv.player, side).getSeenConstellations());

        int offsetX = 8;
        int offsetY = 13;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                if (seenConstellations.isEmpty()) return;

                BaseConstellation cst = seenConstellations.pop();
                this.addSlot(new ConstellationPaperSlot(() -> cst, offsetX + column * 18, offsetY + (row * 18)));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        this.defaultQuickMoveStack(player, index);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canQuickTransferItemToSlot(ItemStack stack, Slot targetSlot, boolean onlyTransferToFilledSlots) {
        if (targetSlot instanceof ConstellationPaperSlot && stack.is(ItemsAS.CONSTELLATION_PAPER)) {
            return true;
        }
        return DefaultQuickMoveTransfer.super.canQuickTransferItemToSlot(stack, targetSlot, onlyTransferToFilledSlots);
    }

    @Override
    public Predicate<ItemStack> validContainerStack() {
        return stack -> stack.is(ItemsAS.TOME);
    }

    @Override
    public AbstractContainerMenu self() {
        return this;
    }

}
