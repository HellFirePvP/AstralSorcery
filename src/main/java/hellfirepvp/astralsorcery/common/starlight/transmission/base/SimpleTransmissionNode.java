/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionNodeLens
 * Created by HellFirePvP
 * Date: 03.08.2016 / 11:09
 */
public class SimpleTransmissionNode implements ITransmissionNode {

    private boolean ignoreBlockCollision = false;

    private boolean nextReachable = false;
    private BlockPos nextPos = null;
    private double dstToNextSq = 0;
    private RaytraceAssist assistNext = null;

    private BlockPos thisPos;

    private Set<BlockPos> sourcesToThis = new HashSet<>();

    public SimpleTransmissionNode(BlockPos thisPos) {
        this.thisPos = thisPos;
    }

    @Override
    public BlockPos getLocationPos() {
        return thisPos;
    }

    public void updateIgnoreBlockCollisionState(World world, boolean ignoreBlockCollision) {
        this.ignoreBlockCollision = ignoreBlockCollision;
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(world);
        if (assistNext != null && handle != null) {
            boolean oldState = this.nextReachable;
            this.nextReachable = ignoreBlockCollision || assistNext.isClear(world);
            if (nextReachable != oldState) {
                handle.notifyTransmissionNodeChange(this);
            }
        }
    }

    public boolean ignoresBlockCollision() {
        return ignoreBlockCollision;
    }

    @Override
    public boolean notifyUnlink(World world, BlockPos to) {
        if (to.equals(nextPos)) { //cleanup
            this.nextPos = null;
            this.assistNext = null;
            this.dstToNextSq = 0;
            this.nextReachable = false;
            return true;
        }
        return false;
    }

    @Override
    public void notifyLink(World world, BlockPos pos) {
        addLink(world, pos, true, false);
    }

    private void addLink(World world, BlockPos pos, boolean doRayTest, boolean oldRayState) {
        this.nextPos = pos;
        this.assistNext = new RaytraceAssist(thisPos, nextPos);
        if (doRayTest) {
            this.nextReachable = this.ignoreBlockCollision || assistNext.isClear(world);
        } else {
            this.nextReachable = oldRayState;
        }
        this.dstToNextSq = pos.distanceSq(thisPos);
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos at) {
        if (nextPos == null) {
            return false;
        }
        double dstStart = thisPos.distanceSq(at);
        double dstEnd = nextPos.distanceSq(at);
        if (dstStart > dstToNextSq || dstEnd > dstToNextSq) {
            return false; //out of range
        }
        boolean oldState = this.nextReachable;
        this.nextReachable = ignoreBlockCollision || assistNext.isClear(world);
        return this.nextReachable != oldState;
    }

    @Override
    public void notifySourceLink(World world, BlockPos source) {
        sourcesToThis.add(source);
    }

    @Override
    public void notifySourceUnlink(World world, BlockPos source) {
        sourcesToThis.remove(source);
    }

    @Override
    public NodeConnection<IPrismTransmissionNode> queryNextNode(WorldNetworkHandler handler) {
        if (nextPos == null) {
            return null;
        }
        return new NodeConnection<>(handler.getTransmissionNode(nextPos), nextPos, nextReachable);
    }

    @Override
    public List<BlockPos> getSources() {
        return new ArrayList<>(sourcesToThis);
    }

    @Override
    public TransmissionProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        this.thisPos = NBTHelper.readBlockPosFromNBT(compound);
        this.sourcesToThis.clear();
        this.ignoreBlockCollision = compound.getBoolean("ignoreBlockCollision");

        ListNBT list = compound.getList("sources", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            sourcesToThis.add(NBTHelper.readBlockPosFromNBT(list.getCompound(i)));
        }

        if (compound.contains("nextPos")) {
            CompoundNBT tag = compound.getCompound("nextPos");
            BlockPos next = NBTHelper.readBlockPosFromNBT(tag);
            boolean oldRay = tag.getBoolean("rayState");
            addLink(null, next, false, oldRay);
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        NBTHelper.writeBlockPosToNBT(thisPos, compound);
        compound.putBoolean("ignoreBlockCollision", this.ignoreBlockCollision);

        ListNBT sources = new ListNBT();
        for (BlockPos source : sourcesToThis) {
            CompoundNBT comp = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(source, comp);
            sources.add(comp);
        }
        compound.put("sources", sources);

        if (nextPos != null) {
            CompoundNBT pos = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(nextPos, pos);
            pos.putBoolean("rayState", nextReachable);
            compound.put("nextPos", pos);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTransmissionNode that = (SimpleTransmissionNode) o;
        return Objects.equals(thisPos, that.thisPos);
    }

    @Override
    public int hashCode() {
        return thisPos != null ? thisPos.hashCode() : 0;
    }

    public static class Provider extends TransmissionProvider {

        @Override
        public IPrismTransmissionNode get() {
            return new SimpleTransmissionNode(null);
        }
    }

}
