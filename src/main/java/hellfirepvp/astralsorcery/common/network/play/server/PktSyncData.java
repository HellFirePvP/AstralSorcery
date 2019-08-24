/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.sync.AbstractData;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
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
 * Class: PktSyncData
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:21
 */
public class PktSyncData extends ASPacket<PktSyncData> {

    private Map<String, AbstractData> data = new HashMap<>();

    public PktSyncData() {}

    public PktSyncData(Map<String, AbstractData> dataToSend) {
        this.data = dataToSend;
    }

    @Nonnull
    @Override
    public Encoder<PktSyncData> encoder() {
        return (packet, buffer) -> {
            buffer.writeInt(packet.data.size());

            for (String key : packet.data.keySet()) {
                AbstractData aData = packet.data.get(key);
                CompoundNBT tag = new CompoundNBT();
                aData.writeToPacket(tag);

                ByteBufUtils.writeString(buffer, key);
                buffer.writeByte(aData.getProviderID());
                ByteBufUtils.writeNBTTag(buffer, tag);
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
    public Handler<PktSyncData> handler() {
        return new Handler<PktSyncData>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktSyncData packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> SyncDataHolder.receiveServerPacket(packet.data));
            }

            @Override
            public void handle(PktSyncData packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
