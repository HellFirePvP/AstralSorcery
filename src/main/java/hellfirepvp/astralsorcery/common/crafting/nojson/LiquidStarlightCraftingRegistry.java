package hellfirepvp.astralsorcery.common.crafting.nojson;

import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.FormCelestialCrystalClusterRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.FormGemCrystalClusterRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.InfusedWoodRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidStarlightRecipe;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidStarlightCraftingRegistry
 * Created by HellFirePvP
 * Date: 30.09.2019 / 20:27
 */
public class LiquidStarlightCraftingRegistry {

    private static Set<LiquidStarlightRecipe> recipes = new HashSet<>();

    public static void init() {
        recipes.add(new InfusedWoodRecipe());
        recipes.add(new FormCelestialCrystalClusterRecipe());
        recipes.add(new FormGemCrystalClusterRecipe());
    }

    @Nullable
    public static LiquidStarlightRecipe getRecipeFor(ItemEntity itemEntity, World world, BlockPos at) {
        return recipes.stream()
                .filter(recipe -> recipe.doesStartRecipe(itemEntity.getItem()))
                .filter(recipes -> recipes.matches(itemEntity, world, at))
                .findFirst()
                .orElse(null);
    }

    public static void tryCraft(ItemEntity itemEntity) {
        if (!itemEntity.isAlive()) {
            return;
        }
        World world = itemEntity.getEntityWorld();
        BlockPos floorAt = itemEntity.getPosition();

        LiquidStarlightRecipe recipe = LiquidStarlightCraftingRegistry.getRecipeFor(itemEntity, world, floorAt);
        if (recipe != null) {
            if (!world.isRemote()) {
                recipe.doServerCraftTick(itemEntity, world, floorAt);
            } else {
                recipe.doClientEffectTick(itemEntity, world, floorAt);
            }
        }
    }

}
