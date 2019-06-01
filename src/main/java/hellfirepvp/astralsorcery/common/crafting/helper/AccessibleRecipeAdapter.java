/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AccessibleRecipeAdapter
 * Created by HellFirePvP
 * Date: 30.05.2019 / 18:04
 */
public class AccessibleRecipeAdapter extends AccessibleRecipe {

    private final IRecipe parent;
    private final AbstractRecipeAccessor abstractRecipe;

    public AccessibleRecipeAdapter(IRecipe parent, AbstractRecipeAccessor abstractRecipe) {
        super(parent.getId());
        this.parent = parent;
        this.abstractRecipe = abstractRecipe;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public NonNullList<ItemStack> getExpectedStackForRender(int row, int column) {
        ItemHandle handle = abstractRecipe.getExpectedStack(row, column);
        if(handle == null) return NonNullList.create();
        return refactorSubItems(handle.getApplicableItemsForRender());
    }

    @Nullable
    public ItemHandle getExpectedStackHandle(int row, int column) {
        return abstractRecipe.getExpectedStack(row, column);
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public NonNullList<ItemStack> getExpectedStackForRender(ShapedRecipeSlot slot) {
        ItemHandle handle = abstractRecipe.getExpectedStack(slot);
        if(handle == null) return NonNullList.create();
        return refactorSubItems(handle.getApplicableItemsForRender());
    }

    @Nullable
    public ItemHandle getExpectedStackHandle(ShapedRecipeSlot slot) {
        return abstractRecipe.getExpectedStack(slot);
    }

    @OnlyIn(Dist.CLIENT)
    private NonNullList<ItemStack> refactorSubItems(NonNullList<ItemStack> applicableItems) {
        NonNullList<ItemStack> out = NonNullList.create();
        out.addAll(applicableItems);
        return out;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return parent.getIngredients();
    }

    @Override
    public String getGroup() {
        return parent.getGroup();
    }

    @Override
    public boolean isDynamic() {
        return parent.isDynamic();
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return parent.matches(inv, worldIn);
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return parent.getCraftingResult(inv);
    }

    @Override
    public boolean canFit(int width, int height) {
        return parent.canFit(width, height);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return parent.getRecipeOutput();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(IInventory inv) {
        return parent.getRemainingItems(inv);
    }

    public IRecipe getParentRecipe() {
        return parent;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return parent.getSerializer();
    }
}
