package hellfirepvp.astralsorcery.common.ritual;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.ritual.constraints.RitualConstraint;
import hellfirepvp.astralsorcery.common.ritual.constraints.SizeConstraint;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RitualStructureCalculator
 * Created by HellFirePvP
 * Date: 09.08.2016 / 15:34
 */
@Deprecated
public class RitualStructureCalculator {

    @Deprecated
    public double getEnhancementByStructure(World world, BlockPos central, Constellation type, SizeConstraint maxSize, Collection<RitualConstraint> constraints) {
        int radSize = maxSize.getStructureSize() + 2;
        Map<BlockPos, Integer> offsetMap = new HashMap<>();
        for (int xx = -radSize; xx <= radSize; xx++) {
            for (int zz = -radSize; zz <= radSize; zz++) {
                for (int yy = -radSize; yy <= radSize; yy++) {
                    BlockPos offsetPos = new BlockPos(xx, yy, zz);
                    BlockPos realPos = central.add(offsetPos);
                    IBlockState offsetState = world.getBlockState(realPos);
                    Block b = offsetState.getBlock();
                    /*if(b instanceof IBlockRitualComponent) {
                        if(!((IBlockRitualComponent) b).isValidComponent(type, world, realPos)) continue;
                    } else {
                        if(!RitualComponentRegistry.isComponent(offsetState)) continue;
                    }
                    int meta = b.getMetaFromState(offsetState);
                    offsetMap.put(offsetPos, meta);*/
                }
            }
        }
        //always <= 0
        int sizeFulfillment = maxSize.getFulfillment(world, central, offsetMap, type);
        double fulfillment = 0;
        for (RitualConstraint constraint : constraints) {
            fulfillment += (((double) constraint.getFulfillment(world, central, offsetMap, type)) * constraint.getWeight());
        }
        return (sizeFulfillment < 0 ? sizeFulfillment : 1) * fulfillment;
    }

}
