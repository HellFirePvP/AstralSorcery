/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncData
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:21
 */
public class PktSyncData extends ASPacket<PktSyncData> {

    private Map<ResourceLocation, CompoundNBT> diffData = new HashMap<>();

    public PktSyncData() {}

    public PktSyncData(Map<ResourceLocation, CompoundNBT> dataToSend) {
        this.diffData = dataToSend;
    }

    @Nonnull
    @Override
    public Encoder<PktSyncData> encoder() {
        return (packet, buffer) -> {
            buffer.writeInt(packet.diffData.size());

            for (ResourceLocation key : packet.diffData.keySet()) {
                ByteBufUtils.writeResourceLocation(buffer, key);
                ByteBufUtils.writeNBTTag(buffer, packet.diffData.get(key));
            }
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncData> decoder() {
        return buffer -> {
            PktSyncData pktData = new PktSyncData();
            int size = buffer.readInt();

            for (int i = 0; i < size; i++) {
                ResourceLocation key = ByteBufUtils.readResourceLocation(buffer);
                CompoundNBT tag = ByteBufUtils.readNBTTag(buffer);
                pktData.diffData.put(key, tag);
            }
            return pktData;
        };
    }

    @Nonnull
    @Override
    public Handler<PktSyncData> handler() {
        return new Handler<PktSyncData>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktSyncData packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    for (ResourceLocation key : packet.diffData.keySet()) {
                        ClientDataReader reader = SyncDataHolder.getReader(key);
                        if (reader != null) {
                            SyncDataHolder.executeClient(key, ClientData.class,
                                    data -> reader.readFromIncomingDiff(data, packet.diffData.get(key)));
                        }
                    }
                });
            }

            @Override
            public void handle(PktSyncData packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
