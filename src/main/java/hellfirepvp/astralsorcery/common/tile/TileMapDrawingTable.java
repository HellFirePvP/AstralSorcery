/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

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
 * Date: 18.03.2017 / 20:02
 */
public class TileMapDrawingTable extends TileSkybound {

    private ItemStack slotIn = ItemStack.EMPTY;
    private ItemStack slotGlassLens = ItemStack.EMPTY;

    public TileMapDrawingTable() {}

    @Override
    protected void onFirstTick() {}

    public int addParchment(int amt) {
        if(!slotIn.isEmpty()) {
            if(slotIn.getItem() instanceof ItemCraftingComponent &&
                    slotIn.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()) {
                int current = slotIn.getCount();
                if(current + amt <= 64) {
                    current += amt;
                    slotIn.setCount(current);
                    markForUpdate();
                    return 0;
                } else {
                    int ret = (current + amt) - 64;
                    slotIn.setCount(64);
                    markForUpdate();
                    return ret;
                }
            }
            return amt;
        } else {
            slotIn = ItemCraftingComponent.MetaType.PARCHMENT.asStack();
            slotIn.setCount(amt);
            markForUpdate();
            return 0;
        }
    }

    public void putGlassLens(ItemStack glassLens) {
        this.slotGlassLens = ItemUtils.copyStackWithSize(glassLens, Math.min(glassLens.getCount(), 1));
        markForUpdate();
    }

    public boolean hasGlassLens() {
        return !slotGlassLens.isEmpty(); //Change to differ between engraved & normal lens..
    }

    public boolean hasParchment() {
        return !slotIn.isEmpty() && slotIn.getItem() instanceof ItemCraftingComponent &&
                slotIn.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta() &&
                slotIn.getCount() > 0;
    }

    public void dropContents() {
        if(!slotIn.isEmpty()) {
            ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, slotIn);
            slotIn = ItemStack.EMPTY;
        }
        if(!slotGlassLens.isEmpty()) {
            ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, slotGlassLens);
            slotGlassLens = ItemStack.EMPTY;
        }
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.slotIn = new ItemStack(compound.getCompoundTag("slotIn"));
        this.slotGlassLens = new ItemStack(compound.getCompoundTag("slotGlassLens"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeNetNBT(compound);

        NBTTagCompound tag = new NBTTagCompound();
        this.slotIn.writeToNBT(tag);
        compound.setTag("slotIn", tag);

        tag = new NBTTagCompound();
        this.slotGlassLens.writeToNBT(tag);
        compound.setTag("slotGlassLens", tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(0.5, 0.5, 0.5);
    }
}
