/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.starlight.network.handler.BlockTransmutationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightNetworkRegistry
 * Created by HellFirePvP
 * Date: 04.08.2016 / 22:25
 */
public class StarlightNetworkRegistry {

    private static List<IStarlightBlockHandler> blockHandlers = new LinkedList<>();

    @Nullable
    public static IStarlightBlockHandler getStarlightHandler(World world, BlockPos pos, BlockState state, IWeakConstellation cst) {
        Block b = state.getBlock();
        if (b instanceof BlockStarlightRecipient) {
            return null;
        }
        for (IStarlightBlockHandler handler : blockHandlers) {
            if (handler.isApplicable(world, pos, state, cst)) {
                return handler;
            }
        }
        return null;
    }

    public static void registerBlockHandler(IStarlightBlockHandler handler) {
        blockHandlers.add(handler);
    }

    public static void setupRegistry() {
        registerBlockHandler(new BlockTransmutationHandler());
    }

    //1 instance is/should be created for 1 type of block+meta pair
    //This is NOT suggested as "first choice" - please implement BlockStarlightRecipient instead if possible.
    public static interface IStarlightBlockHandler {

        public boolean isApplicable(World world, BlockPos pos, BlockState state, IWeakConstellation starlightType);

        public void receiveStarlight(World world, Random rand, BlockPos pos, BlockState state, IWeakConstellation starlightType, double amount);

    }

}
