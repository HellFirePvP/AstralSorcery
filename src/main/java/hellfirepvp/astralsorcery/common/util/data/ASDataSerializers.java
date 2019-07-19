/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;

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

    public static IDataSerializer<CompatFluidStack> FLUID = new IDataSerializer<CompatFluidStack>() {
        @Override
        public void write(PacketBuffer buf, CompatFluidStack value) {
            buf.writeBoolean(value != null);
            if (value != null) {
                ByteBufUtils.writeFluidStack(buf, value);
            }
        }

        @Override
        public CompatFluidStack read(PacketBuffer buf) {
            return buf.readBoolean() ? ByteBufUtils.readFluidStack(buf) : null;
        }

        @Override
        public DataParameter<CompatFluidStack> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        @Override
        public CompatFluidStack copyValue(CompatFluidStack value) {
            return value == null ? null : value.copy();
        }
    };

}
