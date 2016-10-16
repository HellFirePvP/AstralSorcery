package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarBase
 * Created by HellFirePvP
 * Date: 16.10.2016 / 19:26
 */
public abstract class ContainerAltarBase extends Container {

    public final InventoryPlayer playerInv;
    public final TileAltar tileAltar;

    public ContainerAltarBase(InventoryPlayer playerInv, TileAltar tileAltar) {
        this.playerInv = playerInv;
        this.tileAltar = tileAltar;

        bindPlayerInventory();
        bindAltarInventory();
    }

    abstract void bindPlayerInventory();

    abstract void bindAltarInventory();

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}
