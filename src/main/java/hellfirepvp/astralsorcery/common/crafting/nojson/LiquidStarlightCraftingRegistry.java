package hellfirepvp.astralsorcery.common.crafting.nojson;

import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.FormCelestialCrystalClusterRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.FormGemCrystalClusterRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.InfusedWoodRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.starlight.LiquidStarlightRecipe;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidStarlightCraftingRegistry
 * Created by HellFirePvP
 * Date: 30.09.2019 / 20:27
 */
public class LiquidStarlightCraftingRegistry extends CustomRecipeRegistry<LiquidStarlightRecipe> {

    public static final LiquidStarlightCraftingRegistry INSTANCE = new LiquidStarlightCraftingRegistry();

    @Override
    public void init() {
        this.register(new InfusedWoodRecipe());
        this.register(new FormCelestialCrystalClusterRecipe());
        this.register(new FormGemCrystalClusterRecipe());
    }

    @Nullable
    public LiquidStarlightRecipe getRecipeFor(ItemEntity itemEntity, World world, BlockPos at) {
        return this.getRecipes()
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

        LiquidStarlightRecipe recipe = LiquidStarlightCraftingRegistry.INSTANCE.getRecipeFor(itemEntity, world, floorAt);
        if (recipe != null) {
            if (!world.isRemote()) {
                recipe.doServerCraftTick(itemEntity, world, floorAt);
            } else {
                recipe.doClientEffectTick(itemEntity, world, floorAt);
            }
        }
    }

}
