/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntitySourceFX
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class EntitySourceFX extends EntityFX {

    public EntitySourceFX(Vector3 pos) {
        super(pos);
    }

    public abstract void tickSpawnFX();
}
