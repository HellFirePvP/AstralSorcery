/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NoOpTeleporter
 * Created by HellFirePvP
 * Date: 19.04.2017 / 14:37
 */
public class NoOpTeleporter extends Teleporter {

    private final BlockPos targetPos;

    public NoOpTeleporter(ServerWorld worldIn, BlockPos targetPos) {
        super(worldIn);
        this.targetPos = targetPos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity created = repositionEntity.apply(false);
        created.setPositionAndUpdate(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        return created;
    }
}
