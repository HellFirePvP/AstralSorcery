/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.collision;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomCollisionHandler
 * Created by HellFirePvP
 * Date: 19.12.2020 / 14:21
 */
public interface CustomCollisionHandler {

    boolean shouldAddCollisionFor(Entity entity);

    void addCollision(Entity entity, AxisAlignedBB testBox, List<AxisAlignedBB> additionalCollision);

}
