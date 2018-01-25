/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipe
 * Created by HellFirePvP
 * Date: 19.11.2017 / 10:22
 */
public class GrindstoneRecipe {

    protected static final Random rand = new Random();

    protected final ItemHandle input;
    protected final ItemStack output;
    protected final int chance;

    public GrindstoneRecipe(ItemStack input, ItemStack output, int chance) {
        this.input = new ItemHandle(input);
        this.output = output;
        this.chance = chance;
    }

    public GrindstoneRecipe(ItemHandle input, ItemStack output, int chance) {
        this.input = input;
        this.output = output;
        this.chance = chance;
    }

    public boolean matches(ItemStack stackIn) {
        return this.input.matchCrafting(stackIn);
    }

    public boolean isValid() {
        return this.input.getApplicableItems().size() > 0 && !this.output.isEmpty();
    }

    @Nonnull
    public GrindResult grind(ItemStack stackIn) {
        if(rand.nextInt(chance) == 0) {
            return GrindResult.itemChange(ItemUtils.copyStackWithSize(this.output, this.output.getCount()));
        }
        return GrindResult.failNoOp();
    }

    @Nonnull
    public ItemStack getOutputForMatching() {
        return this.output;
    }

    @Nonnull
    public ItemHandle getOutputForRender() {
        return new ItemHandle(this.output);
    }

    @Nonnull
    public ItemHandle getInputForRender() {
        return this.input;
    }

    public static class GrindResult {

        private final ResultType type;
        private final ItemStack stack;

        private GrindResult(ResultType type, ItemStack stack) {
            this.type = type;
            this.stack = stack;
        }

        public ResultType getType() {
            return type;
        }

        @Nonnull
        public ItemStack getStack() {
            return stack;
        }

        public static GrindResult success() {
            return new GrindResult(ResultType.SUCCESS, ItemStack.EMPTY);
        }

        public static GrindResult itemChange(@Nonnull ItemStack newStack) {
            return new GrindResult(ResultType.ITEMCHANGE, newStack);
        }

        public static GrindResult failNoOp() {
            return new GrindResult(ResultType.FAIL_SILENT, ItemStack.EMPTY);
        }

        public static GrindResult failBreakItem() {
            return new GrindResult(ResultType.FAIL_BREAK_ITEM, ItemStack.EMPTY);
        }

    }

    public static enum ResultType {

        SUCCESS, //Successfully grinded something
        ITEMCHANGE, //Successfully grinded something, other item now on the grindstone
        FAIL_SILENT, //Did nothing, but nothing went wrong. just.. uuuh.. nothing.
        FAIL_BREAK_ITEM //The item broke while grinding.

    }
}
