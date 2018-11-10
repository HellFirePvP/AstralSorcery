/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.item.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.network.packet.ClientReplyPacket;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktClearBlockStorageStack
 * Created by HellFirePvP
 * Date: 30.04.2018 / 15:30
 */
public class PktClearBlockStorageStack implements IMessage, IMessageHandler<PktClearBlockStorageStack, IMessage>, ClientReplyPacket {

    public PktClearBlockStorageStack() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(PktClearBlockStorageStack message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleDelayed(() -> ItemBlockStorage.tryClearContainerFor(ctx.getServerHandler().player));
        return null;
    }

}
