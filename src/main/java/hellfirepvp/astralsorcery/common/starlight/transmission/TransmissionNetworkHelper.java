/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightUpdateHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import hellfirepvp.astralsorcery.common.tile.base.TileNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionNetworkHelper
 * Created by HellFirePvP
 * Date: 03.08.2016 / 17:34
 */
public class TransmissionNetworkHelper {

    private static final double MAX_TRANSMISSION_DIST = 16; //Rip.

    public static boolean hasTransmissionLink(IStarlightTransmission tr, BlockPos end) {
        IPrismTransmissionNode node = tr.getNode();
        if (node == null) return false;
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(tr.getTrWorld());
        List<NodeConnection<IPrismTransmissionNode>> nextNodes = node.queryNext(handler);
        for (NodeConnection<IPrismTransmissionNode> nextNode : nextNodes) {
            if (nextNode.getTo().equals(end)) return true;
        }
        return false;
    }

    public static boolean canCreateTransmissionLink(IStarlightTransmission tr, BlockPos end) {
        IPrismTransmissionNode node = tr.getNode();
        if (node == null) return false;
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(tr.getTrWorld());
        List<NodeConnection<IPrismTransmissionNode>> nextNodes = node.queryNext(handler);
        for (NodeConnection<IPrismTransmissionNode> nextNode : nextNodes) {
            if (nextNode.getTo().equals(end)) return false;
        }

        return tr.getTrPos().withinDistance(new BlockPos(end), MAX_TRANSMISSION_DIST);
    }

    public static boolean createTransmissionLink(IStarlightTransmission tr, BlockPos next) {
        IPrismTransmissionNode node = tr.getNode();
        if (node == null) {
            AstralSorcery.log.info("Trying to create transmission link on non-existing transmission tile! Not creating link!");
            return false;
        }
        createLink(node, tr, next);
        return true;
    }

    public static void removeTransmissionLink(IStarlightTransmission tr, BlockPos next) {
        IPrismTransmissionNode node = tr.getNode();
        if (node == null) {
            return;
        }
        removeLink(node, tr, next);
    }

    //Harsh reality methods. xP
    private static void removeLink(IPrismTransmissionNode transmissionNode, IStarlightTransmission transmission, BlockPos to) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(transmission.getTrWorld());
        IPrismTransmissionNode nextNode = handler.getTransmissionNode(to);
        removeLink(transmissionNode, nextNode, transmission.getTrWorld(), transmission.getTrPos(), to);

        handler.markDirty(transmission.getTrPos(), to);
    }

    private static void removeLink(IPrismTransmissionNode thisNode, IPrismTransmissionNode nextNode, World world, BlockPos from, BlockPos to) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(world);
        if (nextNode != null) {
            nextNode.notifySourceUnlink(world, from);
            if (handle != null) {
                handle.notifyTransmissionNodeChange(nextNode);
            }
        }
        thisNode.notifyUnlink(world, to);
        if (handle != null) {
            handle.notifyTransmissionNodeChange(thisNode);
        }
    }

    private static void createLink(IPrismTransmissionNode transmissionNode, IStarlightTransmission transmission, BlockPos to) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(transmission.getTrWorld());
        IPrismTransmissionNode nextNode = handler.getTransmissionNode(to);
        createLink(transmissionNode, nextNode, transmission.getTrWorld(), transmission.getTrPos(), to);

        handler.markDirty(transmission.getTrPos(), to);
    }

    private static void createLink(IPrismTransmissionNode thisNode, IPrismTransmissionNode nextNode, World world, BlockPos from, BlockPos to) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(world);
        if (nextNode != null) {
            nextNode.notifySourceLink(world, from);
            if (handle != null) {
                handle.notifyTransmissionNodeChange(nextNode);
            }
        }
        thisNode.notifyLink(world, to);
        if (handle != null) {
            handle.notifyTransmissionNodeChange(thisNode);
        }
    }

    public static boolean isTileInNetwork(TileNetwork tileNetwork) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(tileNetwork.getWorld());
        return handler.getTransmissionNode(tileNetwork.getPos()) != null;
    }

    public static void informNetworkTilePlacement(TileNetwork tileNetwork) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(tileNetwork.getWorld());
        if (tileNetwork instanceof IStarlightSource) {
            handler.addNewSourceTile((IStarlightSource) tileNetwork);
        } else if (tileNetwork instanceof IStarlightTransmission) {
            handler.addTransmissionTile((IStarlightTransmission) tileNetwork);
        } else {
            AstralSorcery.log.warn("Placed a network tile that's not transmission/receiver or source! At: dim=" + tileNetwork.getWorld().getDimension().getType().getId() + ", pos=" + tileNetwork.getPos());
        }

        IPrismTransmissionNode node = handler.getTransmissionNode(tileNetwork.getPos());
        if (node == null) {
            AstralSorcery.log.warn("Placed a network tile that didn't produce a network node! At: dim=" + tileNetwork.getWorld().getDimension().getType().getId() + ", pos=" + tileNetwork.getPos());
        } else if (node.needsUpdate()) {
            StarlightUpdateHandler.getInstance().addNode(tileNetwork.getWorld(), node);
        }
    }

    public static void informNetworkTileRemoval(TileNetwork tileNetwork) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(tileNetwork.getWorld());

        IPrismTransmissionNode node = handler.getTransmissionNode(tileNetwork.getPos());
        if (node == null) {
            AstralSorcery.log.warn("Tried to get a network node at a TileEntity, but didn't find one! At: dim=" + tileNetwork.getWorld().getDimension().getType().getId() + ", pos=" + tileNetwork.getPos());
        } else {
            StarlightUpdateHandler.getInstance().removeNode(((IStarlightTransmission) tileNetwork).getTrWorld(), node);
        }

        if (tileNetwork instanceof IStarlightSource) {
            handler.removeSource((IStarlightSource) tileNetwork);
        } else if (tileNetwork instanceof IStarlightTransmission) {
            handler.removeTransmission((IStarlightTransmission) tileNetwork);
        } else {
            AstralSorcery.log.warn("Removed a network tile that's not transmission/receiver or source! At: dim=" + tileNetwork.getWorld().getDimension().getType().getId() + ", pos=" + tileNetwork.getPos());
        }
    }

}
