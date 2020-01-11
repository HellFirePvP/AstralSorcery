/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin;

import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationBaseMergeStatsRecipe
 * Created by HellFirePvP
 * Date: 28.09.2019 / 08:31
 */
public class ConstellationBaseMergeStatsRecipe extends ConstellationBaseItemRecipe {

    public ConstellationBaseMergeStatsRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, AltarRecipeGrid recipeGrid) {
        super(recipeId, altarType, duration, starlightRequirement, recipeGrid);
    }

    public static ConstellationBaseMergeStatsRecipe convertToThis(SimpleAltarRecipe other) {
        return new ConstellationBaseMergeStatsRecipe(other.getId(), other.getAltarType(), other.getDuration(), other.getStarlightRequirement(), other.getInputs());
    }

    @Override
    @Nonnull
    public ItemStack getOutputForRender(Iterable<ItemStack> inventoryContents) {
        ItemStack out = super.getOutputForRender(inventoryContents);
        setStats(out, inventoryContents);
        return out;
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs(TileAltar altar) {
        List<ItemStack> out = super.getOutputs(altar);
        out.forEach(stack -> setStats(stack, altar.getInventory()));
        return out;
    }

    private void setStats(ItemStack out, Iterable<ItemStack> inventoryContents) {
        if (!(out.getItem() instanceof CrystalAttributeItem)) {
            return;
        }

        CrystalAttributes.Builder builder = CrystalAttributes.Builder.newBuilder(true);
        for (ItemStack stack : inventoryContents) {
            if (stack.getItem() instanceof CrystalAttributeItem) {
                CrystalAttributes attr = ((CrystalAttributeItem) stack.getItem()).getAttributes(stack);
                if (attr != null) {
                    builder.addAll(attr);
                }
            }
        }

        CrystalAttributes attr = builder.build();
        if (!attr.isEmpty()) {
            ((CrystalAttributeItem) out.getItem()).setAttributes(out, attr);
        }
    }
}
