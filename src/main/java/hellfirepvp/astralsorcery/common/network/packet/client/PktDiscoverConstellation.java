package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktDiscoverConstellation
 * Created by HellFirePvP
 * Date: 12.05.2016 / 13:50
 */
public class PktDiscoverConstellation implements IMessage, IMessageHandler<PktDiscoverConstellation, IMessage> {

    private String discoveredConstellation;

    public PktDiscoverConstellation() {}

    public PktDiscoverConstellation(String discoveredConstellation) {
        this.discoveredConstellation = discoveredConstellation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        discoveredConstellation = ByteBufUtils.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, discoveredConstellation);
    }

    @Override
    public IMessage onMessage(PktDiscoverConstellation message, MessageContext ctx) {
        Constellation received = ConstellationRegistry.getConstellationByName(message.discoveredConstellation);
        if(received == null) {
            AstralSorcery.log.info("Received unknown constellation from client: " + message.discoveredConstellation);
        } else {
            ResearchManager.discoverConstellation(received, ctx.getServerHandler().playerEntity);
            ctx.getServerHandler().playerEntity.addChatMessage(
                    new TextComponentString("§aDiscovered constellation " +
                            I18n.translateToLocal(message.discoveredConstellation) + "!"));
        }
        return null;
    }

}
