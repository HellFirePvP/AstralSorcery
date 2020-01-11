/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
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
 * Class: ContainerAltarDiscovery
 * Created by HellFirePvP
 * Date: 15.08.2019 / 15:59
 */
public class ContainerAltarDiscovery extends ContainerAltarBase {

    public ContainerAltarDiscovery(TileAltar altar, PlayerInventory inv, int windowId) {
        super(altar, ContainerTypesAS.ALTAR_DISCOVERY, inv, windowId);
    }

    @Override
    void bindPlayerInventory(PlayerInventory plInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(plInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(plInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    void bindAltarInventory(TileInventory altarInventory) {
        for (int xx = 0; xx < 3; xx++) {
            addSlot(new SlotItemHandler(altarInventory,  6 + xx, 62 + xx * 18, 11));
        }
        for (int xx = 0; xx < 3; xx++) {
            addSlot(new SlotItemHandler(altarInventory, 11 + xx, 62 + xx * 18, 29));
        }
        for (int xx = 0; xx < 3; xx++) {
            addSlot(new SlotItemHandler(altarInventory, 16 + xx, 62 + xx * 18, 47));
        }
    }

    @Override
    Optional<ItemStack> handleCustomTransfer(PlayerEntity player, int index) {
        return Optional.empty();
    }
}
