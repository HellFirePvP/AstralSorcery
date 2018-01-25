/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldBlockPos
 * Created by HellFirePvP
 * Date: 07.11.2016 / 11:47
 */
public class WorldBlockPos extends BlockPos {

    private final World world;

    public WorldBlockPos(World world, BlockPos pos) {
        super(pos);
        this.world = world;
    }

    public WorldBlockPos(TileEntity te) {
        this(te.getWorld(), te.getPos());
    }

    public static WorldBlockPos wrap(World world, BlockPos pos) {
        return new WorldBlockPos(world, pos);
    }

    public World getWorld() {
        return world;
    }

    public IBlockState getStateAt() {
        return world.getBlockState(this);
    }

    @Override
    public WorldBlockPos add(int x, int y, int z) {
        return wrap(world, super.add(x, y, z));
    }

    @Override
    public WorldBlockPos add(double x, double y, double z) {
        return wrap(world, super.add(x, y, z));
    }

    @Override
    public WorldBlockPos add(Vec3i vec) {
        return wrap(world, super.add(vec));
    }

    public <T extends TileEntity> T getTileAt(Class<T> tileClass, boolean forceChunkLoad) {
        return MiscUtils.getTileAt(world, this, tileClass, forceChunkLoad);
    }

    public boolean isChunkLoaded() {
        return MiscUtils.isChunkLoaded(world, new ChunkPos(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WorldBlockPos that = (WorldBlockPos) o;
        return !(world != null ? !world.equals(that.world) : that.world != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (world != null ? world.hashCode() : 0);
        return result;
    }
}
