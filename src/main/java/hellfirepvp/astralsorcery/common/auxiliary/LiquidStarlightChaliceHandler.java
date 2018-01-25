/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

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

    public static boolean doFluidTransfer(@Nonnull TileEntity source, @Nonnull TileEntity target, @Nonnull FluidStack toTransfer) {
        if(target.isInvalid() || source.isInvalid()) {
            return false;
        }

        IFluidHandler targetHandler = null;
        if(target.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            targetHandler = target.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        }
        if(targetHandler == null) {
            return false;
        }
        if(targetHandler.fill(toTransfer, false) < toTransfer.amount) {
            return false;
        }
        World world = source.getWorld();
        EntityLiquidSpark spark = new EntityLiquidSpark(world, source.getPos().up(), target);
        spark.setFluidRepresented(toTransfer);
        world.spawnEntity(spark);
        return true;
    }

    public static boolean requestLiquidStarlightAndTransferTo(ILiquidStarlightPowered target, TileChalice source, int tileTicksExisted, int mbRequested) {
        if(!(target instanceof TileEntity) || (tileTicksExisted % 100) != 0) {
            return false;
        }

        FluidStack expected = new FluidStack(BlocksAS.fluidLiquidStarlight, mbRequested);
        World world = ((TileEntity) target).getWorld();
        if(source.getTank() != null &&
                source.getTank().getFluid() != null &&
                source.getTank().getFluid().containsFluid(expected)) {
            FluidStack drained = source.getTank().drain(expected, true);
            if(drained != null) {
                source.markForUpdate();
                EntityLiquidSpark spark = new EntityLiquidSpark(world, source.getPos().up(), (TileEntity) target);
                spark.setFluidRepresented(new FluidStack(BlocksAS.fluidLiquidStarlight, drained.amount));
                world.spawnEntity(spark);
                return true;
            }
        }
        return false;
    }

    public static boolean requestLiquidStarlightAndTransferTo(ILiquidStarlightPowered target, int tileTicksExisted, int mbRequested) {
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
                            if(world.isBlockIndirectlyGettingPowered(te.getPos()) > 0) continue;
                            TileChalice tc = (TileChalice) te;
                            RaytraceAssist rta = new RaytraceAssist(thisV, new Vector3(tc.getPos()).add(0.5, 0.5, 0.5));
                            if(!rta.isClear(world)) {
                                continue;
                            }
                            if(tc.getTank() != null &&
                                    tc.getTank().getFluid() != null &&
                                    tc.getTank().getFluid().containsFluid(expected)) {
                                FluidStack drained = tc.getTank().drain(expected, true);
                                if(drained != null) {
                                    tc.markForUpdate();
                                    EntityLiquidSpark spark = new EntityLiquidSpark(world, tc.getPos().up(), (TileEntity) target);
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
    public static List<TileChalice> findNearbyChalicesWithSpaceFor(TileEntity origin, FluidStack stackExpectedToFit) {
        List<TileChalice> out = new LinkedList<>();
        FluidStack expected = stackExpectedToFit.copy();
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
                            if(world.isBlockIndirectlyGettingPowered(te.getPos()) > 0) continue;
                            TileChalice tc = (TileChalice) te;
                            if(tc.getTank() != null &&
                                    tc.getTank().canFillFluidType(expected) &&
                                    tc.getTank().fill(expected, false) >= expected.amount) {
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

    @Nonnull
    public static List<TileChalice> findNearbyChalicesThatContain(TileEntity origin, FluidStack expected) {
        List<TileChalice> out = new LinkedList<>();
        expected = expected.copy();
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
                            if(world.isBlockIndirectlyGettingPowered(te.getPos()) > 0) continue;
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
