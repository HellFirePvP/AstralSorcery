package hellfire.astralSorcery.common.util;

import io.netty.buffer.ByteBuf;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:51
 */
public class ByteBufUtils {

    public static void writeString(ByteBuf buf, String toWrite) {
        byte[] str = toWrite.getBytes();
        buf.writeInt(str.length);
        buf.writeBytes(str);
    }

    public static String readString(ByteBuf buf) {
        int length = buf.readInt();
        byte[] strBytes = new byte[length];
        buf.readBytes(strBytes, 0, length);
        return new String(strBytes);
    }

}
