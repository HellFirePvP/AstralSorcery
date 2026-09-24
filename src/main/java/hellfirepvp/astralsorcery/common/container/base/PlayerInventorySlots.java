/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.base;

import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.TriFunction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerInventorySlots
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface PlayerInventorySlots {

    default AbstractContainerMenu self() {
        return MiscUtil.cast(this);
    }

    default void addDefaultPlayerSlots(Inventory playerInv, int slotsOffsetX, int slotsOffsetY) {
        this.addDefaultPlayerSlots(slotsOffsetX, slotsOffsetY, (slotIndex, x, y) -> new Slot(playerInv, slotIndex, x, y));
    }

    default void addDefaultPlayerSlots(int slotsOffsetX, int slotsOffsetY, TriFunction<Integer, Integer, Integer, Slot> createSlot) {
        int hotbarOffset = slotsOffsetY + 58;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int index = column + row * 9 + 9;

                this.self().addSlot(createSlot.apply(index, slotsOffsetX + column * 18, slotsOffsetY + row * 18));
            }
        }
        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.self().addSlot(createSlot.apply(hotbarSlot, slotsOffsetX + hotbarSlot * 18, hotbarOffset));
        }
    }
}
