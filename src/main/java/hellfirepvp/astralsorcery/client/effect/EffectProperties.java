/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.context.BatchRenderContext;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectProperties
 * Created by HellFirePvP
 * Date: 30.05.2019 / 00:07
 */
public class EffectProperties {

    private final BatchRenderContext ctx;

    private UUID owner = null;
    private Vec3i position = Vec3i.NULL_VECTOR;
    private boolean ignoreLimit = false;

    public EffectProperties(BatchRenderContext ctx) {
        this.ctx = ctx;
    }

    // What player this effects belongs to, if any.
    public EffectProperties setOwner(@Nullable UUID owner) {
        this.owner = owner;
        return this;
    }

    // The position the effect originates from, if any.
    public EffectProperties setPosition(@Nonnull Vec3i position) {
        this.position = position;
        return this;
    }

    // If this effect should ignore the spawn-limit soft cap on particles
    public EffectProperties setIgnoreLimit(boolean ignoreLimit) {
        this.ignoreLimit = ignoreLimit;
        return this;
    }

    public BatchRenderContext getContext() {
        return ctx;
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    @Nonnull
    public Vec3i getPosition() {
        return position;
    }

    public boolean ignoresSpawnLimit() {
        return ignoreLimit;
    }

    // Method in favour of not using the event system to apply special effects.
    public void applySpecialEffects(IComplexEffect effect) {
        
    }
}
