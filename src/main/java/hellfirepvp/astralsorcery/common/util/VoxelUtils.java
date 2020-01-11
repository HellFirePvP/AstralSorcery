/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VoxelUtils
 * Created by HellFirePvP
 * Date: 20.07.2019 / 16:49
 */
public class VoxelUtils {

    public static VoxelShape combineAll(IBooleanFunction fct, VoxelShape... shapes) {
        return combineAll(fct, Arrays.asList(shapes));
    }

    public static VoxelShape combineAll(IBooleanFunction fct, List<VoxelShape> shapes) {
        if (shapes.isEmpty()) {
            return VoxelShapes.empty();
        }
        VoxelShape first = shapes.get(0);
        for (int i = 1; i < shapes.size(); i++) {
            first = VoxelShapes.combine(first, shapes.get(i), fct);
        }
        return first;
    }

}
