/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.login.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.sync.AbstractData;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.network.base.ASLoginPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.nbt.CompoundNBT;
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

    private Map<String, AbstractData> data = new HashMap<>();

    public PktLoginSyncDataHolder() {}

    public static PktLoginSyncDataHolder makeLogin() {
        PktLoginSyncDataHolder pkt = new PktLoginSyncDataHolder();
        pkt.data = SyncDataHolder.getSyncServerData();
        return pkt;
    }

    @Nonnull
    @Override
    public Encoder<PktLoginSyncDataHolder> encoder() {
        return (packet, buffer) -> {
            buffer.writeInt(packet.data.size());

            for (String key : packet.data.keySet()) {
                AbstractData aData = packet.data.get(key);
                CompoundNBT tag = new CompoundNBT();
                aData.writeAllDataToPacket(tag);

                ByteBufUtils.writeString(buffer, key);
                buffer.writeByte(aData.getProviderID());
                ByteBufUtils.writeNBTTag(buffer, tag);
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
                String key = ByteBufUtils.readString(buffer);
                byte providerId = buffer.readByte();
                CompoundNBT tag = ByteBufUtils.readNBTTag(buffer);

                AbstractData.AbstractDataProvider<? extends AbstractData> provider = AbstractData.Registry.getProvider(providerId);
                if (provider == null) {
                    AstralSorcery.log.warn("Provider for ID " + providerId + " doesn't exist! Skipping...");
                    continue;
                }

                AbstractData dat = provider.provideNewInstance(Dist.CLIENT);
                dat.readRawFromPacket(tag);

                pktData.data.put(key, dat);
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
                    SyncDataHolder.receiveServerPacket(packet.data);
                    acknowledge(context);
                });
            }

            @Override
            public void handle(PktLoginSyncDataHolder packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
