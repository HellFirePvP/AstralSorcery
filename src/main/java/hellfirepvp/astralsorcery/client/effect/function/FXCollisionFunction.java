/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXCollisionFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXCollisionFunction<T extends EntityFX> {

    FXCollisionFunction NO_COLLISION = (fx, motion) -> motion;

    FXCollisionFunction REMOVE_ON_COLLISION = (fx, motion) -> {
        Level level = Minecraft.getInstance().level;
        if (level == null) return motion;
        AABB posBox = new AABB(fx.getPos().toVector3d(), fx.getPos().toVector3d());
        if (level.getBlockCollisions(null, posBox).iterator().hasNext()) {
            fx.requestRemoval();
        }
        return motion;
    };

    FXCollisionFunction COLLIDE_WITH_BLOCKS = (fx, motion) -> {
        Level level = Minecraft.getInstance().level;
        if (level == null) return motion;
        AABB posBox = new AABB(fx.getPos().toVector3d(), fx.getPos().toVector3d());
        return new Vector3(Entity.collideBoundingBox(null, motion.toVector3d(), posBox, level, List.of()));
    };
    FXCollisionFunction COLLIDE_WITH_BLOCKS_AND_ENTITIES = (fx, motion) -> {
        Level level = Minecraft.getInstance().level;
        if (level == null) return motion;
        AABB posBox = new AABB(fx.getPos().toVector3d(), fx.getPos().toVector3d());
        List<VoxelShape> entities = level.getEntityCollisions(null, posBox.expandTowards(motion.toVector3d()));
        return new Vector3(Entity.collideBoundingBox(null, motion.toVector3d(), posBox, level, entities));
    };

    Vector3 collide(T fx, Vector3 motion);

}
