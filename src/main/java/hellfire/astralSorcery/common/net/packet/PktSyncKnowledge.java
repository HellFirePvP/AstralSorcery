package hellfire.astralSorcery.common.net.packet;

import hellfire.astralSorcery.common.data.research.PlayerProgress;
import hellfire.astralSorcery.common.data.research.ResearchManager;
import hellfire.astralSorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 09.02.2016 11:45
 */
public class PktSyncKnowledge implements IMessage, IMessageHandler<PktSyncKnowledge, IMessage> {

    public static final byte STATE_ADD = 0;
    public static final byte STATE_WIPE = 1;

    private byte state;
    public List<String> knownConstellations = new ArrayList<String>();
    public int progressTier = 0;

    public PktSyncKnowledge() {}

    public PktSyncKnowledge(byte state) {
        this.state = state;
    }

    public void load(PlayerProgress progress) {
        this.knownConstellations = progress.getKnownConstellations();
        this.progressTier = progress.getTierReached();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.state = buf.readByte();

        int cLength = buf.readInt();
        if(cLength != -1) {
            knownConstellations = new ArrayList<String>();
            for (int i = 0; i < cLength; i++) {
                String val = ByteBufUtils.readString(buf);
                knownConstellations.add(val);
            }
        }
        this.progressTier = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(state);

        if(knownConstellations != null) {
            buf.writeInt(knownConstellations.size());
            for (String dat : knownConstellations) {
                ByteBufUtils.writeString(buf, dat);
            }
        } else {
            buf.writeInt(-1);
        }

        buf.writeInt(this.progressTier);
    }

    @Override
    public PktSyncKnowledge onMessage(PktSyncKnowledge message, MessageContext ctx) {
        switch (message.state) {
            case STATE_ADD:
                ResearchManager.recieveProgressFromServer(message);
                break;
            case STATE_WIPE:
                ResearchManager.clientProgress = new PlayerProgress();
                break;
        }
        return null;
    }

}
