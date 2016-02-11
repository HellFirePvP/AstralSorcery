package hellfire.astralSorcery.common.event;

import hellfire.astralSorcery.common.data.research.ResearchManager;
import hellfire.astralSorcery.common.data.sync.SyncDataHolder;
import hellfire.astralSorcery.common.net.PacketChannel;
import hellfire.astralSorcery.common.net.packet.PktSyncConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 21:34
 */
public class EventHandlerNetwork {

    public static int serverTick = 0;

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        EntityPlayerMP p = (EntityPlayerMP) e.player;
        PacketChannel.CHANNEL.sendTo(new PktSyncConfig(), p);

        ResearchManager.sendInitClientKnowledge(p);

        SyncDataHolder.syncAllDataTo(p);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        EntityPlayer player = e.player;

        ResearchManager.wipeClientKnowledge(player);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        serverTick++;

        SyncDataHolder.doNecessaryUpdates();
    }
}
