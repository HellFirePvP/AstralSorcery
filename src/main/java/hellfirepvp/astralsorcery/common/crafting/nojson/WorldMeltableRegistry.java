/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldMeltableRegistry
 * Created by HellFirePvP
 * Date: 29.11.2019 / 22:56
 */
public class WorldMeltableRegistry extends CustomRecipeRegistry<WorldMeltableRecipe> {

    public static final WorldMeltableRegistry INSTANCE = new WorldMeltableRegistry();

    @Override
    public void init() {
        this.register(BlockMeltableRecipe.of(BlockTags.ICE, Blocks.WATER.getDefaultState()));
        this.register(BlockMeltableRecipe.of(Tags.Blocks.STONE, Blocks.LAVA.getDefaultState()));
        this.register(BlockMeltableRecipe.of(Tags.Blocks.NETHERRACK, Blocks.LAVA.getDefaultState()));
        this.register(BlockMeltableRecipe.of(Tags.Blocks.OBSIDIAN, Blocks.LAVA.getDefaultState()));
        this.register(BlockMeltableRecipe.of(Blocks.MAGMA_BLOCK.getDefaultState(), Blocks.LAVA.getDefaultState()));

        this.register(new FurnaceMeltableRecipe());
    }

    @Nullable
    public WorldMeltableRecipe getRecipeFor(World world, BlockPos pos) {
        return this.getRecipes()
                .stream()
                .filter(recipe -> recipe.canMelt(world, pos))
                .findFirst()
                .orElse(null);
    }
}
