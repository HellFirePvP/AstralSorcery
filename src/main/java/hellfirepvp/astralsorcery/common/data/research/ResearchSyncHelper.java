/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchSyncHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 19:20
 */
public class ResearchSyncHelper {

    public static void pushProgressToClientUnsafe(PlayerProgress progress, EntityPlayerMP p) {
        PktSyncKnowledge pkt = new PktSyncKnowledge(PktSyncKnowledge.STATE_ADD);
        pkt.load(progress);
        PacketChannel.CHANNEL.sendTo(pkt, p);
    }

    @OnlyIn(Dist.CLIENT)
    public static void recieveProgressFromServer(PktSyncKnowledge message, EntityPlayer player) {
        int currentLvl = ResearchHelper.clientProgress == null ? 0 : ResearchHelper.clientProgress.getPerkLevel(player);
        ResearchHelper.clientProgress = new PlayerProgress();
        ResearchHelper.clientProgress.receive(message);
        ResearchHelper.clientInitialized = true;
        if (ResearchHelper.clientProgress.getPerkLevel(player) > currentLvl) {
            ClientRenderEventHandler.requestPermChargeReveal(160);
        }
    }

}
