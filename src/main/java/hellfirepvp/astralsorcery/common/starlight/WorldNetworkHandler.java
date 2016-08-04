package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.LightNetworkBuffer;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldNetworkHandler
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:09
 */
public class WorldNetworkHandler {

    private final LightNetworkBuffer buffer;
    private World world;

    public WorldNetworkHandler(LightNetworkBuffer lightNetworkBuffer, World world) {
        this.buffer = lightNetworkBuffer;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public static WorldNetworkHandler getNetworkHandler(World world) {
        LightNetworkBuffer buffer = WorldCacheManager.getData(world, WorldCacheManager.SaveKey.LIGHT_NETWORK);
        return buffer.getNetworkHandler(world);
    }

    public void informBlockChange(BlockPos at) {
        List<LightNetworkBuffer.ChunkSectionNetworkData> relatedData = getAffectedChunkSections(at);
        if(relatedData.isEmpty()) return; //lucky. nothing to do.

        for (LightNetworkBuffer.ChunkSectionNetworkData data : relatedData) {
            if(data == null) continue;
            Collection<IPrismTransmissionNode> transmissionNodes = data.getAllTransmissionNodes();
            for (IPrismTransmissionNode node : transmissionNodes) {
                node.notifyBlockChange(getWorld(), at);
            }
        }
    }

    @Nullable
    public IPrismTransmissionNode getTransmissionNode(@Nullable BlockPos pos) {
        if(pos == null) return null;
        LightNetworkBuffer.ChunkSectionNetworkData section = getNetworkData(pos);
        if(section != null) {
            return section.getTransmissionNode(pos);
        }
        return null;
    }

    public void removeSource(IStarlightSource source) {
        removeThisSourceFromNext(source);

        buffer.removeSource(source.getPos());
    }

    public void removeTransmission(IStarlightTransmission transmission) {
        removeThisSourceFromNext(transmission);

        buffer.removeTransmission(transmission.getPos());
    }

    public void addNewSourceTile(IStarlightSource source) {
        buffer.addSource(source, source.getPos());

        linkNextToThisSources(source);
    }

    public void addTransmissionTile(IStarlightTransmission transmission) {
        buffer.addTransmission(transmission, transmission.getPos());

        linkNextToThisSources(transmission);
    }

    //What it does: For all "next"'s of this "tr" inform them that "tr" has been removed and
    //Remove "tr" from their sources.
    private void removeThisSourceFromNext(IStarlightTransmission tr) {
        IPrismTransmissionNode node = tr.getNode();
        if(node == null) {
            AstralSorcery.log.warn("Can't find Transmission node for removal-queued Transmission tile!");
            AstralSorcery.log.warn("This is an implementation error. Report it along with the steps to create this, if you come across this.");
            return;
        }
        BlockPos thisPos = tr.getPos();
        List<NodeConnection<IPrismTransmissionNode>> nodeConnections = node.queryNext(this);
        for (NodeConnection<IPrismTransmissionNode> connection : nodeConnections) {
            if(connection.getNode() != null) {
                connection.getNode().notifySourceUnlink(getWorld(), thisPos);
            }
        }
    }

    //That's what i call resource intensive.
    //What it does: Checks if there is a Node that has a "next" at this "tr"'s position.
    //If yes, it'll add that node as source for this "tr" to make backwards find possible.
    private void linkNextToThisSources(IStarlightTransmission tr) {
        IPrismTransmissionNode node = tr.getNode();
        if(node == null) {
            AstralSorcery.log.warn("Previously added Transmission tile didn't create a Transmission node!");
            AstralSorcery.log.warn("This is an implementation error. Report it along with the steps to create this, if you come across this.");
            return;
        }
        BlockPos thisPos = tr.getPos();
        List<LightNetworkBuffer.ChunkSectionNetworkData> dataList = getAffectedChunkSections(tr.getPos());
        for (LightNetworkBuffer.ChunkSectionNetworkData data : dataList) {
            if(data == null) continue;
            for (IPrismTransmissionNode otherNode : data.getAllTransmissionNodes()) {
                List<NodeConnection<IPrismTransmissionNode>> nodeConnections = otherNode.queryNext(this);
                for (NodeConnection<IPrismTransmissionNode> connection : nodeConnections) {
                    if(connection.getTo().equals(thisPos)) {
                        node.notifySourceLink(getWorld(), otherNode.getPos());
                    }
                }
            }
        }
    }

    //Collects a 3x3 field of chunkData, centered around the given pos.
    //Might be empty, if there's no network nearby.
    private List<LightNetworkBuffer.ChunkSectionNetworkData> getAffectedChunkSections(BlockPos centralPos) {
        List<LightNetworkBuffer.ChunkSectionNetworkData> dataList = new LinkedList<>();
        ChunkPos central = new ChunkPos(centralPos);
        int posYLevel = (centralPos.getY() & 255) >> 4;
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++) {
                    queryData(new ChunkPos(central.chunkXPos + xx, central.chunkZPos + zz), posYLevel + yy, dataList);
                }
            }
        }
        return dataList;
    }

    private void queryData(ChunkPos pos, int yLevel, List<LightNetworkBuffer.ChunkSectionNetworkData> out) {
        LightNetworkBuffer.ChunkSectionNetworkData data = buffer.getSectionData(pos, yLevel);
        if(data != null && !data.isEmpty()) out.add(data);
    }

    @Nullable
    private LightNetworkBuffer.ChunkSectionNetworkData getNetworkData(BlockPos at) {
        LightNetworkBuffer.ChunkSectionNetworkData data = buffer.getSectionData(at);
        if(data != null && !data.isEmpty()) return data;
        return null;
    }

}
