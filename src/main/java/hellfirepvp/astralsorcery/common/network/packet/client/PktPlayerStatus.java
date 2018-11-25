/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.PlayerActivityManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayerStatus
 * Created by HellFirePvP
 * Date: 23.11.2018 / 18:11
 */
public class PktPlayerStatus implements IMessageHandler<PktPlayerStatus, IMessage>, IMessage {

    private UUID playerUUID;
    private boolean status;

    public PktPlayerStatus() {}

    public PktPlayerStatus(UUID playerUUID, boolean active) {
        this.playerUUID = playerUUID;
        this.status = active;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerUUID = ByteBufUtils.readUUID(buf);
        status = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUUID(buf, playerUUID);
        buf.writeBoolean(status);
    }

    @Override
    public IMessage onMessage(PktPlayerStatus pkt, MessageContext ctx) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            PlayerActivityManager.INSTANCE.setStatusServer(pkt.playerUUID, pkt.status);
        });
        return null;
    }
}
