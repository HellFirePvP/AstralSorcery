/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASDataSerializers
 * Created by HellFirePvP
 * Date: 18.07.2017 / 23:46
 */
public class ASDataSerializers {

    public static DataSerializer<Long> LONG = new DataSerializer<Long>() {
        @Override
        public void write(PacketBuffer buf, Long value) {
            buf.writeLongLE(value);
        }

        @Override
        public Long read(PacketBuffer buf) {
            return buf.readLongLE();
        }

        @Override
        public DataParameter<Long> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        @Override
        public Long copyValue(Long value) {
            return new Long(value);
        }
    };

    public static DataSerializer<Vector3> VECTOR = new DataSerializer<Vector3>() {
        @Override
        public void write(PacketBuffer buf, Vector3 value) {
            buf.writeDouble(value.getX());
            buf.writeDouble(value.getY());
            buf.writeDouble(value.getZ());
        }

        @Override
        public Vector3 read(PacketBuffer buf) throws IOException {
            return new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public DataParameter<Vector3> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        @Override
        public Vector3 copyValue(Vector3 value) {
            return value.clone();
        }
    };

    public static DataSerializer<FluidStack> FLUID = new DataSerializer<FluidStack>() {
        @Override
        public void write(PacketBuffer buf, FluidStack value) {
            buf.writeBoolean(value != null);
            if (value != null) {
                ByteBufUtils.writeFluidStack(buf, value);
            }
        }

        @Override
        public FluidStack read(PacketBuffer buf) throws IOException {
            return buf.readBoolean() ? ByteBufUtils.readFluidStack(buf) : null;
        }

        @Override
        public DataParameter<FluidStack> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        @Override
        public FluidStack copyValue(FluidStack value) {
            return value == null ? null : value.copy();
        }
    };

    public static void registerSerializers() {
        DataSerializers.registerSerializer(VECTOR);
        DataSerializers.registerSerializer(FLUID);
        DataSerializers.registerSerializer(LONG);
    }

}
