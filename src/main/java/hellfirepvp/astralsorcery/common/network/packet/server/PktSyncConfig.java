/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.Sync;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncConfig
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:13
 */
public class PktSyncConfig implements IMessage, IMessageHandler<PktSyncConfig, IMessage> {

    public List<SyncTuple> fields = new ArrayList<>();

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readByte();
        fields = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            fields.add(new SyncTuple(null, null)); //Empty init
        }

        for (int i = 0; i < count; i++) {
            byte[] data = new byte[buf.readShort()];
            buf.readBytes(data);

            ByteArrayInputStream in = new ByteArrayInputStream(data);
            SyncTuple tuple = null;
            String key = null;
            try {
                key = new DataInputStream(in).readUTF();
                Object value = new ObjectInputStream(in).readObject();
                tuple = new SyncTuple(key, value);
            } catch (Exception ignored) {
            }

            if (tuple == null) {
                fields = null;
                if (key != null) {
                    AstralSorcery.log.info("[AstralSorcery] Could not read config from server with key: " + key);
                }
                break;
            }
            fields.set(i, tuple);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        List<byte[]> bufferedFieldContents = new ArrayList<>();
        for (Field f : Config.class.getFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.isAnnotationPresent(Sync.class)) {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                try {
                    new DataOutputStream(byteStream).writeUTF(f.getName());
                    new ObjectOutputStream(byteStream).writeObject(f.get(null));
                } catch (Exception ignored) {
                }
                bufferedFieldContents.add(byteStream.toByteArray());
                try {
                    byteStream.close();
                } catch (IOException ignored) {
                }
            }
        }

        buf.writeByte(bufferedFieldContents.size());
        for (byte[] data : bufferedFieldContents) {
            buf.writeShort(data.length);
            buf.writeBytes(data);
        }
    }

    @Override
    public IMessage onMessage(PktSyncConfig message, MessageContext ctx) {
        try {
            Config.savedSyncTuples.clear();
            for (SyncTuple tuple : message.fields) {
                Field field = Config.class.getField(tuple.key);
                Config.savedSyncTuples.add(new SyncTuple(tuple.key, field.get(null)));
                field.set(null, tuple.value);
            }
        } catch (Throwable exc) {
            AstralSorcery.log.error("[AstralSorcery] Could not applyServer config received from server!");
            throw new RuntimeException(exc);
        }
        return null;
    }

    public static class SyncTuple extends Tuple<String, Object> {

        public SyncTuple(String key, Object value) {
            super(key, value);
        }

    }

}
