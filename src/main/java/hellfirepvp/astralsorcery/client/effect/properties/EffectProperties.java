/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.properties;

import net.minecraft.util.math.Vec3i;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectProperties
 * Created by HellFirePvP
 * Date: 30.05.2019 / 00:07
 */
public class EffectProperties {

    private UUID owner;
    private Vec3i position;
    private boolean ignoreLimit;

    public EffectProperties setOwner(UUID owner) {
        this.owner = owner;
        return this;
    }

    public EffectProperties setPosition(Vec3i position) {
        this.position = position;
        return this;
    }

    public EffectProperties setIgnoreLimit(boolean ignoreLimit) {
        this.ignoreLimit = ignoreLimit;
        return this;
    }

    public UUID getOwner() {
        return owner;
    }

    public Vec3i getPosition() {
        return position;
    }

    public boolean ignoresSpawnLimit() {
        return ignoreLimit;
    }
}
