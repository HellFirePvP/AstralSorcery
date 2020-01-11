/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASIDataSerializers
 * Created by HellFirePvP
 * Date: 18.07.2017 / 23:46
 */
public class ASDataSerializers {

    public static IDataSerializer<Long> LONG = new IDataSerializer<Long>() {
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

    public static IDataSerializer<Vector3> VECTOR = new IDataSerializer<Vector3>() {
        @Override
        public void write(PacketBuffer buf, Vector3 value) {
            buf.writeDouble(value.getX());
            buf.writeDouble(value.getY());
            buf.writeDouble(value.getZ());
        }

        @Override
        public Vector3 read(PacketBuffer buf) {
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

    public static IDataSerializer<FluidStack> FLUID = new IDataSerializer<FluidStack>() {
        @Override
        public void write(PacketBuffer buf, FluidStack value) {
            ByteBufUtils.writeFluidStack(buf, value);
        }

        @Override
        public FluidStack read(PacketBuffer buf) {
            return ByteBufUtils.readFluidStack(buf);
        }

        @Override
        public DataParameter<FluidStack> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        @Override
        public FluidStack copyValue(FluidStack value) {
            return value.copy();
        }
    };

}
