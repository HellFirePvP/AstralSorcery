package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

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
    protected ItemStack[] contents = new ItemStack[9]; //Max. 9

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
        this.contents[contentCounter++] = stack;
        return this;
    }

    @Override
    public void register() {
        CraftingManager.getInstance().addRecipe(make());
    }

    @Override
    public AccessibleRecipeAdapater make() {
        Object[] parts = new Object[contentCounter];
        System.arraycopy(contents, 0, parts, 0, contentCounter);
        return new AccessibleRecipeAdapater(RecipeHelper.getShapelessRecipe(getOutput(), parts), this);
    }

    @Nullable
    @Override
    public ItemStack getExpectedStack(int row, int column) {
        int index = row * 3 + column;
        return index >= contentCounter ? null : contents[index];
    }

    @Nullable
    @Override
    public ItemStack getExpectedStack(ShapedRecipeSlot slot) {
        int index = slot.rowMultipler * 3 + slot.columnMultiplier;
        return index >= contentCounter ? null : contents[index];
    }
}
