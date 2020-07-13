/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ByteBufUtils
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:13
 */
public class ByteBufUtils {

    @Nullable
    public static <T> T readOptional(PacketBuffer buf, Function<PacketBuffer, T> readFct) {
        if (buf.readBoolean()) {
            return readFct.apply(buf);
        }
        return null;
    }

    public static <T> void writeOptional(PacketBuffer buf, @Nullable T object, BiConsumer<PacketBuffer, T> applyFct) {
        buf.writeBoolean(object != null);
        if (object != null) {
            applyFct.accept(buf, object);
        }
    }

    public static void writeUUID(PacketBuffer buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(PacketBuffer buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static <T> void writeList(PacketBuffer buf, @Nullable Collection<T> list, BiConsumer<PacketBuffer, T> iterationFct) {
        if (list != null) {
            buf.writeInt(list.size());
            list.forEach(e -> iterationFct.accept(buf, e));
        } else {
            buf.writeInt(-1);
        }
    }

    @Nullable
    public static <T> List<T> readList(PacketBuffer buf, Function<PacketBuffer, T> readFct) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        }
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(readFct.apply(buf));
        }
        return list;
    }

    public static <K, V> void writeMap(PacketBuffer buf,
                                       @Nullable Map<K, V> map,
                                       BiConsumer<PacketBuffer, K> keySerializer,
                                       BiConsumer<PacketBuffer, V> valueSerializer) {
        if (map != null) {
            buf.writeInt(map.size());
            for (Map.Entry<K, V> entry : map.entrySet()) {
                keySerializer.accept(buf, entry.getKey());
                valueSerializer.accept(buf, entry.getValue());
            }
        } else {
            buf.writeInt(-1);
        }
    }

    @Nullable
    public static <K, V> Map<K, V> readMap(PacketBuffer buf,
                                           Function<PacketBuffer, K> readKey,
                                           Function<PacketBuffer, V> readValue) {
        int size = buf.readInt();
        if (size == -1) {
            return null;
        }
        Map<K, V> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(readKey.apply(buf), readValue.apply(buf));
        }
        return map;
    }

    public static void writeString(PacketBuffer buf, String toWrite) {
        byte[] str = toWrite.getBytes(Charset.forName("UTF-8"));
        buf.writeInt(str.length);
        buf.writeBytes(str);
    }

    public static String readString(PacketBuffer buf) {
        int length = buf.readInt();
        byte[] strBytes = new byte[length];
        buf.readBytes(strBytes, 0, length);
        return new String(strBytes, Charset.forName("UTF-8"));
    }

    public static <T> void writeRegistryEntry(PacketBuffer buf, IForgeRegistryEntry<T> entry) {
        if (entry instanceof DimensionType) {
            buf.writeInt(1);
            writeResourceLocation(buf, DimensionType.getKey((DimensionType) entry));
        } else {
            buf.writeInt(0);
            writeResourceLocation(buf, entry.getRegistryName());
            writeResourceLocation(buf, RegistryManager.ACTIVE.getRegistry(entry.getRegistryType()).getRegistryName());
        }
    }

    public static <T> T readRegistryEntry(PacketBuffer buf) {
        int type = buf.readInt();
        if (type == 1) {
            ResourceLocation entryName = readResourceLocation(buf);
            return (T) DimensionType.byName(entryName);
        } else {
            ResourceLocation entryName = readResourceLocation(buf);
            ResourceLocation registryName = readResourceLocation(buf);
            return (T) RegistryManager.ACTIVE.getRegistry(registryName).getValue(entryName);
        }
    }

    public static void writeResourceLocation(PacketBuffer buf, ResourceLocation key) {
        writeString(buf, key.toString());
    }

    public static ResourceLocation readResourceLocation(PacketBuffer buf) {
        return new ResourceLocation(readString(buf));
    }

    public static void writeNumber(PacketBuffer buf, Number nbr) {
        long sNumber = nbr.longValue();
        if (nbr instanceof Float) {
            sNumber = Float.floatToRawIntBits(nbr.floatValue());
        } else if (nbr instanceof Double) {
            sNumber = Double.doubleToRawLongBits(nbr.doubleValue());
        }
        buf.writeLong(sNumber);
    }

    public static long readNumber(PacketBuffer buf) {
        return buf.readLong();
    }

    public static <T extends Enum<T>> void writeEnumValue(PacketBuffer buf, T value) {
        buf.writeInt(value.ordinal());
    }

    public static <T extends Enum<T>> T readEnumValue(PacketBuffer buf, Class<T> enumClazz) {
        if (!enumClazz.isEnum()) {
            throw new IllegalArgumentException("Passed class is not an enum!");
        }
        return enumClazz.getEnumConstants()[buf.readInt()];
    }

    public static void writePos(PacketBuffer buf, BlockPos pos) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static BlockPos readPos(PacketBuffer buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        return new BlockPos(x, y, z);
    }

    public static void writeVector(PacketBuffer buf, Vector3 vec) {
        buf.writeDouble(vec.getX());
        buf.writeDouble(vec.getY());
        buf.writeDouble(vec.getZ());
    }

    public static Vector3 readVector(PacketBuffer buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vector3(x, y, z);
    }

    public static void writeItemStack(PacketBuffer byteBuf, @Nonnull ItemStack stack) {
        boolean defined = !stack.isEmpty();
        byteBuf.writeBoolean(defined);
        if (defined) {
            CompoundNBT tag = new CompoundNBT();
            stack.write(tag);
            writeNBTTag(byteBuf, tag);
        }
    }

    @Nonnull
    public static ItemStack readItemStack(PacketBuffer byteBuf) {
        boolean defined = byteBuf.readBoolean();
        if (defined) {
            return ItemStack.read(readNBTTag(byteBuf));
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static void writeBlockState(PacketBuffer byteBuf, @Nonnull BlockState state) {
        ByteBufUtils.writeRegistryEntry(byteBuf, state.getBlock());

        Collection<IProperty<?>> properties = state.getProperties();
        byteBuf.writeInt(properties.size());
        for (IProperty prop : properties) {
            ByteBufUtils.writeString(byteBuf, prop.getName());
            ByteBufUtils.writeString(byteBuf, prop.getName(state.get(prop)));
        }
    }

    public static <T extends Comparable<T>> BlockState readBlockState(PacketBuffer byteBuf) {
        Block block = ByteBufUtils.readRegistryEntry(byteBuf);
        BlockState state = block.getDefaultState();

        int properties = byteBuf.readInt();
        for (int i = 0; i < properties; i++) {
            String propName = ByteBufUtils.readString(byteBuf);
            String valueStr = ByteBufUtils.readString(byteBuf);
            IProperty<T> property = (IProperty<T>) MiscUtils.iterativeSearch(state.getProperties(), prop -> prop.getName().equalsIgnoreCase(propName));
            if (property != null) {
                Optional<T> value = property.parseValue(valueStr);
                if (value.isPresent()) {
                    state = state.with(property, value.get());
                }
            }
        }
        return state;
    }

    public static void writeFluidStack(PacketBuffer byteBuf, @Nonnull FluidStack stack) {
        stack.writeToPacket(byteBuf);
    }

    @Nonnull
    public static FluidStack readFluidStack(PacketBuffer byteBuf) {
        return FluidStack.readFromPacket(byteBuf);
    }

    public static void writeNBTTag(PacketBuffer byteBuf, @Nonnull CompoundNBT tag) {
        try (DataOutputStream dos = new DataOutputStream(new ByteBufOutputStream(byteBuf))) {
            CompressedStreamTools.write(tag, dos);
        } catch (Exception exc) {}
    }

    @Nonnull
    public static CompoundNBT readNBTTag(PacketBuffer byteBuf) {
        try (DataInputStream dis = new DataInputStream(new ByteBufInputStream(byteBuf))) {
            return CompressedStreamTools.read(dis);
        } catch (Exception exc) {}
        throw new IllegalStateException("Could not load NBT Tag from incoming byte buffer!");
    }

}
