/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ColumnPos
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ColumnPos(int x, int z) {

    public static final Codec<ColumnPos> CODEC = Codec.INT_STREAM.comapFlatMap(
            intStream -> Util.fixedSize(intStream, 2).map(ints -> new ColumnPos(ints[0], ints[1])),
            pos -> IntStream.of(pos.x(), pos.z()));

    public static final StreamCodec<ByteBuf, ColumnPos> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ColumnPos::x,
            ByteBufCodecs.INT,
            ColumnPos::z,
            ColumnPos::new);

    public BlockPos toBlockPos(int y) {
        return new BlockPos(this.x(), y, this.z());
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(this.toBlockPos(0));
    }

    public static ColumnPos of(BlockPos pos) {
        return new ColumnPos(pos.getX(), pos.getZ());
    }

    public double distSqr(Vec3 vector) {
        return this.distToLowCornerSqr(vector.x(), vector.z());
    }

    public double distSqr(Vec3i vector) {
        return this.distToLowCornerSqr(vector.getX(), vector.getZ());
    }

    public double distSqr(ColumnPos pos) {
        return this.distToLowCornerSqr(pos.x(), pos.z());
    }

    public double distToLowCornerSqr(double x, double z) {
        double xDist = this.x() - x;
        double zDist = this.z() - z;
        return xDist * xDist + zDist * zDist;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ColumnPos that = (ColumnPos) o;
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public String toString() {
        return "ColumnPos{" + "x=" + x + ", z=" + z + '}';
    }
}
