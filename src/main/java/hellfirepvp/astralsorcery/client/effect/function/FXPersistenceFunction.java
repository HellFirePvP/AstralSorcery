/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityFX;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXPersistenceFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXPersistenceFunction<T extends EntityFX> {

    FXPersistenceFunction<?> RENDER_DISTANCE = (fx, entity) -> {
        float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
        return fx.getPos().distanceSquared(entity) <= (renderDistance * renderDistance);
    };

    FXPersistenceFunction<?> ALWAYS_PERSIST = (fx, entity) -> true;

    boolean canEffectPersist(T fx, Entity viewEntity);

}
