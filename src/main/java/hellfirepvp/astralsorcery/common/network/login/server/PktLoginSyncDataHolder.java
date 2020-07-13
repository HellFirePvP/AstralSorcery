/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.login.server;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataRegistry;
import hellfirepvp.astralsorcery.common.data.sync.base.AbstractData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import hellfirepvp.astralsorcery.common.network.base.ASLoginPacket;
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
 * Class: PktLoginSyncDataHolder
 * Created by HellFirePvP
 * Date: 23.08.2019 / 21:43
 */
public class PktLoginSyncDataHolder extends ASLoginPacket<PktLoginSyncDataHolder> {

    private Map<ResourceLocation, CompoundNBT> syncData = new HashMap<>();

    public PktLoginSyncDataHolder() {}

    public static PktLoginSyncDataHolder makeLogin() {
        PktLoginSyncDataHolder pkt = new PktLoginSyncDataHolder();
        for (ResourceLocation key : SyncDataRegistry.getKnownKeys()) {
            SyncDataHolder.executeServer(key, AbstractData.class, data -> {
                CompoundNBT nbt = new CompoundNBT();
                data.writeAllDataToPacket(nbt);
                pkt.syncData.put(key, nbt);
            });
        }
        return pkt;
    }

    @Nonnull
    @Override
    public Encoder<PktLoginSyncDataHolder> encoder() {
        return (packet, buffer) -> {
            buffer.writeInt(packet.syncData.size());

            for (ResourceLocation key : packet.syncData.keySet()) {
                ByteBufUtils.writeResourceLocation(buffer, key);
                ByteBufUtils.writeNBTTag(buffer, packet.syncData.get(key));
            }
        };
    }

    @Nonnull
    @Override
    public Decoder<PktLoginSyncDataHolder> decoder() {
        return buffer -> {
            PktLoginSyncDataHolder pktData = new PktLoginSyncDataHolder();
            int size = buffer.readInt();

            for (int i = 0; i < size; i++) {
                ResourceLocation key = ByteBufUtils.readResourceLocation(buffer);
                CompoundNBT tag = ByteBufUtils.readNBTTag(buffer);
                pktData.syncData.put(key, tag);
            }
            return pktData;
        };
    }

    @Nonnull
    @Override
    public Handler<PktLoginSyncDataHolder> handler() {
        return new Handler<PktLoginSyncDataHolder>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktLoginSyncDataHolder packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    for (ResourceLocation key : packet.syncData.keySet()) {
                        ClientDataReader reader = SyncDataHolder.getReader(key);
                        if (reader != null) {
                            SyncDataHolder.executeClient(key, ClientData.class, data -> {
                                reader.readFromIncomingFullSync(data, packet.syncData.get(key));
                            });
                        }
                    }
                    acknowledge(context);
                });
            }

            @Override
            public void handle(PktLoginSyncDataHolder packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
