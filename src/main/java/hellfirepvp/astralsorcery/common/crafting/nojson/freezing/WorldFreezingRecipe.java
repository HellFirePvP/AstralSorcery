/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.freezing;

import hellfirepvp.astralsorcery.common.crafting.nojson.CustomRecipe;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldFreezingRecipe
 * Created by HellFirePvP
 * Date: 30.11.2019 / 19:10
 */
public abstract class WorldFreezingRecipe extends CustomRecipe {

    private final BlockPredicate matcher;

    public WorldFreezingRecipe(ResourceLocation key, BlockPredicate matcher) {
        super(key);
        this.matcher = matcher;
    }

    public boolean canFreeze(World world, BlockPos pos) {
        return this.matcher.test(world, pos, world.getBlockState(pos));
    }

    public abstract void doOutput(World world, BlockPos pos, BlockState state, Consumer<ItemStack> itemOutput);
}
