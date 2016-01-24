package hellfire.astralSorcery.common.net.packet;

import hellfire.astralSorcery.common.AstralSorcery;
import hellfire.astralSorcery.common.config.Config;
import hellfire.astralSorcery.common.config.Sync;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:49
 */
public class PktSyncConfig implements IMessage, IMessageHandler<PktSyncConfig, IMessage> {

    public List<Tuple> fields = new ArrayList<Tuple>();

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readByte();
        fields = new ArrayList<Tuple>();

        for(int i = 0; i < count; i++) {
            byte[] data = new byte[buf.readShort()];
            buf.readBytes(data);

            ByteArrayInputStream in = new ByteArrayInputStream(data);
            Tuple tuple = null;
            String key = null;
            try {
                key = new DataInputStream(in).readUTF();
                Object value = new ObjectInputStream(in).readObject();
                tuple = new Tuple(key, value);
            } catch (Exception ignored) {}

            if(tuple == null) {
                fields = null;
                if(key != null) {
                    AstralSorcery.log.info("Could not read config from server with key: " + key);
                }
                break;
            }
            fields.set(i, tuple);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        List<byte[]> bufferedFieldContents = new ArrayList<byte[]>();
        for(Field f : Config.class.getFields()) {
            if(Modifier.isStatic(f.getModifiers()) && f.isAnnotationPresent(Sync.class)) {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                try {
                    new DataOutputStream(byteStream).writeUTF(f.getName());
                    new ObjectOutputStream(byteStream).writeObject(f.get(null));
                } catch (Exception ignored) {}
                bufferedFieldContents.add(byteStream.toByteArray());
                try {
                    byteStream.close();
                } catch (IOException ignored) {}
            }
        }

        buf.writeByte(bufferedFieldContents.size());
        for(byte[] data : bufferedFieldContents) {
            buf.writeShort(data.length);
            buf.writeBytes(data);
        }
    }

    @Override
    public IMessage onMessage(PktSyncConfig message, MessageContext ctx) {
        try {
            for(Tuple tuple : message.fields) {
                Field field = Config.class.getField(tuple.key);
                field.set(null, tuple.value);
            }
        } catch (Throwable ignored) {
            AstralSorcery.log.error("Could not apply config received from server!");
            throw new RuntimeException(ignored);
        }
        return null;
    }

    public static class Tuple {

        public final String key;
        public final Object value;

        public Tuple(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

}
