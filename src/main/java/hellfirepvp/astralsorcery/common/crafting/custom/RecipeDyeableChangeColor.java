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
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
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
    private final Item targetItem;
    private final BiConsumer<ItemStack, DyeColor> colorFn;

    public RecipeDyeableChangeColor(ResourceLocation idIn, Supplier<IRecipeSerializer<?>> serializer, Item targetItem, BiConsumer<ItemStack, DyeColor> colorFn) {
        super(idIn);
        this.serializer = serializer;
        this.targetItem = targetItem;
        this.colorFn = colorFn;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return tryFindValidRecipeAndDye(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        Tuple<DyeColor, ItemStack> itemColorTpl = tryFindValidRecipeAndDye(inv);
        if (itemColorTpl == null) {
            return ItemStack.EMPTY;
        }
        ItemStack out = ItemUtils.copyStackWithSize(itemColorTpl.getB(), 1);
        this.colorFn.accept(out, itemColorTpl.getA());
        return out;
    }

    @Nullable
    private Tuple<DyeColor, ItemStack> tryFindValidRecipeAndDye(CraftingInventory inv) {
        ItemStack itemFound = ItemStack.EMPTY;
        DyeColor dyeColorFound = null;
        int nonEmptyItemsFound = 0;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack in = inv.getStackInSlot(slot);
            if (!in.isEmpty()) {
                nonEmptyItemsFound++;

                if (in.getItem().equals(this.targetItem)) {
                    itemFound = in;
                } else {
                    DyeColor color = DyeColor.getColor(in);
                    if (color != null) {
                        dyeColorFound = color;
                    }
                }
            }
        }

        if (itemFound.isEmpty() || dyeColorFound == null || nonEmptyItemsFound != 2) {
            return null;
        } else {
            return new Tuple<>(dyeColorFound, itemFound);
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
                    Item.getItemFromBlock(BlocksAS.GATEWAY), BlockCelestialGateway::setColor));
            this.setRegistryName(RecipeSerializersAS.CUSTOM_CHANGE_GATEWAY_COLOR);
        }
    }
}
