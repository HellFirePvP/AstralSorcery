/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper.ingredient;

import hellfirepvp.astralsorcery.common.lib.IngredientSerializersAS;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidIngredient
 * Created by HellFirePvP
 * Date: 30.05.2019 / 17:27
 */
public class FluidIngredient extends Ingredient {

    private final List<FluidStack> fluids;
    private IntList itemIds = null;
    private ItemStack[] itemArray = null;
    private int cacheItemStacks = -1, cacheItemIds = -1;

    public FluidIngredient(List<FluidStack> fluidStacks) {
        super(Stream.empty());
        this.fluids = fluidStacks;
    }

    public FluidIngredient(FluidStack... fluidStacks) {
        super(Stream.empty());
        this.fluids = Arrays.asList(fluidStacks);
    }

    public List<FluidStack> getFluids() {
        return fluids;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        if (itemArray == null || this.cacheItemStacks != this.fluids.size()) {
            NonNullList<ItemStack> lst = NonNullList.create();

            for (FluidStack fluid : this.fluids) {
                lst.add(FluidUtil.getFilledBucket(fluid));
            }

            this.itemArray = lst.toArray(new ItemStack[lst.size()]);
            this.cacheItemStacks = this.fluids.size();
        }
        return this.itemArray;
    }

    @Override
    public IntList getValidItemStacksPacked() {
        if (this.itemIds == null || this.cacheItemIds != fluids.size()) {
            this.itemIds = new IntArrayList(this.fluids.size());

            for (FluidStack fluid : this.fluids) {
                ItemStack bucketFluid = FluidUtil.getFilledBucket(fluid);
                this.itemIds.add(RecipeItemHelper.pack(bucketFluid));
            }

            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
            this.cacheItemIds = this.fluids.size();
        }

        return this.itemIds;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) {
            return false;
        }

        FluidStack contained = FluidUtil.getFluidContained(input).orElse(null);
        if (contained == null || contained.getFluid() == null || contained.getAmount() <= 0) {
            return false;
        }

        for (FluidStack target : this.fluids) {
            if (contained.containsFluid(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasNoMatchingItems() {
        return this.fluids.isEmpty();
    }

    @Override
    protected void invalidate() {
        super.invalidate();

        this.itemIds = null;
        this.itemArray = null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return IngredientSerializersAS.FLUID_SERIALIZER;
    }
}
