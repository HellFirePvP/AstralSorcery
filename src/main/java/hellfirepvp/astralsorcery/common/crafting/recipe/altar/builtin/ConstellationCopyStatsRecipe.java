package hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.ConstellationBaseItem;
import hellfirepvp.astralsorcery.common.constellation.ConstellationItem;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationCopyStatsRecipe
 * Created by HellFirePvP
 * Date: 06.08.2020 / 18:16
 */
public class ConstellationCopyStatsRecipe extends ConstellationBaseAverageStatsRecipe {

    private static final String KEY_CONSTELLATION_SLOT = "constellationSlot";

    private int constellationSlot = -1;

    public ConstellationCopyStatsRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, AltarRecipeGrid recipeGrid) {
        super(recipeId, altarType, duration, starlightRequirement, recipeGrid);
    }

    public static ConstellationCopyStatsRecipe convertToThis(SimpleAltarRecipe other) {
        return new ConstellationCopyStatsRecipe(other.getId(), other.getAltarType(), other.getDuration(), other.getStarlightRequirement(), other.getInputs());
    }

    public void setConstellationSlot(int constellationSlot) {
        this.constellationSlot = constellationSlot;
    }

    public int getConstellationSlot() {
        return constellationSlot;
    }

    @Override
    public void deserializeAdditionalJson(JsonObject recipeObject) throws JsonSyntaxException {
        super.deserializeAdditionalJson(recipeObject);

        if (JSONUtils.hasField(recipeObject, KEY_CONSTELLATION_SLOT)) {
            this.constellationSlot = JSONUtils.getInt(recipeObject, KEY_CONSTELLATION_SLOT);
        }
    }

    @Override
    public void serializeAdditionalJson(JsonObject recipeObject) {
        super.serializeAdditionalJson(recipeObject);

        if (this.constellationSlot != -1) {
            recipeObject.addProperty(KEY_CONSTELLATION_SLOT, this.constellationSlot);
        }
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender(Iterable<ItemStack> inventoryContents) {
        ItemStack out = super.getOutputForRender(inventoryContents);
        copyConstellation(out, inventoryContents);
        return out;
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs(TileAltar altar) {
        List<ItemStack> out = super.getOutputs(altar);
        out.forEach(stack -> copyConstellation(stack, altar.getInventory()));
        return out;
    }

    private void copyConstellation(ItemStack out, Iterable<ItemStack> inventoryContents) {
        if (out.getItem() instanceof ConstellationItem) {
            ConstellationItem iOut = (ConstellationItem) out.getItem();
            if (iOut.getAttunedConstellation(out) == null || iOut.getTraitConstellation(out) == null) {
                //Make a prioritizing iterable with the given index, if possible
                if (this.constellationSlot >= 0) {
                    inventoryContents = Iterables.concat(Lists.newArrayList(Iterables.get(inventoryContents, this.constellationSlot, ItemStack.EMPTY)), inventoryContents);
                }
                for (ItemStack stack : inventoryContents) {
                    if (stack.getItem() instanceof ConstellationItem) {
                        if (iOut.getAttunedConstellation(out) == null) {
                            IWeakConstellation c = ((ConstellationItem) stack.getItem()).getAttunedConstellation(stack);
                            if (c != null) {
                                iOut.setAttunedConstellation(out, c);
                            }
                        }
                        if (iOut.getTraitConstellation(out) == null) {
                            IMinorConstellation c = ((ConstellationItem) stack.getItem()).getTraitConstellation(stack);
                            if (c != null) {
                                iOut.setTraitConstellation(out, c);
                            }
                        }
                    }
                }
            }
        }
    }
}
