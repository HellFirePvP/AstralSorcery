/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RefreshFunction
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:43
 */
public interface RefreshFunction<T extends EntityComplexFX> {

    RefreshFunction<?> DESPAWN = fx -> false;

    public boolean shouldRefresh(@Nonnull T fx);

}
