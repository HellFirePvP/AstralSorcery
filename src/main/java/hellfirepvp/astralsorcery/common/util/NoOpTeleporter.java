/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NoOpTeleporter
 * Created by HellFirePvP
 * Date: 19.04.2017 / 14:37
 */
public class NoOpTeleporter implements ITeleporter {

    private final BlockPos targetPos;

    public NoOpTeleporter(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        entity.moveToBlockPosAndAngles(targetPos, yaw, entity.rotationPitch);
    }
}
