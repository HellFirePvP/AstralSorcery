package hellfirepvp.astralsorcery.common.integration.crt.actions;

import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRecipeBase;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultSpawnEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;
import java.util.Map;

public class ActionRemoveLiquidInteractionEntity extends ActionRecipeBase {
    private final MCEntityType entityType;

    public ActionRemoveLiquidInteractionEntity(IRecipeManager manager, MCEntityType entityType) {
        super(manager);
        this.entityType = entityType;
    }

    @Override
    public void apply() {
        Map<ResourceLocation, IRecipe<?>> recipes = this.getManager().getRecipes();
        Iterator<ResourceLocation> iterator = recipes.keySet().iterator();
        while (iterator.hasNext()) {
            ResourceLocation recipeId = iterator.next();
            LiquidInteraction recipe = (LiquidInteraction) recipes.get(recipeId);
            InteractionResult result = recipe.getResult();
            if (result instanceof ResultSpawnEntity) {
                ResultSpawnEntity resultSpawnEntity = (ResultSpawnEntity) result;
                if (entityType.getInternal() == resultSpawnEntity.getEntityType()) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public String describe() {
        return "Removing \"" + Registry.RECIPE_TYPE.getKey(this.getManager().getRecipeType()) + "\" recipes with entity output: " + entityType + "\"";
    }
}
