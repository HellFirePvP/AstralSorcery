/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionChain;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionWorldHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.tile.base.TileNetwork;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightNetworkDebugHandler
 * Created by HellFirePvP
 * Date: 20.05.2018 / 17:52
 */
public class StarlightNetworkDebugHandler implements ITickHandler {

    public static final StarlightNetworkDebugHandler INSTANCE = new StarlightNetworkDebugHandler();

    private Map<UUID, Tuple<Integer, Runnable>> playerAwaitingDebugMode = new HashMap<>();

    private StarlightNetworkDebugHandler() {}

    public void awaitDebugInteraction(EntityPlayer player, Runnable timeoutRunnable) {
        playerAwaitingDebugMode.put(player.getUniqueID(), new Tuple<>(400, timeoutRunnable));
    }

    public boolean beginDebugFor(World world, BlockPos pos, EntityPlayer player) {
        if(!playerAwaitingDebugMode.containsKey(player.getUniqueID())) {
            return false;
        }

        WorldNetworkHandler wnh = WorldNetworkHandler.getNetworkHandler(world);
        TransmissionWorldHandler twh = StarlightTransmissionHandler.getInstance().getWorldHandler(world);
        TileEntity te = MiscUtils.getTileAt(world, pos, TileEntity.class, false);
        IPrismTransmissionNode tr = wnh.getTransmissionNode(pos);

        player.sendMessage(new TextComponentString("§aPrinting debug for..."));
        player.sendMessage(new TextComponentString("§aWorld-ID:§c " + world.provider.getDimension()));
        player.sendMessage(new TextComponentString("§aPos:§c " + pos.toString()));
        player.sendMessage(new TextComponentString("§aTile found:§c " + (te == null ? "null" : te.getClass().getName())));

        if(twh == null) {
            player.sendMessage(new TextComponentString("§cWorld is missing a starlight-transmission handler! Is this world not ticking?"));
        }

        if(te != null) {
            player.sendMessage(new TextComponentString("§aIs Network-Tile:§c " + (te instanceof TileNetwork)));
            player.sendMessage(new TextComponentString("§aIs Starlight-Transmission-Tile:§c " + (te instanceof IStarlightTransmission)));
        }
        player.sendMessage(new TextComponentString("§aIs Transmission-Node present:§c " + (tr != null)));
        if(tr != null) {
            player.sendMessage(new TextComponentString("§aFull Transmission-Node class:§c " + tr.getClass().getName()));
            player.sendMessage(new TextComponentString("§aInternal Transmission-Node position:§c " + tr.getLocationPos().toString()));

            List<BlockPos> sources = tr.getSources();
            player.sendMessage(new TextComponentString("§aTransmission-Node Network-Source-Positions:"));
            if(sources.isEmpty()) {
                player.sendMessage(new TextComponentString("§cNONE"));
            }
            for (BlockPos sPos : sources) {
                player.sendMessage(new TextComponentString("§c" + sPos.toString()));
            }
            List<NodeConnection<IPrismTransmissionNode>> next = tr.queryNext(wnh);
            player.sendMessage(new TextComponentString("§aTransmission-Node next links:"));
            if(next.isEmpty()) {
                player.sendMessage(new TextComponentString("§cNONE"));
            }
            for (NodeConnection<IPrismTransmissionNode> nextNode : next) {
                player.sendMessage(new TextComponentString("§c" + nextNode.getTo() + "§a - canSee/connected:§c " + nextNode.canConnect()));
            }

            if(tr instanceof ITransmissionSource) {
                IIndependentStarlightSource source = wnh.getSourceAt(tr.getLocationPos());
                if(source != null) {
                    player.sendMessage(new TextComponentString("§aFound starlight source:§c " + source.getClass().getName()));

                    if(twh != null) {
                        TransmissionChain chain = twh.getSourceChain(source);
                        if(chain == null) {
                            player.sendMessage(new TextComponentString("§cStarlight source does not have a transmission chain!"));
                        } else {
                            player.sendMessage(new TextComponentString("§aAmount of nodes this source provides starlight for:§c " + chain.getEndpointsNodes().size()));
                            player.sendMessage(new TextComponentString("§aAmount of normal blocks this source provides starlight for:§c " + chain.getUncheckedEndpointsBlock().size()));
                            player.sendMessage(new TextComponentString("§aInvolved chunks in this transmission-chain:§c " + chain.getInvolvedChunks().size()));
                        }
                    }
                } else {
                    player.sendMessage(new TextComponentString("§cTransmission-Source-Node is missing starlight source!"));
                }
            }
        }
        if(twh != null) {
            Collection<TransmissionChain> chains = twh.getTransmissionChains();
            for (TransmissionChain ch : chains) {
                if (ch.getUncheckedEndpointsBlock().contains(pos)) {
                    player.sendMessage(new TextComponentString("§aFound TransmissionChain transmitting starlight to this block from " + (ch.getSourceNode() == null ? "null" : ch.getSourceNode().getLocationPos().toString()) + "!"));
                }
            }
        }

        playerAwaitingDebugMode.remove(player.getUniqueID());
        return true;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        for (UUID plUUID : playerAwaitingDebugMode.keySet()) {
            Tuple<Integer, Runnable> cd = playerAwaitingDebugMode.get(plUUID);
            cd = new Tuple<>(cd.getFirst() - 1, cd.getSecond());
            if(cd.getFirst() <= 0) {
                playerAwaitingDebugMode.remove(plUUID);
                cd.getSecond().run();
            } else {
                playerAwaitingDebugMode.put(plUUID, cd);
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Starlight Network Debug Handler";
    }
}
