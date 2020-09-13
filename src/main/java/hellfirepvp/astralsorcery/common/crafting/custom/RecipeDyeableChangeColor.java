/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.custom;

import hellfirepvp.astralsorcery.common.block.tile.BlockCelestialGateway;
import hellfirepvp.astralsorcery.common.item.wand.ItemIlluminationWand;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeDyeableChangeColor
 * Created by HellFirePvP
 * Date: 29.11.2019 / 13:24
 */
public class RecipeDyeableChangeColor extends SpecialRecipe {

    private final Supplier<IRecipeSerializer<?>> serializer;
    private final Item output;
    private final BiConsumer<ItemStack, DyeColor> colorFn;

    public RecipeDyeableChangeColor(ResourceLocation idIn, Supplier<IRecipeSerializer<?>> serializer, Item output, BiConsumer<ItemStack, DyeColor> colorFn) {
        super(idIn);
        this.serializer = serializer;
        this.output = output;
        this.colorFn = colorFn;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return tryFindValidRecipeAndDye(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        DyeColor color = tryFindValidRecipeAndDye(inv);
        if (color == null) {
            return ItemStack.EMPTY;
        }
        ItemStack wand = new ItemStack(this.output);
        ItemIlluminationWand.setConfiguredColor(wand, color);
        return wand;
    }

    @Nullable
    private DyeColor tryFindValidRecipeAndDye(CraftingInventory inv) {
        boolean foundWand = false;
        DyeColor dyeColorFound = null;
        int nonEmptyItemsFound = 0;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack in = inv.getStackInSlot(slot);
            if (!in.isEmpty()) {
                nonEmptyItemsFound++;

                if (in.getItem().equals(this.output)) {
                    foundWand = true;
                } else {
                    DyeColor color = DyeColor.getColor(in);
                    if (color != null) {
                        dyeColorFound = color;
                    }
                }
            }
        }

        if (!foundWand || dyeColorFound == null || nonEmptyItemsFound != 2) {
            return null;
        } else {
            return dyeColorFound;
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return this.serializer.get();
    }

    public static class IlluminationWandColorSerializer extends SpecialRecipeSerializer<RecipeDyeableChangeColor> {

        public IlluminationWandColorSerializer() {
            super(id -> new RecipeDyeableChangeColor(id, () -> RecipeSerializersAS.CUSTOM_CHANGE_WAND_COLOR_SERIALIZER,
                    ItemsAS.ILLUMINATION_WAND, ItemIlluminationWand::setConfiguredColor));
            this.setRegistryName(RecipeSerializersAS.CUSTOM_CHANGE_WAND_COLOR);
        }
    }

    public static class CelestialGatewayColorSerializer extends SpecialRecipeSerializer<RecipeDyeableChangeColor> {

        public CelestialGatewayColorSerializer() {
            super(id -> new RecipeDyeableChangeColor(id, () -> RecipeSerializersAS.CUSTOM_CHANGE_GATEWAY_COLOR_SERIALIZER,
                    BlocksAS.GATEWAY.asItem(), BlockCelestialGateway::setColor));
            this.setRegistryName(RecipeSerializersAS.CUSTOM_CHANGE_GATEWAY_COLOR);
        }
    }
}
