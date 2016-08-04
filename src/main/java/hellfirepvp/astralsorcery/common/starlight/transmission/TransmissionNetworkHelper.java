package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.tile.base.TileNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        return node != null && node.getSources().contains(end);
    }

    public static boolean canCreateTransmissionLink(IStarlightTransmission tr, BlockPos end) {
        IPrismTransmissionNode node = tr.getNode();
        if(node == null) return false;

        double dst = tr.getPos().getDistance(end.getX(), end.getY(), end.getZ());
        return dst <= MAX_TRANSMISSION_DIST;
    }

    public static void createTransmissionLink(IStarlightTransmission tr, BlockPos next) {
        IPrismTransmissionNode node = tr.getNode();
        if(node == null) {
            AstralSorcery.log.info("Trying to create transmission link on non-existing transmission tile! Not creating link!");
            return;
        }
        createLink(node, tr, next);
    }

    public static void removeTransmissionLink(IStarlightTransmission tr, BlockPos next) {
        IPrismTransmissionNode node = tr.getNode();
        if(node == null) {
            return;
        }
        removeLink(node, tr, next);
    }

    //Harsh reality methods. xP
    private static void removeLink(IPrismTransmissionNode transmissionNode, IStarlightTransmission transmission, BlockPos to) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(transmission.getWorld());
        IPrismTransmissionNode nextNode = handler.getTransmissionNode(to);
        removeLink(transmissionNode, nextNode, transmission.getWorld(), transmission.getPos(), to);
    }

    private static void removeLink(IPrismTransmissionNode thisNode, IPrismTransmissionNode nextNode, World world, BlockPos from, BlockPos to) {
        if(nextNode != null) {
            nextNode.notifySourceUnlink(world, from);
        }
        thisNode.notifyUnlink(world, to);
    }

    private static void createLink(IPrismTransmissionNode transmissionNode, IStarlightTransmission transmission, BlockPos to) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(transmission.getWorld());
        IPrismTransmissionNode nextNode = handler.getTransmissionNode(to);
        createLink(transmissionNode, nextNode, transmission.getWorld(), transmission.getPos(), to);
    }

    private static void createLink(IPrismTransmissionNode thisNode, IPrismTransmissionNode nextNode, World world, BlockPos from, BlockPos to) {
        if(nextNode != null) {
            nextNode.notifySourceLink(world, from);
        }
        thisNode.notifyLink(world, to);
    }

    public static void notifyBlockChange(World world, BlockPos pos) {
        WorldNetworkHandler.getNetworkHandler(world).informBlockChange(pos);
    }

    public static void informNetworkTilePlacement(TileNetwork tileNetwork) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(tileNetwork.getWorld());
        if(tileNetwork instanceof IStarlightSource) {
            handler.addNewSourceTile((IStarlightSource) tileNetwork);
        } else if(tileNetwork instanceof IStarlightTransmission) {
            handler.addTransmissionTile((IStarlightTransmission) tileNetwork);
        } else {
            AstralSorcery.log.warn("Placed a network tile that's not transmission or source! At: dim=" + tileNetwork.getWorld().provider.getDimension() + ", pos=" + tileNetwork.getPos());
        }
    }

    public static void informNetworkTileRemoval(TileNetwork tileNetwork) {
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(tileNetwork.getWorld());
        if(tileNetwork instanceof IStarlightSource) {
            handler.removeSource((IStarlightSource) tileNetwork);
        } else if(tileNetwork instanceof IStarlightTransmission) {
            handler.removeTransmission((IStarlightTransmission) tileNetwork);
        } else {
            AstralSorcery.log.warn("Removed a network tile that's not transmission or source! At: dim=" + tileNetwork.getWorld().provider.getDimension() + ", pos=" + tileNetwork.getPos());
        }
    }

}
