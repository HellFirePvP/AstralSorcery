package hellfire.astralSorcery.common.net.packet;

import hellfire.astralSorcery.common.AstralSorcery;
import hellfire.astralSorcery.common.data.sync.AbstractData;
import hellfire.astralSorcery.common.data.sync.SyncDataHolder;
import hellfire.astralSorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 21:02
 */
public class PktSyncData implements IMessage, IMessageHandler<PktSyncData, IMessage> {

    private Map<String, AbstractData> data = new HashMap<String, AbstractData>();
    private boolean shouldSyncAll = false;

    public PktSyncData() {}

    public PktSyncData(Map<String, AbstractData> dataToSend, boolean shouldSyncAll) {
        this.data = dataToSend;
        this.shouldSyncAll = shouldSyncAll;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            String key = ByteBufUtils.readString(buf);

            byte providerId = buf.readByte();
            AbstractData.AbstractDataProvider<? extends AbstractData> provider = AbstractData.Registry.getProvider(providerId);
            if(provider == null) {
                AstralSorcery.log.warn("Provider for ID " + providerId + " doesn't exist! Skipping...");
                continue;
            }

            NBTTagCompound cmp;
            short compoundLength = buf.readShort();
            byte[] abyte = new byte[compoundLength];
            buf.readBytes(abyte);
            DataInputStream dataInput = null;
            try {
                dataInput = new DataInputStream(new ByteArrayInputStream(abyte));
                cmp = CompressedStreamTools.read(dataInput, new NBTSizeTracker(2097152L));
            } catch (IOException e) {
                AstralSorcery.log.warn("Provider Compound of " + providerId + " threw an IOException! Skipping...");
                AstralSorcery.log.warn("Exception message: " + e.getMessage());
                continue;
            } finally {
                try {
                    if(dataInput != null)
                        dataInput.close();
                } catch (Exception ignored) {}
            }

            AbstractData dat = provider.provideNewInstance();
            dat.readRawFromPacket(cmp);

            data.put(key, dat);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(data.size());

        for(String key : data.keySet()) {
            AbstractData dat = data.get(key);
            NBTTagCompound cmp = new NBTTagCompound();
            if(shouldSyncAll) {
                dat.writeAllDataToPacket(cmp);
            } else {
                dat.writeToPacket(cmp);
            }

            ByteBufUtils.writeString(buf, key);

            byte providerId = dat.getProviderID();
            buf.writeByte(providerId);

            byte[] abyte;
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                CompressedStreamTools.writeCompressed(cmp, output);
                abyte = output.toByteArray();
                //output.close(); unnecessary for ByteArrayOutputStreams. How do you want to close an array wtf...
            } catch (IOException e) {
                AstralSorcery.log.warn("Compressing the NBTTagCompound of " + providerId + " threw an IOException! Skipping...");
                AstralSorcery.log.warn("Exception message: " + e.getMessage());
                continue;
            }
            buf.writeShort((short) abyte.length);
            buf.writeBytes(abyte);
        }
    }

    @Override
    public IMessage onMessage(PktSyncData message, MessageContext ctx) {
        SyncDataHolder.receiveServerPacket(message.data);
        return null;
    }

}
