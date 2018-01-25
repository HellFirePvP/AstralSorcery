/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.AbstractData;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncData
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:12
 */
public class PktSyncData implements IMessage, IMessageHandler<PktSyncData, IMessage> {

    private Map<String, AbstractData> data = new HashMap<>();
    private boolean shouldSyncAll = false;

    public PktSyncData() {
    }

    public PktSyncData(Map<String, AbstractData> dataToSend, boolean shouldSyncAll) {
        this.data = dataToSend;
        this.shouldSyncAll = shouldSyncAll;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        int size = pb.readInt();

        for (int i = 0; i < size; i++) {
            String key = ByteBufUtils.readString(pb);

            byte providerId = pb.readByte();
            AbstractData.AbstractDataProvider<? extends AbstractData> provider = AbstractData.Registry.getProvider(providerId);
            if (provider == null) {
                AstralSorcery.log.warn("[AstralSorcery] Provider for ID " + providerId + " doesn't exist! Skipping...");
                continue;
            }

            NBTTagCompound cmp;
            try {
                cmp = pb.readCompoundTag();
            } catch (IOException e) {
                AstralSorcery.log.warn("[AstralSorcery] Provider Compound of " + providerId + " threw an IOException! Skipping...");
                AstralSorcery.log.warn("[AstralSorcery] Exception message: " + e.getMessage());
                continue;
            }

            AbstractData dat = provider.provideNewInstance();
            dat.readRawFromPacket(cmp);

            data.put(key, dat);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeInt(data.size());

        for (String key : data.keySet()) {
            AbstractData dat = data.get(key);
            NBTTagCompound cmp = new NBTTagCompound();
            if (shouldSyncAll) {
                dat.writeAllDataToPacket(cmp);
            } else {
                dat.writeToPacket(cmp);
            }

            ByteBufUtils.writeString(pb, key);

            byte providerId = dat.getProviderID();
            pb.writeByte(providerId);
            pb.writeCompoundTag(cmp);
        }
    }

    @Override
    public IMessage onMessage(PktSyncData message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> SyncDataHolder.receiveServerPacket(message.data));
        return null;
    }

}
