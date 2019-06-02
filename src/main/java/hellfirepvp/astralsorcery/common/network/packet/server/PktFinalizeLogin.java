/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktFinalizeLogin
 * Created by HellFirePvP
 * Date: 01.06.2019 / 19:15
 */
public class PktFinalizeLogin extends ASPacket<PktFinalizeLogin> {

    @Nonnull
    @Override
    public Encoder<PktFinalizeLogin> encoder() {
        return (pktFinalizeLogin, buffer) -> {};
    }

    @Nonnull
    @Override
    public Decoder<PktFinalizeLogin> decoder() {
        return buffer -> new PktFinalizeLogin();
    }

    @Nonnull
    @Override
    public Handler<PktFinalizeLogin> handler() {
        return new Handler<PktFinalizeLogin>() {

            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktFinalizeLogin packet, NetworkEvent.Context context) {
                ClientProxy.connected = true;
            }

            @Override
            public void handle(PktFinalizeLogin packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
