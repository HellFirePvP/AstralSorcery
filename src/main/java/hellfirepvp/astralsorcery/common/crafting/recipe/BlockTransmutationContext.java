/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.RecipeCraftingContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationContext
 * Created by HellFirePvP
 * Date: 10.10.2019 / 19:28
 */
public class BlockTransmutationContext extends RecipeCraftingContext<BlockTransmutation, IItemHandler> {

    private final IWorld world;
    private final BlockPos pos;
    private final BlockState state;
    private final IWeakConstellation constellation;

    public BlockTransmutationContext(IWorld world, BlockPos pos, BlockState state, IWeakConstellation constellation) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.constellation = constellation;
    }

    public IWorld getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public IWeakConstellation getConstellation() {
        return constellation;
    }
}
