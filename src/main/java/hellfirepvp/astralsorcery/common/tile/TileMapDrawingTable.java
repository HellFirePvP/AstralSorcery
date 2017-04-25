package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileMapDrawingTable
 * Created by HellFirePvP
 * Date: 14.03.2017 / 22:18
 */
public class TileMapDrawingTable extends TileSkybound {

    private ItemStack slotIn = null;
    private ItemStack slotGlassLens = null;

    public TileMapDrawingTable() {}

    @Override
    protected void onFirstTick() {}

    public int addParchment(int amt) {
        if(slotIn != null && slotIn.getItem() != null) {
            if(slotIn.getItem() instanceof ItemCraftingComponent &&
                    slotIn.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()) {
                int current = slotIn.stackSize;
                if(current + amt <= 64) {
                    current += amt;
                    slotIn.stackSize = current;
                    markForUpdate();
                    return 0;
                } else {
                    int ret = (current + amt) - 64;
                    slotIn.stackSize = 64;
                    markForUpdate();
                    return ret;
                }
            }
            return amt;
        } else {
            slotIn = ItemCraftingComponent.MetaType.PARCHMENT.asStack();
            slotIn.stackSize = amt;
            markForUpdate();
            return 0;
        }
    }

    public void putGlassLens(ItemStack glassLens) {
        this.slotGlassLens = ItemUtils.copyStackWithSize(glassLens, Math.min(glassLens.stackSize, 1));
        markForUpdate();
    }

    public boolean hasGlassLens() {
        return slotGlassLens != null && slotGlassLens.getItem() != null; //Change to differ between engraved & normal lens..
    }

    public boolean hasParchment() {
        return slotIn != null && slotIn.getItem() != null && slotIn.getItem() instanceof ItemCraftingComponent &&
                slotIn.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta() &&
                slotIn.stackSize > 0;
    }

    public void dropContents() {
        if(slotIn != null && slotIn.getItem() != null) {
            ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, slotIn);
            slotIn = null;
        }
        if(slotGlassLens != null && slotGlassLens.getItem() != null) {
            ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, slotGlassLens);
            slotGlassLens = null;
        }
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if(compound.hasKey("slotIn")) {
            this.slotIn = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("slotIn"));
        } else {
            this.slotIn = null;
        }
        if(compound.hasKey("slotGlassLens")) {
            this.slotGlassLens = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("slotGlassLens"));
        } else {
            this.slotGlassLens = null;
        }
    }

    @Override
    public void writeNetNBT(NBTTagCompound compound) {
        super.writeNetNBT(compound);

        if(this.slotIn != null) {
            NBTTagCompound tag = new NBTTagCompound();
            this.slotIn.writeToNBT(tag);
            compound.setTag("slotIn", tag);
        }

        if(this.slotGlassLens != null) {
            NBTTagCompound tag = new NBTTagCompound();
            this.slotGlassLens.writeToNBT(tag);
            compound.setTag("slotGlassLens", tag);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(0.5, 0.5, 0.5);
    }

}
