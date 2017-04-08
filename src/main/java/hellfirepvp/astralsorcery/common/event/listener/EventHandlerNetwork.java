/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncAlignmentLevels;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncConfig;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerNetwork
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:10
 */
public class EventHandlerNetwork {

    private static final String AS_WORLDHANDLERS_PAYLOAD = "AS|WH";

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        EntityPlayerMP p = (EntityPlayerMP) e.player;
        AstralSorcery.log.info("Synchronizing configuration to " + p.getName());
        PacketChannel.CHANNEL.sendTo(new PktSyncConfig(), p);
        PacketChannel.CHANNEL.sendTo(new PktSyncAlignmentLevels(ConstellationPerkLevelManager.levelsRequired), p);
        if(Mods.CRAFTTWEAKER.isPresent()) {
            PacketChannel.CHANNEL.sendTo(ModIntegrationCrafttweaker.compileRecipeChangePacket(), p);
        }

        ResearchManager.sendInitClientKnowledge(p);

        SyncDataHolder.syncAllDataTo(p);
    }

    @SubscribeEvent
    public void onLoginEarly(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        List<Integer> dimensions = ((DataWorldSkyHandlers) SyncDataHolder.getDataServer(SyncDataHolder.DATA_SKY_HANDLERS)).getSkyHandlerDimensions();
        buf.writeInt(dimensions.size());
        for (int i : dimensions) {
            buf.writeInt(i);
        }
        event.getManager().sendPacket(new SPacketCustomPayload(AS_WORLDHANDLERS_PAYLOAD, buf));
    }

    public static void clientCatchWorldHandlerPayload(SPacketCustomPayload pkt) {
        if(pkt.getChannelName().equals(AS_WORLDHANDLERS_PAYLOAD)) {
            PacketBuffer buf = pkt.getBufferData();
            int size = buf.readInt();
            List<Integer> dimensions = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                dimensions.add(buf.readInt());
            }

            ((DataWorldSkyHandlers) SyncDataHolder.getDataClient(SyncDataHolder.DATA_SKY_HANDLERS)).updateClient(dimensions);
        }
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        EntityPlayer player = e.player;

        PlayerChargeHandler.instance.informDisconnect(player);
        //ResearchManager.logoutResetClient(player);
    }

}
