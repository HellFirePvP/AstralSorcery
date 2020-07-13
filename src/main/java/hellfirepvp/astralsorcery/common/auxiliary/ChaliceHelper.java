/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChaliceHelper
 * Created by HellFirePvP
 * Date: 09.11.2019 / 15:01
 */
public class ChaliceHelper {

    @Nonnull
    public static List<BlockPos> findNearbyChalices(World world, BlockPos origin, int distance) {
        distance = MathHelper.clamp(distance, 0, 16);
        Vector3 thisVector = new Vector3(origin).add(0.5, 0.5, 0.5);

        List<BlockPos> foundChalices = BlockDiscoverer.searchForBlocksAround(world, origin, distance,
                (w, pos, state) -> !w.isBlockPowered(pos) && state.getBlock().equals(BlocksAS.CHALICE));

        foundChalices.removeIf(pos -> {
            Vector3 chaliceVector = new Vector3(pos).add(0.5, 0.5, 0.5);
            RaytraceAssist assist = new RaytraceAssist(thisVector, chaliceVector);
            return !assist.isClear(world);
        });

        return foundChalices;
    }

    //Distance must be positive and less or equal to 16
    @Nonnull
    public static List<TileChalice> findNearbyChalicesContaining(World world, BlockPos origin, FluidStack expected, int distance) {
        List<TileChalice> out = new LinkedList<>();
        for (BlockPos chalicePos : findNearbyChalices(world, origin, distance)) {
            TileChalice chalice = MiscUtils.getTileAt(world, chalicePos, TileChalice.class, true);
            if (chalice != null && chalice.getTank().drain(expected, IFluidHandler.FluidAction.SIMULATE).getAmount() >= expected.getAmount()) {
                out.add(chalice);
            }
        }

        return out;
    }

    //Distance must be positive and less or equal to 16
    //Only returns a value if the chalices combined fulfilled the amount requirement.
    @Nonnull
    public static Optional<List<TileChalice>> findNearbyChalicesCombined(World world, BlockPos origin, FluidStack expected, int distance) {
        FluidStack required = expected.copy();

        List<TileChalice> out = new LinkedList<>();
        for (BlockPos chalicePos : findNearbyChalices(world, origin, distance)) {
            TileChalice chalice = MiscUtils.getTileAt(world, chalicePos, TileChalice.class, true);
            if (chalice != null) {
                FluidStack drained = chalice.getTank().drain(expected, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    required.shrink(drained.getAmount());
                    out.add(chalice);
                }
            }
        }

        if (required.isEmpty()) {
            return Optional.of(out);
        }
        return Optional.empty();
    }

    public static boolean doChalicesContainCombined(World world, Collection<BlockPos> chalicePositions, FluidStack expected) {
        FluidStack required = expected.copy();

        for (BlockPos pos : chalicePositions) {
            TileChalice chalice = MiscUtils.getTileAt(world, pos, TileChalice.class, true);
            if (chalice != null) {
                FluidStack drained = chalice.getTank().drain(expected, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    required.shrink(drained.getAmount());
                }
            }
        }

        return required.isEmpty();
    }
}
