package hellfirepvp.astralsorcery.common.integration.crt.actions;

import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRecipeBase;
import com.blamejared.crafttweaker.impl_native.blocks.ExpandBlockState;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.util.block.BlockMatchInformation;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.Map;

public class ActionRemoveBlockTransmutation extends ActionRecipeBase {

    private final BlockState outputState;
    private final boolean exact;

    public ActionRemoveBlockTransmutation(IRecipeManager manager, BlockState outputState, boolean exact) {
        super(manager);
        this.outputState = outputState;
        this.exact = exact;
    }

    @Override
    public void apply() {
        Map<ResourceLocation, IRecipe<?>> recipes = this.getManager().getRecipes();
        Iterator<ResourceLocation> iterator = recipes.keySet().iterator();
        BlockMatchInformation matcher = new BlockMatchInformation(outputState, exact);
        while (iterator.hasNext()) {
            ResourceLocation recipeId = iterator.next();
            BlockTransmutation recipe = (BlockTransmutation) recipes.get(recipeId);
            if (matcher.test(recipe.getOutput())) {
                iterator.remove();
            }
        }
    }

    @Override
    public String describe() {
        return "Removing Block Transmutation recipes that output " + ExpandBlockState.getCommandString(outputState);
    }
}
