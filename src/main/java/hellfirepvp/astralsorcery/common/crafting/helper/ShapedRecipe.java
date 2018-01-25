/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public class ShapedRecipe extends AbstractRecipeAccessor {

    private final ShapeMap rawShapeMap;

    private ShapedRecipe(@Nonnull ItemStack output, ShapeMap rawShapeMap) {
        super(output);
        this.rawShapeMap = rawShapeMap;
    }

    @Nullable
    @Override
    public ItemHandle getExpectedStack(int row, int column) {
        ShapedRecipeSlot slot = ShapedRecipeSlot.getByRowColumnIndex(row, column);
        return slot == null ? null : rawShapeMap.get(slot);
    }

    @Nullable
    @Override
    public ItemHandle getExpectedStack(ShapedRecipeSlot slot) {
        return rawShapeMap.get(slot);
    }

    public static class Builder {

        private boolean registered = false;

        private final ResourceLocation entry;
        private final ItemStack output;
        protected ShapeMap crafingShape = new ShapeMap();

        private Builder(String name, ItemStack output) {
            this.entry = new ResourceLocation(AstralSorcery.MODID, "shaped/" + name);
            this.output = ItemUtils.copyStackWithSize(output, output.getCount());
        }

        public static Builder newShapedRecipe(String name, Block output) {
            return newShapedRecipe(name, new ItemStack(output));
        }

        public static Builder newShapedRecipe(String name, Item output) {
            return newShapedRecipe(name, new ItemStack(output));
        }

        public static Builder newShapedRecipe(String name, ItemStack output) {
            return new Builder(name, output);
        }

        public Builder addPart(Block block, ShapedRecipeSlot... slots) {
            return addPart(new ItemStack(block), slots);
        }

        public Builder addPart(Item stack, ShapedRecipeSlot... slots) {
            return addPart(new ItemStack(stack), slots);
        }

        public Builder addPart(ItemStack stack, ShapedRecipeSlot... slots) {
            ItemHandle handle = new ItemHandle(stack);
            for(ShapedRecipeSlot slot : slots) {
                crafingShape.put(slot, handle);
            }
            return this;
        }

        public Builder addPart(FluidStack fluidStack, ShapedRecipeSlot... slots) {
            ItemHandle handle = new ItemHandle(fluidStack);
            for(ShapedRecipeSlot slot : slots) {
                crafingShape.put(slot, handle);
            }
            return this;
        }

        public Builder addPart(Fluid fluid, int mbAmount, ShapedRecipeSlot... slots) {
            return addPart(new FluidStack(fluid, mbAmount), slots);
        }

        public Builder addPart(Fluid fluid, ShapedRecipeSlot... slots) {
            return addPart(fluid, 1000, slots);
        }

        public Builder addPart(String oreDictName, ShapedRecipeSlot... slots) {
            ItemHandle handle = new ItemHandle(oreDictName);
            for(ShapedRecipeSlot slot : slots) {
                crafingShape.put(slot, handle);
            }
            return this;
        }

        public Builder addPart(ItemHandle handle, ShapedRecipeSlot... slots) {
            for(ShapedRecipeSlot slot : slots) {
                crafingShape.put(slot, handle);
            }
            return this;
        }

        public Builder forceEmptySpaces() {
            crafingShape.setCut(false);
            return this;
        }

        public AccessibleRecipeAdapater unregisteredAccessibleShapedRecipe() {
            if(registered) throw new IllegalArgumentException("Tried to register previously built recipe twice!");
            registered = true; //Cache it please instead.
            BasePlainRecipe actual = RecipeHelper.getShapedOredictRecipe(entry, output, crafingShape.bake());
            ShapedRecipe access = new ShapedRecipe(output, crafingShape);
            return new AccessibleRecipeAdapater(actual, access);
        }

        public AccessibleRecipeAdapater buildAndRegisterLightCraftingRecipe() {
            if(registered) throw new IllegalArgumentException("Tried to register previously built recipe twice!");
            registered = true;
            BasePlainRecipe actual = new ShapedLightProximityRecipe(entry, output, crafingShape.bake());
            CommonProxy.registryPrimer.register(actual);
            ShapedRecipe access = new ShapedRecipe(output, crafingShape);
            return new AccessibleRecipeAdapater(actual, access);
        }

        public AccessibleRecipeAdapater buildAndRegisterShapedRecipe() {
            if(registered) throw new IllegalArgumentException("Tried to register previously built recipe twice!");
            registered = true;
            BasePlainRecipe actual = RecipeHelper.getShapedOredictRecipe(entry, output, crafingShape.bake());
            CommonProxy.registryPrimer.register(actual);
            ShapedRecipe access = new ShapedRecipe(output, crafingShape);
            return new AccessibleRecipeAdapater(actual, access);
        }

    }

}
