package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimplePrismTransmissionNode
 * Created by HellFirePvP
 * Date: 03.08.2016 / 16:58
 */
public class SimplePrismTransmissionNode implements IPrismTransmissionNode {

    private Map<BlockPos, PrismNext> nextNodes = new HashMap<>();

    private BlockPos thisPos;

    private Set<BlockPos> sourcesToThis = new HashSet<>();

    public SimplePrismTransmissionNode(@Nonnull BlockPos thisPos) {
        this.thisPos = thisPos;
    }

    @Override
    public BlockPos getPos() {
        return thisPos;
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
        PrismNext nextNode = new PrismNext(world, thisPos, pos, doRayCheck, previousRayState);
        this.nextNodes.put(pos, nextNode);
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos at) {
        boolean anyChange = false;
        for (PrismNext next : nextNodes.values()) {
            if(next.notifyBlockPlace(world, thisPos, at)) anyChange = true;
        }
        return anyChange;
    }

    @Override
    public void notifySourceLink(World world, BlockPos source) {
        if(!sourcesToThis.contains(source)) sourcesToThis.add(source);
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
        return sourcesToThis.stream().collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public TransmissionClassRegistry.TransmissionProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.thisPos = NBTUtils.readBlockPosFromNBT(compound);
        this.sourcesToThis.clear();

        NBTTagList list = compound.getTagList("sources", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            sourcesToThis.add(NBTUtils.readBlockPosFromNBT(list.getCompoundTagAt(i)));
        }

        NBTTagList nextList = compound.getTagList("nextList", 10);
        for (int i = 0; i < nextList.tagCount(); i++) {
            NBTTagCompound tag = nextList.getCompoundTagAt(i);
            BlockPos next = NBTUtils.readBlockPosFromNBT(tag);
            boolean oldState = tag.getBoolean("rayState");
            addLink(null, next, false, oldState); //Rebuild link.
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTUtils.writeBlockPosToNBT(thisPos, compound);

        NBTTagList sources = new NBTTagList();
        for (BlockPos source : sourcesToThis) {
            NBTTagCompound comp = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(source, comp);
            sources.appendTag(comp);
        }
        compound.setTag("sources", sources);

        NBTTagList nextList = new NBTTagList();
        for (BlockPos next : nextNodes.keySet()) {
            PrismNext prism = nextNodes.get(next);
            NBTTagCompound pos = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(next, pos);
            pos.setBoolean("rayState", prism.reachable);
            nextList.appendTag(pos);
        }
        compound.setTag("nextList", nextList);
    }

    private static class PrismNext {

        private boolean reachable = false;
        private double distanceSq;
        private final BlockPos pos;
        private RaytraceAssist rayAssist = null;

        private PrismNext(World world, BlockPos start, BlockPos end, boolean doRayTest, boolean oldRayState) {
            this.pos = end;
            this.rayAssist = new RaytraceAssist(start, end);
            if(doRayTest) {
                this.reachable = rayAssist.isClear(world);
            } else {
                this.reachable = oldRayState;
            }
            this.distanceSq = end.distanceSq(start.getX(), start.getY(), start.getZ());
        }

        private boolean notifyBlockPlace(World world, BlockPos connect, BlockPos at) {
            double dstStart = connect.distanceSq(at.getX(), at.getY(), at.getZ());
            double dstEnd = pos.distanceSq(at.getX(), at.getY(), at.getZ());
            if(dstStart > distanceSq || dstEnd > distanceSq) return false;
            boolean oldState = this.reachable;
            this.reachable = rayAssist.isClear(world);
            return this.reachable != oldState;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePrismTransmissionNode that = (SimplePrismTransmissionNode) o;
        return !(thisPos != null ? !thisPos.equals(that.thisPos) : that.thisPos != null);

    }

    @Override
    public int hashCode() {
        return thisPos != null ? thisPos.hashCode() : 0;
    }

    public static class Provider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public IPrismTransmissionNode provideEmptyNode() {
            return new SimplePrismTransmissionNode(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":SimplePrismTransmissionNode";
        }

    }

}
