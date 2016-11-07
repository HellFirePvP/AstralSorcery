package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.util.math.BlockPos;
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

    public World getWorld() {
        return world;
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
