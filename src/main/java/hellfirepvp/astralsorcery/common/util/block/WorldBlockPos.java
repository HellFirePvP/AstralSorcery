/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.object.TransformReference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldBlockPos
 * Created by HellFirePvP
 * Date: 07.11.2016 / 11:47
 */
public class WorldBlockPos extends BlockPos {

    private final TransformReference<DimensionType, World> worldReference;

    private WorldBlockPos(TransformReference<DimensionType, World> worldReference, BlockPos pos) {
        super(pos);
        this.worldReference = worldReference;
    }

    private WorldBlockPos(DimensionType type, BlockPos pos, Function<DimensionType, World> worldProvider) {
        super(pos);
        this.worldReference = new TransformReference<>(type, worldProvider);
    }

    public static WorldBlockPos wrapServer(World world, BlockPos pos) {
        return new WorldBlockPos(world.getDimension().getType(), pos, type -> {
            MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
            return DimensionManager.getWorld(server, type, true, false);
        });
    }

    public static WorldBlockPos wrapTileEntity(TileEntity tile) {
        return new WorldBlockPos(tile.getWorld().getDimension().getType(), tile.getPos(), type -> tile.getWorld());
    }

    public DimensionType getDimensionType() {
        return this.worldReference.getReference();
    }

    private WorldBlockPos wrapInternal(BlockPos pos) {
        return new WorldBlockPos(this.worldReference, pos);
    }

    @Override
    public WorldBlockPos add(int x, int y, int z) {
        return wrapInternal(super.add(x, y, z));
    }

    @Override
    public WorldBlockPos add(double x, double y, double z) {
        return wrapInternal(super.add(x, y, z));
    }

    @Override
    public WorldBlockPos add(Vec3i vec) {
        return wrapInternal(super.add(vec));
    }

    @Nullable
    public <T extends TileEntity> T getTileAt(Class<T> tileClass, boolean forceChunkLoad) {
        World world = this.worldReference.getValue();
        if (world != null) {
            return MiscUtils.getTileAt(world, this, tileClass, forceChunkLoad);
        }
        return null;
    }

    @Nullable
    public World getWorld() {
        return this.worldReference.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WorldBlockPos that = (WorldBlockPos) o;
        return Objects.equals(getDimensionType(), that.getDimensionType());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getDimensionType().hashCode();
        return result;
    }
}
