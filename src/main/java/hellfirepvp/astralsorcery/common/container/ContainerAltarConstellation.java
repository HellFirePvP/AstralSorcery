/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarConstellation
 * Created by HellFirePvP
 * Date: 15.08.2019 / 16:05
 */
public class ContainerAltarConstellation extends ContainerAltarBase {

    public ContainerAltarConstellation(TileAltar altar, PlayerInventory inv, int windowId) {
        super(altar, ContainerTypesAS.ALTAR_CONSTELLATION, inv, windowId);
    }

    @Override
    void bindPlayerInventory(PlayerInventory plInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(plInventory, j + i * 9 + 9, 48 + j * 18, 120 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(plInventory, i, 48 + i * 18, 178));
        }
    }

    @Override
    void bindAltarInventory(TileInventory altarInventory) {
        addSlot(new SlotItemHandler(altarInventory,  0, 84,  11));
        addSlot(new SlotItemHandler(altarInventory,  1, 102,  11));
        addSlot(new SlotItemHandler(altarInventory,  3, 138,  11));
        addSlot(new SlotItemHandler(altarInventory,  4, 156, 11));
        addSlot(new SlotItemHandler(altarInventory,  5,  84,  29));
        for (int xx = 0; xx < 3; xx++) {
            addSlot(new SlotItemHandler(altarInventory,  6 + xx, 102 + xx * 18, 29));
        }
        addSlot(new SlotItemHandler(altarInventory,  9, 156,  29));
        for (int xx = 0; xx < 3; xx++) {
            addSlot(new SlotItemHandler(altarInventory, 11 + xx, 102 + xx * 18, 47));
        }
        addSlot(new SlotItemHandler(altarInventory, 15, 84,   65));
        for (int xx = 0; xx < 3; xx++) {
            addSlot(new SlotItemHandler(altarInventory, 16 + xx, 102 + xx * 18, 65));
        }
        addSlot(new SlotItemHandler(altarInventory, 19, 156,  65));
        addSlot(new SlotItemHandler(altarInventory, 20, 84,  83));
        addSlot(new SlotItemHandler(altarInventory, 21, 102,  83));
        addSlot(new SlotItemHandler(altarInventory, 23, 138,  83));
        addSlot(new SlotItemHandler(altarInventory, 24, 156, 83));
    }

    @Override
    Optional<ItemStack> handleCustomTransfer(PlayerEntity player, int index) {
        return Optional.empty();
    }

    @Override
    public int translateIndex(int fromIndex) {
        if (fromIndex >= 23) {
            return fromIndex - 4;
        }
        if (fromIndex >= 15) {
            return fromIndex - 3;
        }
        if (fromIndex >= 11) {
            return fromIndex - 2;
        }
        if (fromIndex >= 3) {
            return fromIndex - 1;
        }
        return fromIndex;
    }
}
