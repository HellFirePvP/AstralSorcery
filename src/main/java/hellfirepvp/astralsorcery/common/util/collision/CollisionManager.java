/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.collision;

import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectAevitas;
import hellfirepvp.astralsorcery.common.util.data.SizeLimitMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapeSpliterator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CollisionManager
 * Created by HellFirePvP
 * Date: 19.12.2020 / 14:20
 */
public class CollisionManager {

    private static final List<CustomCollisionHandler> customHandlers = new ArrayList<>();

    //This should be implemented properly at some point, instead of... this.
    private static final LinkedHashMap<VoxelShapeSpliterator, List<AxisAlignedBB>> instanceFlags = new SizeLimitMap<>(20);

    public static void init() {
        register(new MantleEffectAevitas.PlayerWalkableAir());
    }

    public static void register(CustomCollisionHandler handler) {
        customHandlers.add(handler);
    }

    @Nullable
    public static AxisAlignedBB getIteratorBoundingBoxes(VoxelShapeSpliterator iterator, @Nullable Entity entity) {
        if (!instanceFlags.containsKey(iterator)) {
            instanceFlags.put(iterator, getAdditionalBoundingBoxes(entity));
        }
        List<AxisAlignedBB> boxes = instanceFlags.get(iterator);
        if (boxes == null || boxes.isEmpty()) {
            return null;
        }
        return boxes.remove(0);
    }

    public static List<AxisAlignedBB> getAdditionalBoundingBoxes(@Nullable Entity entity) {
        List<AxisAlignedBB> additionalCollision = new ArrayList<>();
        AxisAlignedBB entityBox = entity != null ? entity.getBoundingBox() : new AxisAlignedBB(BlockPos.ZERO);
        customHandlers.stream()
                .filter(handler -> handler.shouldAddCollisionFor(entity))
                .forEach(handler -> handler.addCollision(entity, entityBox, additionalCollision));
        return additionalCollision;
    }
}
