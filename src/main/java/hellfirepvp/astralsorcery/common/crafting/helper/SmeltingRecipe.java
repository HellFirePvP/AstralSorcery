/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SmeltingRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:20
 */
public class SmeltingRecipe extends AbstractRecipeData {

    private final ItemStack input;
    private final float exp;

    private SmeltingRecipe(ItemStack input, ItemStack output, float exp) {
        super(output);
        this.input = input;
        this.exp = exp;

        FurnaceRecipes.instance().addSmeltingRecipe(this.input, this.getOutput(), this.exp);
    }

    public ItemStack getInput() {
        return input;
    }

    public float getExp() {
        return exp;
    }

    public static class Builder {

        private boolean registered = false;

        private final ResourceLocation entry;
        private final ItemStack output;

        private ItemStack input = ItemStack.EMPTY;
        private float exp = 1F;

        private Builder(String name, @Nonnull ItemStack output) {
            this.entry = new ResourceLocation(AstralSorcery.MODID, "smelting/" + name);
            this.output = ItemUtils.copyStackWithSize(output, output.getCount());
        }

        public static Builder newSmelting(String name, Block output) {
            return newSmelting(name, new ItemStack(output));
        }

        public static Builder newSmelting(String name, Item output) {
            return newSmelting(name, new ItemStack(output));
        }

        public static Builder newSmelting(String name, ItemStack output) {
            return new Builder(name, output);
        }

        public Builder setInput(Block block) {
            return setInput(new ItemStack(block));
        }

        public Builder setInput(Item item) {
            return setInput(new ItemStack(item));
        }

        public Builder setInput(ItemStack stack) {
            this.input = stack;
            return this;
        }

        public Builder setExp(float exp) {
            this.exp = exp;
            return this;
        }

        public SmeltingRecipe buildAndRegister() {
            if(registered) {
                throw new IllegalArgumentException("Tried to register previously built recipe twice!");
            }
            registered = true;
            return new SmeltingRecipe(this.input, this.output, this.exp);
        }

    }

}
