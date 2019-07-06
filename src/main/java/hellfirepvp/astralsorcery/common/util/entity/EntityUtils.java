/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.entity;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:26
 */
public class EntityUtils {

    public static void applyVortexMotion(Function<Void, Vector3> getPositionFunction, Function<Vector3, Object> addMotionFunction, Vector3 to, double vortexRange, double multiplier) {
        Vector3 pos = getPositionFunction.apply(null);
        double diffX = (to.getX() - pos.getX()) / vortexRange;
        double diffY = (to.getY() - pos.getY()) / vortexRange;
        double diffZ = (to.getZ() - pos.getZ()) / vortexRange;
        double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (1.0D - dist > 0.0D) {
            double dstFactorSq = (1.0D - dist) * (1.0D - dist);
            Vector3 toAdd = new Vector3();
            toAdd.setX(diffX / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setY(diffY / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setZ(diffZ / dist * dstFactorSq * 0.15D * multiplier);
            addMotionFunction.apply(toAdd);
        }
    }

    public static boolean canEntitySpawnHere(World world, BlockPos at, EntityType<? extends Entity> type, SpawnReason spawnReason, @Nullable Consumer<Entity> preCheckEntity) {
        EntitySpawnPlacementRegistry.PlacementType placementType = EntitySpawnPlacementRegistry.getPlacementType(type);
        if (!world.getWorldBorder().contains(at) || !placementType.canSpawnAt(world, at, type)) {
            return false;
        }
        if (!EntitySpawnPlacementRegistry.func_223515_a(type, world, spawnReason, at, world.rand)) {
            return false;
        }
        if (!world.areCollisionShapesEmpty(type.func_220328_a(at.getX() + 0.5, at.getY(), at.getZ() + 0.5))) {
            return false;
        }

        Entity entity = type.create(world);
        if(entity == null) {
            return false;
        }
        entity.setLocationAndAngles(at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
        if (preCheckEntity != null) {
            preCheckEntity.accept(entity);
        }

        if(entity instanceof LivingEntity) {
            if (entity instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) entity;
                Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(mobEntity, world, entity.posX, entity.posY, entity.posZ, null);
                if (canSpawn == Event.Result.DENY) {
                    return false;
                } else if (canSpawn == Event.Result.DEFAULT) {
                    if (!mobEntity.canSpawn(world, spawnReason) || !mobEntity.isNotColliding(world)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
