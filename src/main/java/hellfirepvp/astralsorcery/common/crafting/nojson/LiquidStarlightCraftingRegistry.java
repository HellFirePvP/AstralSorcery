package hellfirepvp.astralsorcery.common.crafting.nojson;

import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.FormCelestialCrystalClusterRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.FormGemCrystalClusterRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.InfusedWoodRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.LiquidStarlightRecipe;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidStarlightCraftingRegistry
 * Created by HellFirePvP
 * Date: 30.09.2019 / 20:27
 */
public class LiquidStarlightCraftingRegistry {

    private static Map<ResourceLocation, LiquidStarlightRecipe> recipes = new HashMap<>();

    public static void init() {
        register(new InfusedWoodRecipe());
        register(new FormCelestialCrystalClusterRecipe());
        register(new FormGemCrystalClusterRecipe());
    }

    public static void register(LiquidStarlightRecipe recipe) {
        recipes.put(recipe.getKey(), recipe);
    }

    @Nullable
    public static LiquidStarlightRecipe getRecipe(ResourceLocation key) {
        return recipes.get(key);
    }

    @Nullable
    public static LiquidStarlightRecipe getRecipeFor(ItemEntity itemEntity, World world, BlockPos at) {
        return recipes.values()
                .stream()
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
