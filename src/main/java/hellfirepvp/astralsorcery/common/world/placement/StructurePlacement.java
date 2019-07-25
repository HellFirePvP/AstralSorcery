/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import com.mojang.datafixers.Dynamic;
import hellfirepvp.astralsorcery.common.world.placement.config.EvenStructurePlacementConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructurePlacement
 * Created by HellFirePvP
 * Date: 23.07.2019 / 07:34
 */
public abstract class StructurePlacement<DC extends EvenStructurePlacementConfig> extends Placement<DC> {

    public StructurePlacement(Function<Dynamic<?>, ? extends DC> cfgFactory) {
        super(cfgFactory);
    }

    protected BlockPos getApplicablePosition(IWorld world, BlockPos pos) {
        return world.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos);
    }

    @Nullable
    protected BlockPos getStructurePlacement(IWorld world, BlockPos centre, int structSize) {
        centre = world.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, centre);
        int diff = structSize / 3;
        BlockPos offset;
        if ((offset = this.getApplicablePosition(world, centre.add(structSize, 0, structSize))) == null || Math.abs(offset.getY() - centre.getY()) > diff) {
            return null;
        }
        if ((offset = this.getApplicablePosition(world, centre.add(-structSize, 0, structSize))) == null || Math.abs(offset.getY() - centre.getY()) > diff) {
            return null;
        }
        if ((offset = this.getApplicablePosition(world, centre.add(-structSize, 0, -structSize))) == null || Math.abs(offset.getY() - centre.getY()) > diff) {
            return null;
        }
        if ((offset = this.getApplicablePosition(world, centre.add(structSize, 0, -structSize))) == null || Math.abs(offset.getY() - centre.getY()) > diff) {
            return null;
        }
        return centre;
    }
}
