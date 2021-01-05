/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.*;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncKnowledge
 * Created by HellFirePvP
 * Date: 02.06.2019 / 08:42
 */
public class PktSyncKnowledge extends ASPacket<PktSyncKnowledge> {

    public static final byte STATE_ADD = 0;
    public static final byte STATE_WIPE = 1;

    private byte state;
    public List<ResourceLocation> knownConstellations = new ArrayList<>();
    public List<ResourceLocation> seenConstellations = new ArrayList<>();
    public List<ResourceLocation> storedConstellationPapers = new ArrayList<>();
    public Collection<ResearchProgression> researchProgression = new ArrayList<>();
    public IMajorConstellation attunedConstellation = null;
    public boolean wasOnceAttuned = false;
    public int progressTier = 0;
    public boolean doPerkAbilities = true;
    public PlayerPerkData perkData = null;

    public PktSyncKnowledge() {}

    public PktSyncKnowledge(byte state) {
        this.state = state;
    }

    public void load(PlayerProgress progress) {
        this.knownConstellations = progress.getKnownConstellations();
        this.seenConstellations = progress.getSeenConstellations();
        this.storedConstellationPapers = progress.getStoredConstellationPapers();
        this.researchProgression = progress.getResearchProgression();
        this.progressTier = progress.getTierReached().ordinal();
        this.attunedConstellation = progress.getAttunedConstellation();
        this.perkData = progress.getPerkData();
        this.wasOnceAttuned = progress.wasOnceAttuned();
        this.doPerkAbilities = progress.doPerkAbilities();
    }

    @Nonnull
    @Override
    public Encoder<PktSyncKnowledge> encoder() {
        return (packet, buffer) -> {
            buffer.writeByte(packet.state);

            ByteBufUtils.writeOptional(buffer, packet.perkData, (buf, perkData) -> perkData.write(buf));
            ByteBufUtils.writeCollection(buffer, packet.knownConstellations, ByteBufUtils::writeResourceLocation);
            ByteBufUtils.writeCollection(buffer, packet.seenConstellations, ByteBufUtils::writeResourceLocation);
            ByteBufUtils.writeCollection(buffer, packet.storedConstellationPapers, ByteBufUtils::writeResourceLocation);
            ByteBufUtils.writeCollection(buffer, packet.researchProgression, ByteBufUtils::writeEnumValue);
            ByteBufUtils.writeOptional(buffer, packet.attunedConstellation, ByteBufUtils::writeRegistryEntry);
            buffer.writeBoolean(packet.wasOnceAttuned);
            buffer.writeInt(packet.progressTier);
            buffer.writeBoolean(packet.doPerkAbilities);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncKnowledge> decoder() {
        return buffer -> {
            PktSyncKnowledge pkt = new PktSyncKnowledge(buffer.readByte());

            pkt.perkData = ByteBufUtils.readOptional(buffer, buf -> PlayerPerkData.read(buf, LogicalSide.CLIENT));
            pkt.knownConstellations = ByteBufUtils.readList(buffer, ByteBufUtils::readResourceLocation);
            pkt.seenConstellations = ByteBufUtils.readList(buffer, ByteBufUtils::readResourceLocation);
            pkt.storedConstellationPapers = ByteBufUtils.readList(buffer, ByteBufUtils::readResourceLocation);
            pkt.researchProgression = ByteBufUtils.readList(buffer, buf -> ByteBufUtils.readEnumValue(buf, ResearchProgression.class));
            pkt.attunedConstellation = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);
            pkt.wasOnceAttuned = buffer.readBoolean();
            pkt.progressTier = buffer.readInt();
            pkt.doPerkAbilities = buffer.readBoolean();

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktSyncKnowledge> handler() {
        return new Handler<PktSyncKnowledge>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktSyncKnowledge packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player != null) {
                        if (packet.state == STATE_ADD) {
                            ResearchSyncHelper.recieveProgressFromServer(packet, player);
                        } else {
                            ResearchHelper.updateClientResearch(null);
                        }
                    }
                });
            }

            @Override
            public void handle(PktSyncKnowledge packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
