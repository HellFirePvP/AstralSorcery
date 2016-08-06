package hellfirepvp.astralsorcery.common.util.nbt;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NBTUtils
 * Created by HellFirePvP
 * Date: 03.08.2016 / 17:24
 */
public class NBTUtils {

    public static void writeBlockPosToNBT(BlockPos pos, NBTTagCompound compound) {
        compound.setInteger("bposX", pos.getX());
        compound.setInteger("bposY", pos.getY());
        compound.setInteger("bposZ", pos.getZ());
    }

    public static BlockPos readBlockPosFromNBT(NBTTagCompound compound) {
        int x = compound.getInteger("bposX");
        int y = compound.getInteger("bposY");
        int z = compound.getInteger("bposZ");
        return new BlockPos(x, y, z);
    }

    public static byte[] packBlockPos(BlockPos pos) {
        byte[] posArray = new byte[10]; //4 * 2 + 2 (y)
        System.arraycopy(Ints.toByteArray(pos.getX()), 0, posArray, 0, 4);
        System.arraycopy(Shorts.toByteArray((short) (pos.getY() & 255)), 0, posArray, 4, 2);
        System.arraycopy(Ints.toByteArray(pos.getZ()), 0, posArray, 6, 4);
        return posArray;
    }

    public static BlockPos unpackBlockPos(byte[] packed) {
        if(packed.length != 10) throw new IllegalArgumentException("Trying to unpack illegal blockPos byteArray!");
        return new BlockPos(
                Ints.fromBytes(packed[0], packed[1], packed[2], packed[3]),
                Shorts.fromBytes(packed[4], packed[5]),
                Ints.fromBytes(packed[6], packed[7], packed[8], packed[9]));
    }

}
