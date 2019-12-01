/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson;

import hellfirepvp.astralsorcery.common.crafting.nojson.freezing.BlockFreezingRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.freezing.FluidFreezingRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.freezing.WorldFreezingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldFreezingRegistry
 * Created by HellFirePvP
 * Date: 30.11.2019 / 19:10
 */
public class WorldFreezingRegistry {

    private static Map<ResourceLocation, WorldFreezingRecipe> recipes = new HashMap<>();

    public static void init() {
        register(BlockFreezingRecipe.of(Blocks.FIRE, Blocks.AIR.getDefaultState()));
        register(BlockFreezingRecipe.of(Blocks.AIR.getDefaultState(), Blocks.ICE.getDefaultState()));
        register(BlockFreezingRecipe.of(Blocks.CAVE_AIR.getDefaultState(), Blocks.PACKED_ICE.getDefaultState()));

        register(new FluidFreezingRecipe());
    }

    public static void register(WorldFreezingRecipe recipe) {
        recipes.put(recipe.getKey(), recipe);
    }

    @Nullable
    public static WorldFreezingRecipe getRecipe(ResourceLocation key) {
        return recipes.get(key);
    }

    @Nullable
    public static WorldFreezingRecipe getRecipeFor(World world, BlockPos pos) {
        return recipes.values()
                .stream()
                .filter(recipe -> recipe.canFreeze(world, pos))
                .findFirst()
                .orElse(null);
    }

}
