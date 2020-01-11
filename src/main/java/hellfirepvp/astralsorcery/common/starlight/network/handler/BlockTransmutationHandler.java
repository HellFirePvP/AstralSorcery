/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network.handler;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutationContext;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightNetworkRegistry;
import hellfirepvp.astralsorcery.common.util.block.WorldBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationHandler
 * Created by HellFirePvP
 * Date: 13.10.2019 / 10:41
 */
public class BlockTransmutationHandler implements StarlightNetworkRegistry.IStarlightBlockHandler {

    private static Map<WorldBlockPos, ActiveTransmutation> runningTransmutations = new HashMap<>();

    @Override
    public boolean isApplicable(World world, BlockPos pos, BlockState state, IWeakConstellation starlightType) {
        return RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.findRecipe(new BlockTransmutationContext(world, pos, state, starlightType)) != null;
    }

    @Override
    public void receiveStarlight(World world, Random rand, BlockPos pos, BlockState state, IWeakConstellation starlightType, double amount) {
        BlockTransmutation recipe = RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.findRecipe(new BlockTransmutationContext(world, pos, state, starlightType));
        if (recipe == null) {
            return; //Wait what
        }

        WorldBlockPos at = WorldBlockPos.wrapServer(world, pos);
        ActiveTransmutation activeRecipe = runningTransmutations.get(at);
        if (activeRecipe == null || !activeRecipe.recipe.equals(recipe)) {
            activeRecipe = new ActiveTransmutation(recipe);
            runningTransmutations.put(at, activeRecipe);
        }

        activeRecipe.acceptStarlight(amount);

        if (activeRecipe.isFinished() && activeRecipe.finish(world, pos)) {
            runningTransmutations.remove(at);
        }
    }

    private static class ActiveTransmutation {

        //After 15 seconds no charge, we throw it all away.
        private static final int MS_THRESHOLD = 15_000;

        private final BlockTransmutation recipe;
        private long lastMillisecondStarlightReceived = System.currentTimeMillis();
        private double accumulatedStarlight = 0;

        private ActiveTransmutation(BlockTransmutation recipe) {
            this.recipe = recipe;
        }

        private void acceptStarlight(double amount) {
            long msReceived = System.currentTimeMillis();
            long receiveDiff = msReceived - this.lastMillisecondStarlightReceived;
            if (receiveDiff >= MS_THRESHOLD) {
                this.accumulatedStarlight = 0;
            }

            this.accumulatedStarlight += amount;
            this.lastMillisecondStarlightReceived = msReceived;
        }

        private boolean isFinished() {
            return this.accumulatedStarlight >= this.recipe.getStarlightRequired();
        }

        private boolean finish(IWorld world, BlockPos pos) {
            if (world.setBlockState(pos, this.recipe.getOutput(), Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
                return true;
            }
            //Retry a bit later
            this.accumulatedStarlight *= 0.9F;
            return false;
        }
    }
}
