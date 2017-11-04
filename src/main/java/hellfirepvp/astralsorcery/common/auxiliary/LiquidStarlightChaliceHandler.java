/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.entities.EntityLiquidSpark;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.ILiquidStarlightPowered;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidStarlightChaliceHandler
 * Created by HellFirePvP
 * Date: 04.11.2017 / 17:16
 */
public class LiquidStarlightChaliceHandler {

    public static boolean requestLiquidStarlightFrom(ILiquidStarlightPowered target, int tileTicksExisted, int mbRequested) {
        if(!(target instanceof TileEntity) || (tileTicksExisted % 100) != 0) {
            return false;
        }

        Vector3 thisV = new Vector3(((TileEntity) target).getPos()).add(0.5, 0.5, 0.5);
        FluidStack expected = new FluidStack(BlocksAS.fluidLiquidStarlight, mbRequested);
        World world = ((TileEntity) target).getWorld();
        int chX = ((TileEntity) target).getPos().getX() >> 4;
        int chZ = ((TileEntity) target).getPos().getZ() >> 4;

        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                int cX = chX + xx;
                int cZ = chZ + zz;
                if(world.isBlockLoaded(new BlockPos(cX * 16, 1, cZ * 16))) {
                    Chunk ch = world.getChunkFromChunkCoords(cX, cZ);
                    Map<BlockPos, TileEntity> tiles = ch.getTileEntityMap();
                    for (TileEntity te : tiles.values()) {
                        if(!te.isInvalid() && te instanceof TileChalice &&
                                new Vector3(te.getPos()).distance(thisV) <= 16) {
                            TileChalice tc = (TileChalice) te;
                            if(tc.getTank() != null &&
                                    tc.getTank().getFluid() != null &&
                                    tc.getTank().getFluid().containsFluid(expected)) {
                                FluidStack drained = tc.getTank().drain(expected, true);
                                if(drained != null) {
                                    tc.markForUpdate();
                                    EntityLiquidSpark spark = new EntityLiquidSpark(world, tc.getPos().up(), target);
                                    spark.setFluidRepresented(new FluidStack(BlocksAS.fluidLiquidStarlight, drained.amount));
                                    world.spawnEntity(spark);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nonnull
    public static List<TileChalice> findNearbyChalices(TileEntity origin, int amountExpected) {
        List<TileChalice> out = new LinkedList<>();
        FluidStack expected = new FluidStack(BlocksAS.fluidLiquidStarlight, amountExpected);
        Vector3 thisV = new Vector3(origin).add(0.5, 0.5, 0.5);
        World world = origin.getWorld();

        int chX = origin.getPos().getX() >> 4;
        int chZ = origin.getPos().getZ() >> 4;
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                int cX = chX + xx;
                int cZ = chZ + zz;
                if(world.isBlockLoaded(new BlockPos(cX * 16, 1, cZ * 16))) {
                    Chunk ch = world.getChunkFromChunkCoords(cX, cZ);
                    Map<BlockPos, TileEntity> tiles = ch.getTileEntityMap();
                    for (TileEntity te : tiles.values()) {
                        if(!te.isInvalid() && te instanceof TileChalice &&
                                new Vector3(te.getPos()).distance(thisV) <= 16) {
                            TileChalice tc = (TileChalice) te;
                            if(tc.getTank() != null &&
                                    tc.getTank().getFluid() != null &&
                                    tc.getTank().getFluid().containsFluid(expected)) {
                                out.add(tc);
                            }
                        }
                    }
                }
            }
        }

        Iterator<TileChalice> iterator = out.iterator();
        while (iterator.hasNext()) {
            TileChalice tc = iterator.next();
            RaytraceAssist rta = new RaytraceAssist(thisV, new Vector3(tc.getPos()).add(0.5, 0.5, 0.5));
            if(!rta.isClear(world)) {
                iterator.remove();
            }
        }

        return out;
    }

}
