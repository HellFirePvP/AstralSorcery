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
 * Class: SimplePrismTransmissionNode
 * Created by HellFirePvP
 * Date: 03.08.2016 / 16:58
 */
public class SimplePrismTransmissionNode implements IPrismTransmissionNode {

    private boolean ignoreBlockCollision = false;

    private Map<BlockPos, PrismNext> nextNodes = new HashMap<>();

    private BlockPos thisPos;

    private Set<BlockPos> sourcesToThis = new HashSet<>();

    public SimplePrismTransmissionNode(BlockPos thisPos) {
        this.thisPos = thisPos;
    }

    @Override
    public BlockPos getLocationPos() {
        return thisPos;
    }

    public void updateIgnoreBlockCollisionState(World world, boolean ignoreBlockCollision) {
        this.ignoreBlockCollision = ignoreBlockCollision;
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(world);
        if (handle != null) {
            boolean anyChange = false;
            for (PrismNext next : nextNodes.values()) {
                boolean oldState = next.reachable;
                next.reachable = ignoreBlockCollision || next.rayAssist.isClear(world);
                if (next.reachable != oldState) {
                    anyChange = true;
                }
            }
            if (anyChange) {
                handle.notifyTransmissionNodeChange(this);
            }
        }
    }

    public boolean ignoresBlockCollision() {
        return ignoreBlockCollision;
    }

    @Override
    public boolean notifyUnlink(World world, BlockPos to) {
        return nextNodes.remove(to) != null;
    }

    @Override
    public void notifyLink(World world, BlockPos pos) {
        addLink(world, pos, true, false);
    }

    private void addLink(World world, BlockPos pos, boolean doRayCheck, boolean previousRayState) {
        PrismNext nextNode = new PrismNext(this, world, thisPos, pos, doRayCheck, previousRayState);
        this.nextNodes.put(pos, nextNode);
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos at) {
        boolean anyChange = false;
        for (PrismNext next : nextNodes.values()) {
            if (next.notifyBlockPlace(world, thisPos, at)) anyChange = true;
        }
        return anyChange;
    }

    @Override
    public void notifySourceLink(World world, BlockPos source) {
        if (!sourcesToThis.contains(source)) sourcesToThis.add(source);
    }

    @Override
    public void notifySourceUnlink(World world, BlockPos source) {
        sourcesToThis.remove(source);
    }

    @Override
    public List<NodeConnection<IPrismTransmissionNode>> queryNext(WorldNetworkHandler handler) {
        List<NodeConnection<IPrismTransmissionNode>> nodes = new LinkedList<>();
        for (BlockPos pos : nextNodes.keySet()) {
            nodes.add(new NodeConnection<>(handler.getTransmissionNode(pos), pos, nextNodes.get(pos).reachable));
        }
        return nodes;
    }

    @Override
    public List<BlockPos> getSources() {
        return new LinkedList<>(sourcesToThis);
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

        ListNBT nextList = compound.getList("nextList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nextList.size(); i++) {
            CompoundNBT tag = nextList.getCompound(i);
            BlockPos next = NBTHelper.readBlockPosFromNBT(tag);
            boolean oldState = tag.getBoolean("rayState");
            addLink(null, next, false, oldState); //Rebuild link.
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

        ListNBT nextList = new ListNBT();
        for (BlockPos next : nextNodes.keySet()) {
            PrismNext prism = nextNodes.get(next);
            CompoundNBT pos = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(next, pos);
            pos.putBoolean("rayState", prism.reachable);
            nextList.add(pos);
        }
        compound.put("nextList", nextList);
    }

    private static class PrismNext {

        private final SimplePrismTransmissionNode parent;
        private boolean reachable = false;
        private double distanceSq;
        private final BlockPos pos;
        private RaytraceAssist rayAssist = null;

        private PrismNext(SimplePrismTransmissionNode parent, World world, BlockPos start, BlockPos end, boolean doRayTest, boolean oldRayState) {
            this.parent = parent;
            this.pos = end;
            this.rayAssist = new RaytraceAssist(start, end);
            if (doRayTest) {
                this.reachable = parent.ignoreBlockCollision || rayAssist.isClear(world);
            } else {
                this.reachable = oldRayState;
            }
            this.distanceSq = end.distanceSq(start);
        }

        private boolean notifyBlockPlace(World world, BlockPos connect, BlockPos at) {
            double dstStart = connect.distanceSq(at);
            double dstEnd = pos.distanceSq(at);
            if (dstStart > distanceSq || dstEnd > distanceSq) return false;
            boolean oldState = this.reachable;
            this.reachable = parent.ignoreBlockCollision || rayAssist.isClear(world);
            return this.reachable != oldState;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePrismTransmissionNode that = (SimplePrismTransmissionNode) o;
        return Objects.equals(thisPos, that.thisPos);

    }

    @Override
    public int hashCode() {
        return thisPos != null ? thisPos.hashCode() : 0;
    }

    public static class Provider extends TransmissionProvider {

        @Override
        public IPrismTransmissionNode get() {
            return new SimplePrismTransmissionNode(null);
        }

    }

}
