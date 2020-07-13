/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson;

import hellfirepvp.astralsorcery.common.crafting.nojson.freezing.BlockFreezingRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.freezing.FluidFreezingRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.freezing.WorldFreezingRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldFreezingRegistry
 * Created by HellFirePvP
 * Date: 30.11.2019 / 19:10
 */
public class WorldFreezingRegistry extends CustomRecipeRegistry<WorldFreezingRecipe> {

    public static final WorldFreezingRegistry INSTANCE = new WorldFreezingRegistry();

    @Override
    public void init() {
        this.register(BlockFreezingRecipe.of(Blocks.FIRE, Blocks.AIR.getDefaultState()));
        this.register(BlockFreezingRecipe.of(Blocks.AIR.getDefaultState(), Blocks.ICE.getDefaultState()));
        this.register(BlockFreezingRecipe.of(Blocks.CAVE_AIR.getDefaultState(), Blocks.PACKED_ICE.getDefaultState()));

        this.register(new FluidFreezingRecipe());
    }

    @Nullable
    public WorldFreezingRecipe getRecipeFor(World world, BlockPos pos) {
        return this.getRecipes()
                .stream()
                .filter(recipe -> recipe.canFreeze(world, pos))
                .findFirst()
                .orElse(null);
    }

}
