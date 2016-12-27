package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AccessibleRecipeAdapater
 * Created by HellFirePvP
 * Date: 06.10.2016 / 14:26
 */
public class AccessibleRecipeAdapater implements IAccessibleRecipe {

    private final IRecipe parent;
    private final AbstractCacheableRecipe abstractRecipe;

    public AccessibleRecipeAdapater(IRecipe parent, AbstractCacheableRecipe abstractRecipe) {
        this.parent = parent;
        this.abstractRecipe = abstractRecipe;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public List<ItemStack> getExpectedStack(int row, int column) {
        ItemHandle handle = abstractRecipe.getExpectedStack(row, column);
        if(handle == null) return null;
        return refactorSubItems(handle.getApplicableItems());
    }

    @Nullable
    public ItemHandle getExpectedStackHandle(int row, int column) {
        return abstractRecipe.getExpectedStack(row, column);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public List<ItemStack> getExpectedStack(ShapedRecipeSlot slot) {
        ItemHandle handle = abstractRecipe.getExpectedStack(slot);
        if(handle == null) return null;
        return refactorSubItems(handle.getApplicableItems());
    }

    @Nullable
    public ItemHandle getExpectedStackHandle(ShapedRecipeSlot slot) {
        return abstractRecipe.getExpectedStack(slot);
    }

    @SideOnly(Side.CLIENT)
    private List<ItemStack> refactorSubItems(List<ItemStack> applicableItems) {
        List<ItemStack> out = new LinkedList<>();
        for (ItemStack oreDictIn : applicableItems) {
            if(oreDictIn.getItemDamage() == OreDictionary.WILDCARD_VALUE && !oreDictIn.isItemStackDamageable()) {
                oreDictIn.getItem().getSubItems(oreDictIn.getItem(), CreativeTabs.BUILDING_BLOCKS, out);
            } else {
                out.add(oreDictIn);
            }
        }
        return out;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return parent.matches(inv, worldIn);
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return parent.getCraftingResult(inv);
    }

    @Override
    public int getRecipeSize() {
        return parent.getRecipeSize();
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return parent.getRecipeOutput();
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return parent.getRemainingItems(inv);
    }
}
