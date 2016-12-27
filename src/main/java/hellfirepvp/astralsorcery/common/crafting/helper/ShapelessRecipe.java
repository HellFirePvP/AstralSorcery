package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapelessRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public class ShapelessRecipe extends AbstractCacheableRecipe {

    protected int contentCounter = 0;
    protected ItemHandle[] contents = new ItemHandle[9]; //Max. 9

    public ShapelessRecipe(Block block) {
        this(new ItemStack(block));
    }

    public ShapelessRecipe(Item item) {
        this(new ItemStack(item));
    }

    public ShapelessRecipe(ItemStack output) {
        super(output);
    }

    public ShapelessRecipe add(Block block) {
        return add(new ItemStack(block));
    }

    public ShapelessRecipe add(Item item) {
        return add(new ItemStack(item));
    }

    public ShapelessRecipe add(ItemStack stack) {
        if(contentCounter >= 9) return this; //Add nothing then.
        this.contents[contentCounter++] = new ItemHandle(stack);
        return this;
    }

    public ShapelessRecipe add(String oreDictName) {
        if(contentCounter >= 9) return this; //Add nothing then.
        this.contents[contentCounter++] = new ItemHandle(oreDictName);
        return this;
    }

    public ShapelessRecipe addPart(FluidStack fluidStack) {
        if(contentCounter >= 9) return this; //Add nothing then.
        this.contents[contentCounter++] = new ItemHandle(fluidStack);
        return this;
    }

    public ShapelessRecipe addPart(Fluid fluid, int mbAmount) {
        return addPart(new FluidStack(fluid, mbAmount));
    }

    public ShapelessRecipe addPart(Fluid fluid) {
        return addPart(fluid, 1000);
    }

    @Override
    public void register() {
        CraftingManager.getInstance().addRecipe(make());
    }

    @Override
    public AccessibleRecipeAdapater make() {
        Object[] parts = new Object[contentCounter];
        for (int i = 0; i < parts.length; i++) {
            Object obj = parts[i];
            if(obj instanceof ItemHandle) {
                parts[i] = contents[i].getObjectForRecipe();
            } else {
                parts[i] = contents[i];
            }
        }
        return new AccessibleRecipeAdapater(RecipeHelper.getShapelessOreDictRecipe(getOutput(), parts), this);
    }

    @Nullable
    @Override
    public ItemHandle getExpectedStack(int row, int column) {
        int index = row * 3 + column;
        return index >= contentCounter ? null : contents[index];
    }

    @Nullable
    @Override
    public ItemHandle getExpectedStack(ShapedRecipeSlot slot) {
        int index = slot.rowMultipler * 3 + slot.columnMultiplier;
        return index >= contentCounter ? null : contents[index];
    }
}
