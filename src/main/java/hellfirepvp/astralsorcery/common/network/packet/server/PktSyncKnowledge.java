/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<String> seenConstellations = new ArrayList<>();
    public List<ResearchProgression> researchProgression = new ArrayList<>();
    public IMajorConstellation attunedConstellation = null;
    public Map<ConstellationPerk, Integer> appliedPerks = new HashMap<>();
    public int progressTier = 0;
    public boolean wasOnceAttuned = false;
    public double alignmentCharge = 0.0;

    public PktSyncKnowledge() {}

    public PktSyncKnowledge(byte state) {
        this.state = state;
    }

    public void load(PlayerProgress progress) {
        this.knownConstellations = progress.getKnownConstellations();
        this.seenConstellations = progress.getSeenConstellations();
        this.researchProgression = progress.getResearchProgression();
        this.progressTier = progress.getTierReached().ordinal();
        this.attunedConstellation = progress.getAttunedConstellation();
        this.appliedPerks = progress.getAppliedPerks();
        this.alignmentCharge = progress.getAlignmentCharge();
        this.wasOnceAttuned = progress.wasOnceAttuned();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.state = buf.readByte();

        int cLength = buf.readInt();
        if (cLength != -1) {
            knownConstellations = new ArrayList<>(cLength);
            for (int i = 0; i < cLength; i++) {
                String val = ByteBufUtils.readString(buf);
                knownConstellations.add(val);
            }
        } else {
            knownConstellations = new ArrayList<>();
        }

        cLength = buf.readInt();
        if (cLength != -1) {
            seenConstellations = new ArrayList<>(cLength);
            for (int i = 0; i < cLength; i++) {
                String val = ByteBufUtils.readString(buf);
                seenConstellations.add(val);
            }
        } else {
            seenConstellations = new ArrayList<>();
        }

        int rLength = buf.readInt();
        if (rLength != -1) {
            researchProgression = new ArrayList<>(rLength);
            for (int i = 0; i < rLength; i++) {
                researchProgression.add(ResearchProgression.getById(buf.readInt()));
            }
        } else {
            researchProgression = new ArrayList<>();
        }

        int attunementPresent = buf.readByte();
        if(attunementPresent != -1) {
            String attunement = ByteBufUtils.readString(buf);
            IConstellation c = ConstellationRegistry.getConstellationByName(attunement);
            if(c == null || !(c instanceof IMajorConstellation)) {
                AstralSorcery.log.warn("[AstralSorcery] received constellation-attunement progress-packet with unknown constellation: " + attunement);
            } else {
                this.attunedConstellation = (IMajorConstellation) c;
            }
        }

        int perkLength = buf.readInt();
        if(perkLength != -1) {
            this.appliedPerks = new HashMap<>(perkLength);
            for (int i = 0; i < perkLength; i++) {
                int id = buf.readInt();
                int lvl = buf.readInt();
                this.appliedPerks.put(ConstellationPerks.getById(id).getSingleInstance(), lvl);
            }
        } else {
            this.appliedPerks = new HashMap<>();
        }

        this.wasOnceAttuned = buf.readBoolean();
        this.progressTier = buf.readInt();
        this.alignmentCharge = buf.readDouble();
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

        if (seenConstellations != null) {
            buf.writeInt(seenConstellations.size());
            for (String dat : seenConstellations) {
                ByteBufUtils.writeString(buf, dat);
            }
        } else {
            buf.writeInt(-1);
        }

        if (researchProgression != null) {
            buf.writeInt(researchProgression.size());
            for (ResearchProgression progression : researchProgression) {
                buf.writeInt(progression.getProgressId());
            }
        } else {
            buf.writeInt(-1);
        }

        if(attunedConstellation != null) {
            buf.writeByte(1);
            ByteBufUtils.writeString(buf, attunedConstellation.getUnlocalizedName());
        } else {
            buf.writeByte(-1);
        }

        if(appliedPerks != null) {
            buf.writeInt(appliedPerks.size());
            for (ConstellationPerk perk : appliedPerks.keySet()) {
                buf.writeInt(perk.getId());
                buf.writeInt(appliedPerks.get(perk));
            }
        } else {
            buf.writeInt(-1);
        }

        buf.writeBoolean(this.wasOnceAttuned);
        buf.writeInt(this.progressTier);
        buf.writeDouble(this.alignmentCharge);
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
