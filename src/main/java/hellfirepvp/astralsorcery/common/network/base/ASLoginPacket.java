/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.base;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.login.client.PktLoginAcknowledge;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.IntSupplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASLoginPacket
 * Created by HellFirePvP
 * Date: 24.08.2019 / 13:01
 */
public abstract class ASLoginPacket<T extends ASLoginPacket<T>> extends ASPacket<T> implements IntSupplier {

    private int loginIndex;

    public int getLoginIndex() {
        return loginIndex;
    }

    @Override
    public int getAsInt() {
        return this.getLoginIndex();
    }

    public void setLoginIndex(int loginIndex) {
        this.loginIndex = loginIndex;
    }

    protected final void acknowledge(NetworkEvent.Context ctx) {
        PacketChannel.CHANNEL.reply(new PktLoginAcknowledge(), ctx);
    }

}
