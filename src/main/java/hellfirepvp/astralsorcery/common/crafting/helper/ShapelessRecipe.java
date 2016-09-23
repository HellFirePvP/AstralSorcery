package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapelessRecipes;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapelessRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public class ShapelessRecipe extends AbstractCacheableRecipe {

    private int contentCounter = 0;
    private ItemStack[] contents = new ItemStack[9]; //Max. 9

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
    public ShapelessRecipes make() {
        Object[] parts = new Object[contentCounter];
        System.arraycopy(contents, 0, parts, 0, contentCounter);
        return RecipeHelper.getShapessRecipe(getOutput(), parts);
    }
}
