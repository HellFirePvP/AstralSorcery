/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktElytraCapeState
 * Created by HellFirePvP
 * Date: 15.10.2017 / 21:33
 */
public class PktElytraCapeState implements IMessageHandler<PktElytraCapeState, IMessage>, IMessage {

    private byte type = 0;

    public PktElytraCapeState() {}

    public static PktElytraCapeState resetFallDistance() {
        PktElytraCapeState st = new PktElytraCapeState();
        st.type = 0;
        return st;
    }

    public static PktElytraCapeState setFlying() {
        PktElytraCapeState st = new PktElytraCapeState();
        st.type = 1;
        return st;
    }

    public static PktElytraCapeState resetFlying() {
        PktElytraCapeState st = new PktElytraCapeState();
        st.type = 2;
        return st;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.type);
    }

    @Override
    public IMessage onMessage(PktElytraCapeState message, MessageContext ctx) {
        EntityPlayer pl = ctx.getServerHandler().player;
        switch (message.type) {
            case 0: {
                pl.fallDistance = 0F;
                break;
            }
            case 1: {
                pl.setFlag(7, true);
                break;
            }
            case 2: {
                pl.setFlag(7, false);
                break;
            }
        }
        return null;
    }
}
