/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.collision;

import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectAevitas;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapeSpliterator;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CollisionManager
 * Created by HellFirePvP
 * Date: 19.12.2020 / 14:20
 */
public class CollisionManager {

    private static final List<CustomCollisionHandler> customHandlers = new ArrayList<>();

    private static final int maxCacheSize = 20;
    private static final LinkedList<VoxelShapeSpliterator> accessList = new LinkedList<>();
    private static final Map<VoxelShapeSpliterator, List<AxisAlignedBB>> instanceFlags = new HashMap<>();

    public static void init() {
        register(new MantleEffectAevitas.PlayerWalkableAir());
    }

    public static void register(CustomCollisionHandler handler) {
        customHandlers.add(handler);
    }

    @Nullable
    public static AxisAlignedBB getIteratorBoundingBoxes(VoxelShapeSpliterator iterator, @Nullable Entity entity) {
        if (!instanceFlags.containsKey(iterator)) {
            List<AxisAlignedBB> additionalBoundingBoxes = getAdditionalBoundingBoxes(entity);
            if (additionalBoundingBoxes.isEmpty()) {
                return null;
            }
            removeOldestEntry();
            instanceFlags.put(iterator, additionalBoundingBoxes);
            accessList.addFirst(iterator);
        }
        List<AxisAlignedBB> boxes = instanceFlags.get(iterator);
        if (boxes == null || boxes.isEmpty()) {
            return null;
        }
        markActive(iterator);
        return boxes.remove(0);
    }

    public static boolean needsCustomCollision(@Nullable Entity entity) {
        for (CustomCollisionHandler handler : customHandlers) {
            if (handler.shouldAddCollisionFor(entity)) {
                return true;
            }
        }
        return false;
    }

    public static List<AxisAlignedBB> getAdditionalBoundingBoxes(@Nullable Entity entity) {
        List<AxisAlignedBB> additionalCollision = new ArrayList<>();
        AxisAlignedBB entityBox = entity != null ? entity.getBoundingBox() : new AxisAlignedBB(BlockPos.ZERO);
        customHandlers.stream()
                .filter(handler -> handler.shouldAddCollisionFor(entity))
                .forEach(handler -> handler.addCollision(entity, entityBox, additionalCollision));
        return additionalCollision;
    }

    private static void removeOldestEntry() {
        if (accessList.size() >= maxCacheSize) {
            VoxelShapeSpliterator oldest;
            //Apparently the list can be both >= 20 elements in size AND empty at the same time.
            try {
                oldest = accessList.removeLast();
            } catch (NoSuchElementException exc) {
                if (accessList.isEmpty()) {
                    return;
                }
                try {
                    oldest = accessList.get(accessList.size() - 1);
                } catch (Exception e) {
                    return;
                }
            }
            if (oldest != null) {
                instanceFlags.remove(oldest);
            }
        }
    }

    private static void markActive(VoxelShapeSpliterator it) {
        if (accessList.remove(it)) {
            accessList.addFirst(it);
        }
    }
}
