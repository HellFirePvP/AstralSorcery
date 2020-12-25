/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.collision;

import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeSpliterator;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CollisionHelper
 * Created by HellFirePvP
 * Date: 19.12.2020 / 10:00
 */
public class CollisionHelper {

    public static boolean onCollision(VoxelShapeSpliterator iterator, Consumer<? super VoxelShape> action) {
        if (!CollisionManager.needsCustomCollision(iterator.entity)) {
            return false;
        }
        AxisAlignedBB box = CollisionManager.getIteratorBoundingBoxes(iterator, iterator.entity);
        if (box == null) {
            return false;
        }

        VoxelShape floor = VoxelShapes.create(box);
        if (VoxelShapes.compare(floor, VoxelShapes.create(iterator.aabb.grow(1.0E-7D)), IBooleanFunction.AND)) {
            action.accept(floor);
            return true;
        }
        return false;
    }

    public static Vector3d onEntityCollision(Vector3d allowedMovement, Entity entity) {
        if (!CollisionManager.needsCustomCollision(entity)) {
            return allowedMovement;
        }
        List<AxisAlignedBB> additionalBoxes = CollisionManager.getAdditionalBoundingBoxes(entity);
        AxisAlignedBB entityBox = entity.getBoundingBox().grow(1.0E-7D);
        for (AxisAlignedBB box : additionalBoxes) {
            double newYMovement = VoxelShapes.create(box).getAllowedOffset(Direction.Axis.Y, entityBox, allowedMovement.y);
            allowedMovement = new Vector3d(allowedMovement.x, newYMovement, allowedMovement.z);
        }

        return allowedMovement;
    }
}
