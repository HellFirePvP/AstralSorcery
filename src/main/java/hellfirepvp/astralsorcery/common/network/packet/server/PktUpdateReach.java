package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.constellation.perk.impl.PerkCreationReach;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUpdateReach
 * Created by HellFirePvP
 * Date: 23.12.2016 / 11:26
 */
public class PktUpdateReach implements IMessage, IMessageHandler<PktUpdateReach, IMessage> {

    public boolean apply = false;
    public float modifier = 0.0F;

    public PktUpdateReach() {}

    public PktUpdateReach(boolean apply, float modifier) {
        this.apply = apply;
        this.modifier = modifier;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.apply = buf.readBoolean();
        this.modifier = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(apply);
        buf.writeFloat(modifier);
    }

    @Override
    public IMessage onMessage(PktUpdateReach message, MessageContext ctx) {
        PerkCreationReach.updateReach(message.apply, message.modifier);
        return null;
    }
}
