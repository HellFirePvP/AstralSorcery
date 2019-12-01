/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.meltable;

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
 * Class: WorldMeltableRecipe
 * Created by HellFirePvP
 * Date: 29.11.2019 / 22:57
 */
public abstract class WorldMeltableRecipe {

    private final ResourceLocation key;
    private final BlockPredicate matcher;

    public WorldMeltableRecipe(ResourceLocation key, BlockPredicate matcher) {
        this.key = key;
        this.matcher = matcher;
    }

    public final ResourceLocation getKey() {
        return key;
    }

    public boolean canMelt(World world, BlockPos pos) {
        return this.matcher.test(world, pos, world.getBlockState(pos));
    }

    public abstract void doOutput(World world, BlockPos pos, BlockState state, Consumer<ItemStack> itemOutput);

}
