/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.data.research.ResearchSyncHelper;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<ResearchProgression> researchProgression = new ArrayList<>();
    public IMajorConstellation attunedConstellation = null;
    public Map<AbstractPerk, CompoundNBT> usedPerks = new HashMap<>();
    public List<String> freePointTokens = Lists.newArrayList();
    public List<AbstractPerk> sealedPerks = Lists.newArrayList();
    public boolean wasOnceAttuned = false;
    public int progressTier = 0;
    public double perkExp = 0;

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
        this.freePointTokens = progress.getFreePointTokens();
        this.usedPerks = progress.getUnlockedPerkData();
        this.sealedPerks = progress.getSealedPerks();
        this.perkExp = progress.getPerkExp();
        this.wasOnceAttuned = progress.wasOnceAttuned();
    }

    @Nonnull
    @Override
    public Encoder<PktSyncKnowledge> encoder() {
        return (packet, buffer) -> {
            buffer.writeByte(packet.state);

            ByteBufUtils.writeList(buffer, packet.knownConstellations, ByteBufUtils::writeResourceLocation);
            ByteBufUtils.writeList(buffer, packet.seenConstellations, ByteBufUtils::writeResourceLocation);
            ByteBufUtils.writeList(buffer, packet.researchProgression, ByteBufUtils::writeEnumValue);
            ByteBufUtils.writeOptional(buffer, packet.attunedConstellation, ByteBufUtils::writeRegistryEntry);
            ByteBufUtils.writeMap(buffer, packet.usedPerks, ByteBufUtils::writeRegistryEntry, ByteBufUtils::writeNBTTag);
            ByteBufUtils.writeList(buffer, packet.sealedPerks, ByteBufUtils::writeRegistryEntry);
            ByteBufUtils.writeList(buffer, packet.freePointTokens, ByteBufUtils::writeString);
            buffer.writeBoolean(packet.wasOnceAttuned);
            buffer.writeInt(packet.progressTier);
            buffer.writeDouble(packet.perkExp);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncKnowledge> decoder() {
        return buffer -> {
            PktSyncKnowledge pkt = new PktSyncKnowledge(buffer.readByte());

            pkt.knownConstellations = ByteBufUtils.readList(buffer, ByteBufUtils::readResourceLocation);
            pkt.seenConstellations = ByteBufUtils.readList(buffer, ByteBufUtils::readResourceLocation);
            pkt.researchProgression = ByteBufUtils.readList(buffer, buf -> ByteBufUtils.readEnumValue(buf, ResearchProgression.class));
            pkt.attunedConstellation = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);
            pkt.usedPerks = ByteBufUtils.readMap(buffer, ByteBufUtils::readRegistryEntry, ByteBufUtils::readNBTTag);
            pkt.sealedPerks = ByteBufUtils.readList(buffer, ByteBufUtils::readRegistryEntry);
            pkt.freePointTokens = ByteBufUtils.readList(buffer, ByteBufUtils::readString);
            pkt.wasOnceAttuned = buffer.readBoolean();
            pkt.progressTier = buffer.readInt();
            pkt.perkExp = buffer.readDouble();

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
