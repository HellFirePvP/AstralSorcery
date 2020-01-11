/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.channel;

import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BufferedReplyChannel
 * Created by HellFirePvP
 * Date: 21.04.2019 / 19:44
 */
public class BufferedReplyChannel extends SimpleSendChannel {

    public BufferedReplyChannel(SimpleChannel channel) {
        super(channel);
    }

    //Re-add when necessary. hopefully never.
    //@Override
    //public <MSG> void sendToServer(MSG message) {
    //    if (message instanceof ClientReplyPacket && !PacketChannel.canBeSentToServer()) {
    //        return;
    //    }
    //    super.sendToServer(message);
    //}

}
