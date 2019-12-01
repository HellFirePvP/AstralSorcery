/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson;

import hellfirepvp.astralsorcery.common.crafting.nojson.meltable.BlockMeltableRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.meltable.FurnaceMeltableRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.meltable.WorldMeltableRecipe;
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
 * Class: WorldMeltableRegistry
 * Created by HellFirePvP
 * Date: 29.11.2019 / 22:56
 */
public class WorldMeltableRegistry {

    private static Map<ResourceLocation, WorldMeltableRecipe> recipes = new HashMap<>();

    public static void init() {
        register(BlockMeltableRecipe.of(BlockTags.ICE, Blocks.WATER.getDefaultState()));
        register(BlockMeltableRecipe.of(Tags.Blocks.STONE, Blocks.LAVA.getDefaultState()));
        register(BlockMeltableRecipe.of(Tags.Blocks.NETHERRACK, Blocks.LAVA.getDefaultState()));
        register(BlockMeltableRecipe.of(Tags.Blocks.OBSIDIAN, Blocks.LAVA.getDefaultState()));
        register(BlockMeltableRecipe.of(Blocks.MAGMA_BLOCK.getDefaultState(), Blocks.LAVA.getDefaultState()));

        register(new FurnaceMeltableRecipe());
    }

    public static void register(WorldMeltableRecipe recipe) {
        recipes.put(recipe.getKey(), recipe);
    }

    @Nullable
    public static WorldMeltableRecipe getRecipe(ResourceLocation key) {
        return recipes.get(key);
    }

    @Nullable
    public static WorldMeltableRecipe getRecipeFor(World world, BlockPos pos) {
        return recipes.values()
                .stream()
                .filter(recipe -> recipe.canMelt(world, pos))
                .findFirst()
                .orElse(null);
    }
}
