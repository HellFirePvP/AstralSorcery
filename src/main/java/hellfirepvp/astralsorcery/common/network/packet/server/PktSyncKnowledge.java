package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncKnowledge
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:34
 */
public class PktSyncKnowledge implements IMessage, IMessageHandler<PktSyncKnowledge, IMessage> {

    public static final byte STATE_ADD = 0;
    public static final byte STATE_WIPE = 1;

    private byte state;
    public List<String> knownConstellations = new ArrayList<>();
    public int progressTier = 0;

    public PktSyncKnowledge() {
    }

    public PktSyncKnowledge(byte state) {
        this.state = state;
    }

    public void load(PlayerProgress progress) {
        this.knownConstellations = progress.getKnownConstellations();
        this.progressTier = progress.getTierReached().ordinal();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.state = buf.readByte();

        int cLength = buf.readInt();
        if (cLength != -1) {
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

        if (knownConstellations != null) {
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
